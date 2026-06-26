import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import toast from 'react-hot-toast'
import { Plus, Pencil, Trash2, UserCheck, Loader2 } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { employeeService } from '../services/employeeService'
import { UserDto, UserRole } from '../types'
import PageHeader from '../components/common/PageHeader'
import Modal from '../components/ui/Modal'
import DataTable, { Column } from '../components/tables/DataTable'
import LoadingSpinner from '../components/common/LoadingSpinner'
import { formatDate, formatRole } from '../utils/formatters'

const roles: UserRole[] = ['ROLE_STORE_MANAGER', 'ROLE_BRANCH_MANAGER', 'ROLE_BRANCH_CASHIER', 'ROLE_USER']

const schema = z.object({
  fullName: z.string().min(1, 'Name required'),
  email: z.string().email('Invalid email'),
  password: z.string().min(6).optional().or(z.literal('')),
  phoneNumber: z.string().optional(),
  role: z.string().min(1, 'Role required'),
})
type FormData = z.infer<typeof schema>

export default function EmployeesPage() {
  const { storeId } = useAuth()
  const qc = useQueryClient()
  const [modalOpen, setModalOpen] = useState(false)
  const [editItem, setEditItem] = useState<UserDto | null>(null)

  const { data: employees = [], isLoading } = useQuery({
    queryKey: ['employees', storeId],
    queryFn: () => employeeService.getByStore(storeId!),
    enabled: !!storeId,
  })

  const { register, handleSubmit, reset, formState: { errors, isSubmitting } } = useForm<FormData>({ resolver: zodResolver(schema) })

  const createMut = useMutation({
    mutationFn: (d: FormData) => employeeService.createForStore(storeId!, { ...d, password: d.password || 'changeme123' } as any),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['employees'] }); toast.success('Employee created'); closeModal() },
    onError: (e: any) => toast.error(e.response?.data?.message ?? 'Error'),
  })
  const updateMut = useMutation({
    mutationFn: (d: FormData) => employeeService.update(editItem!.id!, d as any),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['employees'] }); toast.success('Updated'); closeModal() },
    onError: (e: any) => toast.error(e.response?.data?.message ?? 'Error'),
  })
  const deleteMut = useMutation({
    mutationFn: (id: number) => employeeService.delete(id),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['employees'] }); toast.success('Removed') },
    onError: (e: any) => toast.error(e.response?.data?.message ?? 'Error'),
  })

  const openCreate = () => { reset(); setEditItem(null); setModalOpen(true) }
  const openEdit = (e: UserDto) => { setEditItem(e); reset({ fullName: e.fullName, email: e.email, phoneNumber: e.phoneNumber, role: e.role }); setModalOpen(true) }
  const closeModal = () => { setModalOpen(false); setEditItem(null) }
  const onSubmit = (d: FormData) => editItem ? updateMut.mutate(d) : createMut.mutate(d)

  const columns: Column<UserDto>[] = [
    { key: 'fullName', label: 'Employee', render: r => (
      <div className="flex items-center gap-3">
        <div className="w-9 h-9 rounded-full bg-primary-100 flex items-center justify-center text-primary-700 font-bold">{r.fullName[0]}</div>
        <div><p className="font-medium text-gray-800">{r.fullName}</p><p className="text-xs text-gray-400">{r.email}</p></div>
      </div>
    )},
    { key: 'role', label: 'Role', render: r => <span className="badge-blue">{formatRole(r.role)}</span> },
    { key: 'phoneNumber', label: 'Phone', render: r => <span className="text-gray-600">{r.phoneNumber ?? '—'}</span> },
    { key: 'createdAt',   label: 'Joined',  render: r => <span className="text-gray-500 text-xs">{formatDate(r.createdAt)}</span> },
  ]

  if (isLoading) return <LoadingSpinner />

  return (
    <div>
      <PageHeader title="Employees" subtitle={`${employees.length} employees`}
        action={<button onClick={openCreate} className="btn-primary"><Plus size={16}/> Add Employee</button>} />

      <DataTable data={employees as any[]} columns={columns as any} searchable
        actions={(e: UserDto) => (
          <div className="flex gap-1 justify-end">
            <button onClick={() => openEdit(e)} className="p-1.5 text-gray-400 hover:text-primary-600 hover:bg-primary-50 rounded-lg"><Pencil size={15}/></button>
            <button onClick={() => confirm('Remove employee?') && deleteMut.mutate(e.id!)} className="p-1.5 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg"><Trash2 size={15}/></button>
          </div>
        )} />

      <Modal open={modalOpen} onClose={closeModal} title={editItem ? 'Edit Employee' : 'Add Employee'}>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Full Name*</label>
            <input {...register('fullName')} className="input" placeholder="John Doe" />
            {errors.fullName && <p className="text-xs text-red-500 mt-1">{errors.fullName.message}</p>}
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Email*</label>
            <input {...register('email')} type="email" className="input" placeholder="john@store.com" />
            {errors.email && <p className="text-xs text-red-500 mt-1">{errors.email.message}</p>}
          </div>
          {!editItem && (
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
              <input {...register('password')} type="password" className="input" placeholder="Min 6 characters" />
            </div>
          )}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Phone</label>
            <input {...register('phoneNumber')} className="input" placeholder="+91 98765 43210" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Role*</label>
            <select {...register('role')} className="input">
              <option value="">Select role</option>
              {roles.map(r => <option key={r} value={r}>{formatRole(r)}</option>)}
            </select>
            {errors.role && <p className="text-xs text-red-500 mt-1">{errors.role.message}</p>}
          </div>
          <div className="flex gap-3">
            <button type="button" onClick={closeModal} className="btn-secondary flex-1">Cancel</button>
            <button type="submit" disabled={isSubmitting} className="btn-primary flex-1 justify-center">
              {isSubmitting ? <Loader2 size={14} className="animate-spin"/> : editItem ? 'Update' : 'Add Employee'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  )
}
