import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import toast from 'react-hot-toast'
import { Plus, Pencil, Trash2, Package, Loader2 } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { productService } from '../services/productService'
import { categoryService } from '../services/categoryService'
import { ProductDto } from '../types'
import PageHeader from '../components/common/PageHeader'
import Modal from '../components/ui/Modal'
import DataTable from '../components/tables/DataTable'
import LoadingSpinner from '../components/common/LoadingSpinner'
import { formatCurrency, generateSKU } from '../utils/formatters'
import { storage } from '../utils/storage'

const schema = z.object({
  name: z.string().min(1, 'Name required'),
  sku: z.string().min(1, 'SKU required'),
  description: z.string().optional(),
  mrp: z.coerce.number().min(0),
  sellingPrice: z.coerce.number().min(0),
  brand: z.string().optional(),
  categoryId: z.coerce.number().min(1, 'Category required'),
})
type FormData = z.infer<typeof schema>

export default function ProductsPage() {
  const { storeId } = useAuth()
  const token = storage.getToken() ?? ''
  const qc = useQueryClient()
  const [modalOpen, setModalOpen] = useState(false)
  const [editItem, setEditItem] = useState<ProductDto | null>(null)

  const { data: products = [], isLoading } = useQuery({
    queryKey: ['products', storeId],
    queryFn: () => productService.getByStore(storeId!),
    enabled: !!storeId,
  })

  const { data: categories = [] } = useQuery({
    queryKey: ['categories', storeId],
    queryFn: () => categoryService.getByStore(storeId!),
    enabled: !!storeId,
  })

  const { register, handleSubmit, reset, setValue, formState: { errors, isSubmitting } } = useForm<FormData>({
    resolver: zodResolver(schema),
  })

  const createMut = useMutation({
    mutationFn: (d: FormData) => productService.create(storeId!, d.categoryId, { ...d, storeId: storeId! }),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['products'] }); toast.success('Product created'); closeModal() },
    onError: (e: any) => toast.error(e.response?.data?.message ?? 'Error'),
  })

  const updateMut = useMutation({
    mutationFn: (d: FormData) => productService.update(editItem!.id!, { ...d, storeId: storeId! }, token),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['products'] }); toast.success('Product updated'); closeModal() },
    onError: (e: any) => toast.error(e.response?.data?.message ?? 'Error'),
  })

  const deleteMut = useMutation({
    mutationFn: (id: number) => productService.delete(id, token),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['products'] }); toast.success('Deleted') },
    onError: (e: any) => toast.error(e.response?.data?.message ?? 'Error'),
  })

  const openCreate = () => { reset({ sku: generateSKU('PRD') }); setEditItem(null); setModalOpen(true) }
  const openEdit = (p: ProductDto) => {
    setEditItem(p)
    reset({ name: p.name, sku: p.sku, description: p.description, mrp: p.mrp, sellingPrice: p.sellingPrice, brand: p.brand, categoryId: p.category?.id })
    setModalOpen(true)
  }
  const closeModal = () => { setModalOpen(false); setEditItem(null) }
  const onSubmit = (d: FormData) => editItem ? updateMut.mutate(d) : createMut.mutate(d)

  const columns = [
    { key: 'name',         label: 'Product',  render: (r: ProductDto) => (
      <div className="flex items-center gap-3">
        <div className="w-9 h-9 rounded-lg bg-primary-50 flex items-center justify-center shrink-0">
          <Package size={16} className="text-primary-600" />
        </div>
        <div><p className="font-medium text-gray-800">{r.name}</p><p className="text-xs text-gray-400">{r.sku}</p></div>
      </div>
    )},
    { key: 'brand',        label: 'Brand',     render: (r: ProductDto) => <span className="text-gray-600">{r.brand ?? '—'}</span> },
    { key: 'category',     label: 'Category',  render: (r: ProductDto) => <span className="badge-blue">{r.category?.name ?? '—'}</span> },
    { key: 'sellingPrice', label: 'Price',     render: (r: ProductDto) => <span className="font-semibold text-gray-900">{formatCurrency(r.sellingPrice)}</span> },
    { key: 'mrp',          label: 'MRP',       render: (r: ProductDto) => <span className="text-gray-400 line-through text-xs">{formatCurrency(r.mrp)}</span> },
  ]

  if (isLoading) return <LoadingSpinner />

  return (
    <div>
      <PageHeader title="Products" subtitle={`${products.length} total`}
        action={<button onClick={openCreate} className="btn-primary"><Plus size={16}/> Add Product</button>} />

      <DataTable data={products as any[]} columns={columns as any} searchable searchPlaceholder="Search products…"
        actions={(p: ProductDto) => (
          <div className="flex items-center gap-1 justify-end">
            <button onClick={() => openEdit(p)} className="p-1.5 text-gray-400 hover:text-primary-600 hover:bg-primary-50 rounded-lg transition-colors"><Pencil size={15}/></button>
            <button onClick={() => confirm('Delete this product?') && deleteMut.mutate(p.id!)}
              className="p-1.5 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"><Trash2 size={15}/></button>
          </div>
        )} />

      <Modal open={modalOpen} onClose={closeModal} title={editItem ? 'Edit Product' : 'Add Product'} size="lg">
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div className="col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">Product Name*</label>
              <input {...register('name')} className="input" placeholder="e.g. Premium Coffee" />
              {errors.name && <p className="text-xs text-red-500 mt-1">{errors.name.message}</p>}
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">SKU*</label>
              <input {...register('sku')} className="input" placeholder="PRD-0001" />
              {errors.sku && <p className="text-xs text-red-500 mt-1">{errors.sku.message}</p>}
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Brand</label>
              <input {...register('brand')} className="input" placeholder="e.g. Nescafé" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">MRP (₹)*</label>
              <input {...register('mrp')} type="number" step="0.01" className="input" placeholder="0.00" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Selling Price (₹)*</label>
              <input {...register('sellingPrice')} type="number" step="0.01" className="input" placeholder="0.00" />
            </div>
            <div className="col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">Category*</label>
              <select {...register('categoryId')} className="input">
                <option value="">Select category</option>
                {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
              </select>
              {errors.categoryId && <p className="text-xs text-red-500 mt-1">{errors.categoryId.message}</p>}
            </div>
            <div className="col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
              <textarea {...register('description')} className="input" rows={2} placeholder="Optional description" />
            </div>
          </div>
          <div className="flex gap-3 pt-2">
            <button type="button" onClick={closeModal} className="btn-secondary flex-1">Cancel</button>
            <button type="submit" disabled={isSubmitting} className="btn-primary flex-1 justify-center">
              {isSubmitting ? <Loader2 size={16} className="animate-spin"/> : editItem ? 'Update' : 'Create'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  )
}
