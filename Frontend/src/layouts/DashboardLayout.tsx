import { Outlet, useLocation } from 'react-router-dom'
import Sidebar from '../components/layout/Sidebar'
import Navbar from '../components/layout/Navbar'

const titles: Record<string, string> = {
  '/': 'Dashboard',
  '/billing': 'Billing / POS',
  '/orders': 'Orders',
  '/products': 'Products',
  '/categories': 'Categories',
  '/inventory': 'Inventory',
  '/customers': 'Customers',
  '/employees': 'Employees',
  '/branches': 'Branches',
  '/stores': 'Stores',
  '/reports': 'Reports',
  '/profile': 'My Profile',
  '/settings': 'Settings',
}

export default function DashboardLayout() {
  const { pathname } = useLocation()
  const title = titles[pathname] ?? 'BillX POS'

  return (
    <div className="flex h-screen bg-gray-50 overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 ml-64">
        <Navbar title={title} />
        <main className="flex-1 overflow-y-auto p-6">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
