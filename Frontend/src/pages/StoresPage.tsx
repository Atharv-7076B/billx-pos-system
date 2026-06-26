import { useQuery } from '@tanstack/react-query'
import { Store, MapPin, Phone, Mail, CheckCircle2, Clock, XCircle } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { storeService } from '../services/storeService'
import { StoreDto } from '../types'
import PageHeader from '../components/common/PageHeader'
import LoadingSpinner from '../components/common/LoadingSpinner'
import { formatDate } from '../utils/formatters'

const statusIcon = { ACTIVE: <CheckCircle2 size={14}/>, PENDING: <Clock size={14}/>, BLOCKED: <XCircle size={14}/> }
const statusClass = { ACTIVE: 'badge-green', PENDING: 'badge-yellow', BLOCKED: 'badge-red' }

export default function StoresPage() {
  const { data: stores = [], isLoading } = useQuery({ queryKey: ['stores'], queryFn: storeService.getAll })
  if (isLoading) return <LoadingSpinner />
  return (
    <div>
      <PageHeader title="Stores" subtitle={`${stores.length} stores in the network`} />
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {stores.map((s: StoreDto) => (
          <div key={s.id} className="card border hover:border-primary-200 transition-all">
            <div className="flex items-start justify-between mb-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-primary-50 flex items-center justify-center">
                  <Store size={20} className="text-primary-600" />
                </div>
                <div>
                  <p className="font-semibold text-gray-800">{s.brand}</p>
                  <p className="text-xs text-gray-500">{s.storeType ?? 'Retail'}</p>
                </div>
              </div>
              <span className={`${statusClass[s.status ?? 'PENDING']} flex items-center gap-1`}>
                {statusIcon[s.status ?? 'PENDING']}{s.status}
              </span>
            </div>
            {s.description && <p className="text-sm text-gray-600 mb-3">{s.description}</p>}
            <div className="space-y-1.5 text-sm text-gray-500">
              {s.storeContact?.address && <div className="flex items-center gap-2"><MapPin size={13}/>{s.storeContact.address}</div>}
              {s.storeContact?.phoneNo && <div className="flex items-center gap-2"><Phone size={13}/>{s.storeContact.phoneNo}</div>}
              {s.storeContact?.email  && <div className="flex items-center gap-2"><Mail size={13}/>{s.storeContact.email}</div>}
            </div>
            <div className="mt-3 pt-3 border-t border-gray-100 flex justify-between text-xs text-gray-400">
              <span>Admin: {s.storeAdmin?.fullName ?? '—'}</span>
              <span>Created {formatDate(s.createdAt)}</span>
            </div>
          </div>
        ))}
        {stores.length === 0 && (
          <div className="col-span-full text-center py-16 text-gray-400">
            <Store size={40} className="mx-auto mb-3 opacity-30" />
            <p>No stores found</p>
          </div>
        )}
      </div>
    </div>
  )
}
