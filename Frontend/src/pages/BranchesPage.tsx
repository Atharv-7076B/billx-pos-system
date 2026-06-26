import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import toast from 'react-hot-toast'
import { Plus, Pencil, Trash2, GitBranch, MapPin, Loader2 } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { branchService } from '../services/branchService'
import { BranchDto } from '../types'
import PageHeader from '../components/common/PageHeader'
import Modal from '../components/ui/Modal'
import LoadingSpinner from '../components/common/LoadingSpinner'

const schema = z.object({
  name: z.string().min(1, 'Name required'),
  address: z.string().optional(),
  phone: z.string().optional(),
  email: z.string().email().optional().or(z.literal('')),
})
type FormData = z.infer<typeof schema>

export default function BranchesPage() {
  const { storeId } = useAuth()
  const qc = useQueryClient()
  const [modalOpen, setModalOpen] = useState(false)
  const [editItem, setEditItem] = useState<BranchDto | null>(null)

  const { data: branches = [], isLoading } = useQuery({
    queryKey: ['branches', storeId], queryFn: () => branchService.getByStore(storeId!), enabled: !!storeId,
  })

  const { register, handleSubmit, reset, formState: { errors, isSubmitting } } = useForm<FormData>({ resolver: zodResolver(schema) })

  const createMut = useMutation({
    mutationFn: (d: FormData) => branchService.create({ ...d, storeId: storeId! }),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['branches'] }); toast.success('Branch created'); closeModal() },
    onError: (e: any) => toast.error(e.response?.data?.message ?? 'Error'),
  })
  const updateMut = useMutation({
    mutationFn: (d: FormData) => branchService.update(editItem!.id!, { ...d, storeId: storeId! }),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['branches'] }); toast.success('Updated'); closeModal() },
    onError: (e: any) => toast.error(e.response?.data?.message ?? 'Error'),
  })
  const deleteMut = useMutation({
    mutationFn: (id: number) => branchService.delete(id),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['branches'] }); toast.success('Deleted') },
    onError: (e: any) => toast.error(e.response?.data?.message ?? 'Error'),
  })

  const openCreate = () => { reset(); setEditItem(null); setModalOpen(true) }
  const openEdit = (b: BranchDto) => { setEditItem(b); reset({ name: b.name, address: b.address, phone: b.phone, email: b.email }); setModalOpen(true) }
  const closeModal = () => { setModalOpen(false); setEditItem(null) }
  const onSubmit = (d: FormData) => editItem ? updateMut.mutate(d) : createMut.mutate(d)

  if (isLoading) return <LoadingSpinner />

  return (
    <div>
      <PageHeader title="Branches" subtitle={`${branches.length} branch${branches.length !== 1 ? 'es' : ''}`}
        action={<button onClick={openCreate} className="btn-primary"><Plus size={16}/> Add Branch</button>} />

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {branches.map(b => (
          <div key={b.id} className="card group hover:border-primary-200 transition-all border">
            <div className="flex items-start justify-between mb-4">
              <div className="w-10 h-10 rounded-xl bg-primary-50 flex items-center justify-center">
                <GitBranch size={20} className="text-primary-600" />
              </div>
              <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                <button onClick={() => openEdit(b)} className="p-1.5 text-gray-400 hover:text-primary-600 hover:bg-primary-50 rounded-lg"><Pencil size={14}/></button>
                <button onClick={() => confirm('Delete?') && deleteMut.mutate(b.id!)} className="p-1.5 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded-lg"><Trash2 size={14}/></button>
              </div>
            </div>
            <h3 className="font-semibold text-gray-800 mb-2">{b.name}</h3>
            {b.address && <div className="flex items-center gap-1.5 text-sm text-gray-500"><MapPin size={13}/>{b.address}</div>}
            {b.phone && <p className="text-sm text-gray-500 mt-1">{b.phone}</p>}
            {b.manager && <p className="text-xs text-primary-600 mt-2 font-medium">Manager: {b.manager.fullName}</p>}
          </div>
        ))}
        {branches.length === 0 && (
          <div className="col-span-full text-center py-16 text-gray-400">
            <GitBranch size={40} className="mx-auto mb-3 opacity-30" />
            <p>No branches yet</p>
          </div>
        )}
      </div>

      <Modal open={modalOpen} onClose={closeModal} title={editItem ? 'Edit Branch' : 'Add Branch'}>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Branch Name*</label>
            <input {...register('name')} className="input" placeholder="e.g. Main Branch" />
            {errors.name && <p className="text-xs text-red-500 mt-1">{errors.name.message}</p>}
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Address</label>
            <input {...register('address')} className="input" placeholder="Street, City" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Phone</label>
            <input {...register('phone')} className="input" placeholder="+91 98765 43210" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
            <input {...register('email')} type="email" className="input" placeholder="branch@store.com" />
          </div>
          <div className="flex gap-3">
            <button type="button" onClick={closeModal} className="btn-secondary flex-1">Cancel</button>
            <button type="submit" disabled={isSubmitting} className="btn-primary flex-1 justify-center">
              {isSubmitting ? <Loader2 size={14} className="animate-spin"/> : editItem ? 'Update' : 'Create'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  )
}
