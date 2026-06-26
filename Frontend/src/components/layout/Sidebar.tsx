import { NavLink, useNavigate } from 'react-router-dom'
import {
  LayoutDashboard, Package, Tag, Warehouse, ShoppingCart, ClipboardList,
  Users, UserCheck, GitBranch, Store, BarChart3, Settings, LogOut,
  ChevronRight, Zap, User
} from 'lucide-react'
import { useAuth } from '../../context/AuthContext'
import clsx from 'clsx'

const nav = [
  { label: 'Dashboard',  icon: LayoutDashboard, to: '/' },
  { label: 'Billing / POS', icon: ShoppingCart,  to: '/billing', highlight: true },
  { label: 'Orders',     icon: ClipboardList,    to: '/orders' },
  { label: 'Products',   icon: Package,          to: '/products' },
  { label: 'Categories', icon: Tag,              to: '/categories' },
  { label: 'Inventory',  icon: Warehouse,        to: '/inventory' },
  { label: 'Customers',  icon: Users,            to: '/customers' },
  { label: 'Employees',  icon: UserCheck,        to: '/employees' },
  { label: 'Branches',   icon: GitBranch,        to: '/branches' },
  { label: 'Stores',     icon: Store,            to: '/stores' },
  { label: 'Reports',    icon: BarChart3,        to: '/reports' },
  { label: 'Profile',    icon: User,             to: '/profile' },
  { label: 'Settings',   icon: Settings,         to: '/settings' },
]

export default function Sidebar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => { logout(); navigate('/login') }

  return (
    <aside className="fixed inset-y-0 left-0 z-50 flex flex-col w-64 bg-gray-900 text-white">
      {/* Logo */}
      <div className="flex items-center gap-3 px-6 py-5 border-b border-gray-800">
        <div className="flex items-center justify-center w-9 h-9 rounded-xl bg-primary-500">
          <Zap className="w-5 h-5 text-white" />
        </div>
        <div>
          <p className="font-bold text-lg leading-none tracking-tight">BillX</p>
          <p className="text-xs text-gray-400 mt-0.5">Point of Sale</p>
        </div>
      </div>

      {/* Nav */}
      <nav className="flex-1 overflow-y-auto py-4 px-3 space-y-0.5">
        {nav.map(({ label, icon: Icon, to, highlight }) => (
          <NavLink
            key={to}
            to={to}
            end={to === '/'}
            className={({ isActive }) =>
              clsx(
                'flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all duration-150 group',
                highlight && !isActive && 'bg-primary-500/10 text-primary-400 hover:bg-primary-500/20',
                isActive
                  ? 'bg-primary-600 text-white shadow-sm'
                  : !highlight && 'text-gray-400 hover:bg-gray-800 hover:text-white'
              )
            }
          >
            <Icon className="w-4.5 h-4.5 shrink-0" size={18} />
            <span className="truncate flex-1">{label}</span>
            {highlight && <span className="text-[10px] font-semibold bg-primary-500 text-white px-1.5 py-0.5 rounded">POS</span>}
          </NavLink>
        ))}
      </nav>

      {/* User footer */}
      <div className="border-t border-gray-800 p-4">
        <div className="flex items-center gap-3 mb-3">
          <div className="w-8 h-8 rounded-full bg-primary-500/20 flex items-center justify-center text-primary-400 text-sm font-bold shrink-0">
            {user?.fullName?.[0]?.toUpperCase() ?? 'U'}
          </div>
          <div className="min-w-0">
            <p className="text-sm font-medium text-white truncate">{user?.fullName}</p>
            <p className="text-xs text-gray-500 truncate">{user?.email}</p>
          </div>
        </div>
        <button
          onClick={handleLogout}
          className="flex items-center gap-2 w-full px-3 py-2 text-sm text-gray-400 hover:text-red-400 hover:bg-red-500/10 rounded-lg transition-colors duration-150"
        >
          <LogOut size={16} />
          Sign out
        </button>
      </div>
    </aside>
  )
}
