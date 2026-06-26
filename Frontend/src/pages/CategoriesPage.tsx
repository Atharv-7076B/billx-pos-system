import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'
import toast from 'react-hot-toast'
import { Plus, Pencil, Trash2, Tag, Loader2 } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { categoryService } from '../services/categoryService'
import { CategoryDto } from '../types'
import PageHeader from '../components/common/PageHeader'
import Modal from '../components/ui/Modal'
import LoadingSpinner from '../components/common/LoadingSpinner'

const schema = z.object({ name: z.string().min(1, 'Name required') })
type FormData = z.infer<typeof schema>

export default function CategoriesPage() {
  const { storeId } = useAuth()
  const qc = useQueryClient()
  const [modalOpen, setModalOpen] = useState(false)
  const [editItem, setEditItem] = useState<CategoryDto | null>(null)

  const { data: categories = [], isLoading } = useQuery({
    queryKey: ['categories', storeId],
    queryFn: () => categoryService.getByStore(storeId!),
    enabled: !!storeId,
  })

  const { register, handleSubmit, reset, formState: { errors, isSubmitting } } = useForm<FormData>({ resolver: zodResolver(schema) })

  const createMut = useMutation({
    mutationFn: (d: FormData) => categoryService.create({ ...d, storeId: storeId! }),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['categories'] }); toast.success('Category created'); closeModal() },
    onError: (e: any) => toast.error(e.response?.data?.message ?? 'Error'),
  })
  const updateMut = useMutation({
    mutationFn: (d: FormData) => categoryService.update(editItem!.id!, { ...d, storeId: storeId! }),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['categories'] }); toast.success('Updated'); closeModal() },
    onError: (e: any) => toast.error(e.response?.data?.message ?? 'Error'),
  })
  const deleteMut = useMutation({
    mutationFn: (id: number) => categoryService.delete(id),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['categories'] }); toast.success('Deleted') },
    onError: (e: any) => toast.error(e.response?.data?.message ?? 'Error'),
  })

  const openCreate = () => { reset(); setEditItem(null); setModalOpen(true) }
  const openEdit = (c: CategoryDto) => { setEditItem(c); reset({ name: c.name }); setModalOpen(true) }
  const closeModal = () => { setModalOpen(false); setEditItem(null) }
  const onSubmit = (d: FormData) => editItem ? updateMut.mutate(d) : createMut.mutate(d)

  if (isLoading) return <LoadingSpinner />

  return (
    <div>
      <PageHeader title="Categories" subtitle={`${categories.length} categories`}
        action={<button onClick={openCreate} className="btn-primary"><Plus size={16}/> Add Category</button>} />

      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
        {categories.map(c => (
          <div key={c.id} className="card flex flex-col items-center text-center gap-3 py-6 group hover:border-primary-200 transition-colors border">
            <div className="w-12 h-12 rounded-xl bg-primary-50 flex items-center justify-center group-hover:bg-primary-100 transition-colors">
              <Tag size={20} className="text-primary-600" />
            </div>
            <p className="font-semibold text-gray-800 text-sm leading-snug">{c.name}</p>
            <div className="flex gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
              <button onClick={() => openEdit(c)} className="p-1.5 text-gray-400 hover:text-primary-600 hover:bg-primary-50 rounded-lg"><Pencil size={13}/></button>
              <button onClick={() => confirm('Delete?') && deleteMut.mutate(c.id!)} className="p-1.5 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded-lg"><Trash2 size={13}/></button>
            </div>
          </div>
        ))}
        {categories.length === 0 && (
          <div className="col-span-full text-center py-16 text-gray-400">
            <Tag size={40} className="mx-auto mb-3 opacity-30" />
            <p>No categories yet. Add your first one!</p>
          </div>
        )}
      </div>

      <Modal open={modalOpen} onClose={closeModal} title={editItem ? 'Edit Category' : 'Add Category'} size="sm">
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Category Name*</label>
            <input {...register('name')} className="input" placeholder="e.g. Beverages" autoFocus />
            {errors.name && <p className="text-xs text-red-500 mt-1">{errors.name.message}</p>}
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
