import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import toast from 'react-hot-toast'
import { Plus, Pencil, Trash2, Users, Loader2 } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { customerService } from '../services/customerService'
import { CustomerDto } from '../types'
import PageHeader from '../components/common/PageHeader'
import Modal from '../components/ui/Modal'
import DataTable, { Column } from '../components/tables/DataTable'
import LoadingSpinner from '../components/common/LoadingSpinner'
import { formatCurrency, formatDate } from '../utils/formatters'

const schema = z.object({
  name: z.string().min(1, 'Name required'),
  email: z.string().email().optional().or(z.literal('')),
  phone: z.string().optional(),
  address: z.string().optional(),
})
type FormData = z.infer<typeof schema>

export default function CustomersPage() {
  const { storeId } = useAuth()
  const qc = useQueryClient()
  const [modalOpen, setModalOpen] = useState(false)
  const [editItem, setEditItem] = useState<CustomerDto | null>(null)

  const { data: customers = [], isLoading } = useQuery({
    queryKey: ['customers', storeId],
    queryFn: () => customerService.getByStore(storeId!),
    enabled: !!storeId,
  })

  const { register, handleSubmit, reset, formState: { errors, isSubmitting } } = useForm<FormData>({ resolver: zodResolver(schema) })

  const createMut = useMutation({
    mutationFn: (d: FormData) => customerService.create({ ...d, storeId: storeId! }),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['customers'] }); toast.success('Customer added'); closeModal() },
    onError: (e: any) => toast.error(e.response?.data?.message ?? 'Error'),
  })
  const updateMut = useMutation({
    mutationFn: (d: FormData) => customerService.update(editItem!.id!, { ...d, storeId: storeId! }),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['customers'] }); toast.success('Updated'); closeModal() },
    onError: (e: any) => toast.error(e.response?.data?.message ?? 'Error'),
  })
  const deleteMut = useMutation({
    mutationFn: (id: number) => customerService.delete(id),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['customers'] }); toast.success('Deleted') },
    onError: (e: any) => toast.error(e.response?.data?.message ?? 'Error'),
  })

  const openCreate = () => { reset(); setEditItem(null); setModalOpen(true) }
  const openEdit = (c: CustomerDto) => { setEditItem(c); reset({ name: c.name, email: c.email, phone: c.phone, address: c.address }); setModalOpen(true) }
  const closeModal = () => { setModalOpen(false); setEditItem(null) }
  const onSubmit = (d: FormData) => editItem ? updateMut.mutate(d) : createMut.mutate(d)

  const columns: Column<CustomerDto>[] = [
    { key: 'name', label: 'Customer', render: r => (
      <div className="flex items-center gap-3">
        <div className="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center text-primary-700 font-bold text-sm shrink-0">
          {r.name[0].toUpperCase()}
        </div>
        <div><p className="font-medium text-gray-800">{r.name}</p><p className="text-xs text-gray-400">{r.email ?? '—'}</p></div>
      </div>
    )},
    { key: 'phone',          label: 'Phone',    render: r => <span className="text-gray-600">{r.phone ?? '—'}</span> },
    { key: 'totalOrders',    label: 'Orders',   render: r => <span className="font-medium">{r.totalOrders ?? 0}</span> },
    { key: 'totalPurchases', label: 'Spent',    render: r => <span className="font-semibold text-gray-900">{formatCurrency(r.totalPurchases ?? 0)}</span> },
    { key: 'createdAt',      label: 'Joined',   render: r => <span className="text-gray-500">{formatDate(r.createdAt)}</span> },
  ]

  if (isLoading) return <LoadingSpinner />

  return (
    <div>
      <PageHeader title="Customers" subtitle={`${customers.length} customers`}
        action={<button onClick={openCreate} className="btn-primary"><Plus size={16}/> Add Customer</button>} />

      <DataTable data={customers as any[]} columns={columns as any} searchable searchPlaceholder="Search customers…"
        actions={(c: CustomerDto) => (
          <div className="flex gap-1 justify-end">
            <button onClick={() => openEdit(c)} className="p-1.5 text-gray-400 hover:text-primary-600 hover:bg-primary-50 rounded-lg"><Pencil size={15}/></button>
            <button onClick={() => confirm('Delete?') && deleteMut.mutate(c.id!)} className="p-1.5 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg"><Trash2 size={15}/></button>
          </div>
        )} />

      <Modal open={modalOpen} onClose={closeModal} title={editItem ? 'Edit Customer' : 'Add Customer'}>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Name*</label>
            <input {...register('name')} className="input" placeholder="Full name" />
            {errors.name && <p className="text-xs text-red-500 mt-1">{errors.name.message}</p>}
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
            <input {...register('email')} type="email" className="input" placeholder="customer@example.com" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Phone</label>
            <input {...register('phone')} className="input" placeholder="+91 98765 43210" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Address</label>
            <textarea {...register('address')} className="input" rows={2} placeholder="Optional address" />
          </div>
          <div className="flex gap-3">
            <button type="button" onClick={closeModal} className="btn-secondary flex-1">Cancel</button>
            <button type="submit" disabled={isSubmitting} className="btn-primary flex-1 justify-center">
              {isSubmitting ? <Loader2 size={14} className="animate-spin"/> : editItem ? 'Update' : 'Add Customer'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  )
}
