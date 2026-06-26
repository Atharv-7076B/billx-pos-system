import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import toast from 'react-hot-toast'
import { Plus, Pencil, Trash2, Warehouse, AlertTriangle, Loader2 } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { inventoryService } from '../services/inventoryService'
import { branchService } from '../services/branchService'
import { productService } from '../services/productService'
import { InventoryDto } from '../types'
import PageHeader from '../components/common/PageHeader'
import Modal from '../components/ui/Modal'
import DataTable, { Column } from '../components/tables/DataTable'
import LoadingSpinner from '../components/common/LoadingSpinner'
import { formatDateTime } from '../utils/formatters'

const schema = z.object({
  productId: z.coerce.number().min(1, 'Product required'),
  branchId:  z.coerce.number().min(1, 'Branch required'),
  quantity:  z.coerce.number().min(0, 'Quantity must be ≥ 0'),
})
type FormData = z.infer<typeof schema>

export default function InventoryPage() {
  const { storeId } = useAuth()
  const qc = useQueryClient()
  const [modalOpen, setModalOpen] = useState(false)
  const [editItem, setEditItem] = useState<InventoryDto | null>(null)
  const [selectedBranch, setSelectedBranch] = useState<number | null>(null)

  const { data: branches = [] } = useQuery({
    queryKey: ['branches', storeId], queryFn: () => branchService.getByStore(storeId!), enabled: !!storeId,
  })
  const { data: products = [] } = useQuery({
    queryKey: ['products', storeId], queryFn: () => productService.getByStore(storeId!), enabled: !!storeId,
  })
  const activeBranchId = selectedBranch ?? branches[0]?.id
  const { data: inventory = [], isLoading } = useQuery({
    queryKey: ['inventory', activeBranchId], queryFn: () => inventoryService.getByBranch(activeBranchId!), enabled: !!activeBranchId,
  })

  const { register, handleSubmit, reset, formState: { errors, isSubmitting } } = useForm<FormData>({ resolver: zodResolver(schema) })

  const createMut = useMutation({
    mutationFn: (d: FormData) => inventoryService.create(d),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['inventory'] }); toast.success('Created'); closeModal() },
    onError: (e: any) => toast.error(e.response?.data?.message ?? 'Error'),
  })
  const updateMut = useMutation({
    mutationFn: (d: FormData) => inventoryService.update(editItem!.id!, { quantity: d.quantity }),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['inventory'] }); toast.success('Updated'); closeModal() },
    onError: (e: any) => toast.error(e.response?.data?.message ?? 'Error'),
  })
  const deleteMut = useMutation({
    mutationFn: (id: number) => inventoryService.delete(id),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['inventory'] }); toast.success('Deleted') },
    onError: (e: any) => toast.error(e.response?.data?.message ?? 'Error'),
  })

  const openCreate = () => { reset({ branchId: activeBranchId }); setEditItem(null); setModalOpen(true) }
  const openEdit = (item: InventoryDto) => {
    setEditItem(item)
    reset({ productId: item.productId, branchId: item.branchId, quantity: item.quantity })
    setModalOpen(true)
  }
  const closeModal = () => { setModalOpen(false); setEditItem(null) }
  const onSubmit = (d: FormData) => editItem ? updateMut.mutate(d) : createMut.mutate(d)

  const columns: Column<InventoryDto>[] = [
    { key: 'product', label: 'Product', render: r => (
      <div><p className="font-medium text-gray-800">{(r.product as any)?.name ?? `Product #${r.productId}`}</p>
      <p className="text-xs text-gray-400">{(r.product as any)?.sku ?? ''}</p></div>
    )},
    { key: 'quantity', label: 'Stock', render: r => (
      <div className="flex items-center gap-2">
        {r.quantity <= 10 && <AlertTriangle size={14} className="text-amber-500" />}
        <span className={`font-bold text-lg ${r.quantity === 0 ? 'text-red-600' : r.quantity <= 10 ? 'text-amber-600' : 'text-gray-800'}`}>{r.quantity}</span>
      </div>
    )},
    { key: 'lastUpdate', label: 'Last Updated', render: r => <span className="text-gray-400 text-xs">{formatDateTime(r.lastUpdate)}</span> },
  ]

  if (isLoading && activeBranchId) return <LoadingSpinner />

  return (
    <div>
      <PageHeader title="Inventory" subtitle="Track stock levels across branches"
        action={<button onClick={openCreate} className="btn-primary"><Plus size={16}/> Add Stock</button>} />

      {branches.length > 1 && (
        <div className="flex gap-2 mb-4 flex-wrap">
          {branches.map(b => (
            <button key={b.id} onClick={() => setSelectedBranch(b.id!)}
              className={`px-3 py-1.5 rounded-lg text-sm font-medium transition-colors ${activeBranchId === b.id ? 'bg-primary-600 text-white' : 'bg-white border text-gray-600 hover:bg-gray-50'}`}>
              {b.name}
            </button>
          ))}
        </div>
      )}

      <DataTable data={inventory as any[]} columns={columns as any} searchable
        actions={(item: InventoryDto) => (
          <div className="flex gap-1 justify-end">
            <button onClick={() => openEdit(item)} className="p-1.5 text-gray-400 hover:text-primary-600 hover:bg-primary-50 rounded-lg"><Pencil size={15}/></button>
            <button onClick={() => confirm('Remove?') && deleteMut.mutate(item.id!)} className="p-1.5 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg"><Trash2 size={15}/></button>
          </div>
        )} />

      <Modal open={modalOpen} onClose={closeModal} title={editItem ? 'Update Stock' : 'Add to Inventory'}>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          {!editItem && (
            <>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Product*</label>
                <select {...register('productId')} className="input">
                  <option value="">Select product</option>
                  {products.map(p => <option key={p.id} value={p.id}>{p.name} ({p.sku})</option>)}
                </select>
                {errors.productId && <p className="text-xs text-red-500 mt-1">{errors.productId.message}</p>}
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Branch*</label>
                <select {...register('branchId')} className="input">
                  <option value="">Select branch</option>
                  {branches.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
                </select>
              </div>
            </>
          )}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Quantity*</label>
            <input {...register('quantity')} type="number" className="input" placeholder="0" />
            {errors.quantity && <p className="text-xs text-red-500 mt-1">{errors.quantity.message}</p>}
          </div>
          <div className="flex gap-3">
            <button type="button" onClick={closeModal} className="btn-secondary flex-1">Cancel</button>
            <button type="submit" disabled={isSubmitting} className="btn-primary flex-1 justify-center">
              {isSubmitting ? <Loader2 size={14} className="animate-spin"/> : editItem ? 'Update' : 'Add'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  )
}
