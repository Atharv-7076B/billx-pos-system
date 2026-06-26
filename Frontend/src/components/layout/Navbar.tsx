import { Bell, Search } from 'lucide-react'
import { useAuth } from '../../context/AuthContext'
import { formatRole } from '../../utils/formatters'

interface NavbarProps { title: string }

export default function Navbar({ title }: NavbarProps) {
  const { user } = useAuth()
  return (
    <header className="bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between">
      <h1 className="text-xl font-semibold text-gray-800">{title}</h1>
      <div className="flex items-center gap-3">
        <button className="relative p-2 text-gray-500 hover:text-gray-700 hover:bg-gray-100 rounded-lg transition-colors">
          <Bell size={18} />
        </button>
        <div className="flex items-center gap-2.5 pl-3 border-l border-gray-200">
          <div className="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center text-primary-700 font-bold text-sm">
            {user?.fullName?.[0]?.toUpperCase() ?? 'U'}
          </div>
          <div className="hidden sm:block">
            <p className="text-sm font-medium text-gray-800 leading-none">{user?.fullName}</p>
            <p className="text-xs text-gray-500 mt-0.5">{formatRole(user?.role ?? '')}</p>
          </div>
        </div>
      </div>
    </header>
  )
}
