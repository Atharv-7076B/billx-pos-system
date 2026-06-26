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
    <div className="flex h-screen bg-gray-50 dark:bg-gray-950 text-gray-900 dark:text-gray-100 overflow-hidden transition-colors duration-300 relative">
      {/* Premium subtle background gradient */}
      <div className="absolute inset-0 bg-[radial-gradient(circle_at_top_right,rgba(16,185,129,0.03),transparent_40%)] dark:bg-[radial-gradient(circle_at_top_right,rgba(16,185,129,0.05),transparent_50%)] pointer-events-none" />
      
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 ml-64 relative">
        <Navbar title={title} />
        <main className="flex-1 overflow-y-auto p-6 focus:outline-none scrollbar-thin">
          <div className="animate-fade-in">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  )
}
