import { useAuth } from '../context/AuthContext'
import PageHeader from '../components/common/PageHeader'
import { formatDate, formatRole } from '../utils/formatters'
import { User, Mail, Phone, Calendar, Shield } from 'lucide-react'

export default function ProfilePage() {
  const { user } = useAuth()
  if (!user) return null
  return (
    <div className="max-w-2xl">
      <PageHeader title="My Profile" />
      <div className="card">
        <div className="flex items-center gap-6 pb-6 border-b border-gray-100 mb-6">
          <div className="w-20 h-20 rounded-2xl bg-primary-100 flex items-center justify-center text-primary-700 font-bold text-3xl">
            {user.fullName[0].toUpperCase()}
          </div>
          <div>
            <h2 className="text-xl font-bold text-gray-900">{user.fullName}</h2>
            <span className="badge-green mt-1">{formatRole(user.role)}</span>
          </div>
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          {[
            { icon: User,     label: 'Full Name',  value: user.fullName },
            { icon: Mail,     label: 'Email',      value: user.email },
            { icon: Phone,    label: 'Phone',      value: user.phoneNumber ?? '—' },
            { icon: Shield,   label: 'Role',       value: formatRole(user.role) },
            { icon: Calendar, label: 'Joined',     value: formatDate(user.createdAt) },
            { icon: Calendar, label: 'Last Login', value: formatDate(user.lastLogin) },
          ].map(({ icon: Icon, label, value }) => (
            <div key={label} className="flex items-center gap-3 p-3 bg-gray-50 rounded-xl">
              <div className="p-2 bg-white rounded-lg shadow-sm"><Icon size={16} className="text-primary-600" /></div>
              <div><p className="text-xs text-gray-500">{label}</p><p className="text-sm font-medium text-gray-800">{value}</p></div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
