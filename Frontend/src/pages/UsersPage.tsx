import { useQuery } from '@tanstack/react-query'
import { useAuth } from '../context/AuthContext'
import { employeeService } from '../services/employeeService'
import { UserDto } from '../types'
import PageHeader from '../components/common/PageHeader'
import DataTable, { Column } from '../components/tables/DataTable'
import LoadingSpinner from '../components/common/LoadingSpinner'
import { formatDate, formatRole } from '../utils/formatters'

export default function UsersPage() {
  const { storeId } = useAuth()
  const { data: users = [], isLoading } = useQuery({
    queryKey: ['employees', storeId], queryFn: () => employeeService.getByStore(storeId!), enabled: !!storeId,
  })

  const columns: Column<UserDto>[] = [
    { key: 'fullName', label: 'User', render: r => (
      <div className="flex items-center gap-3">
        <div className="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center text-primary-700 font-bold text-sm">{r.fullName[0]}</div>
        <div><p className="font-medium text-gray-800">{r.fullName}</p><p className="text-xs text-gray-400">{r.email}</p></div>
      </div>
    )},
    { key: 'role', label: 'Role', render: r => <span className="badge-blue">{formatRole(r.role)}</span> },
    { key: 'phoneNumber', label: 'Phone', render: r => <span className="text-gray-600">{r.phoneNumber ?? '—'}</span> },
    { key: 'createdAt', label: 'Created', render: r => <span className="text-gray-500 text-xs">{formatDate(r.createdAt)}</span> },
  ]

  if (isLoading) return <LoadingSpinner />
  return (
    <div>
      <PageHeader title="Users" subtitle={`${users.length} system users`} />
      <DataTable data={users as any[]} columns={columns as any} searchable searchPlaceholder="Search users…" />
    </div>
  )
}
