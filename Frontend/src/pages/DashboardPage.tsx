import { useQuery } from '@tanstack/react-query'
import { TrendingUp, ShoppingBag, Users, Package, AlertTriangle, IndianRupee, ClipboardList, Store } from 'lucide-react'
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar } from 'recharts'
import { useAuth } from '../context/AuthContext'
import { dashboardService } from '../services/dashboardService'
import StatCard from '../components/common/StatCard'
import LoadingSpinner from '../components/common/LoadingSpinner'
import { formatCurrency, formatDateTime, getStatusColor } from '../utils/formatters'

export default function DashboardPage() {
  const { storeId } = useAuth()

  const { data: stats, isLoading } = useQuery({
    queryKey: ['dashboard', storeId],
    queryFn: () => dashboardService.getStats(storeId!),
    enabled: !!storeId,
    refetchInterval: 60_000,
  })

  if (!storeId) return (
    <div className="flex flex-col items-center justify-center h-64 text-center">
      <Store size={48} className="text-gray-300 mb-4" />
      <h3 className="text-lg font-semibold text-gray-700">No store linked</h3>
      <p className="text-sm text-gray-500 mt-1">Your account isn't linked to a store yet. Contact your admin.</p>
    </div>
  )

  if (isLoading) return <LoadingSpinner />

  const s = stats!

  return (
    <div className="space-y-6">
      {/* Stat cards */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Total Revenue"    value={formatCurrency(s.totalRevenue)}  icon={IndianRupee} color="green" />
        <StatCard title="Today's Sales"    value={formatCurrency(s.todaySales)}    icon={TrendingUp}  color="blue"
          subtitle={`${s.todayOrders} order${s.todayOrders !== 1 ? 's' : ''} today`} />
        <StatCard title="Monthly Sales"    value={formatCurrency(s.monthlySales)}  icon={ShoppingBag} color="purple" />
        <StatCard title="Total Orders"     value={s.totalOrders}                   icon={ClipboardList} color="amber" />
        <StatCard title="Total Customers"  value={s.totalCustomers}                icon={Users}       color="blue" />
        <StatCard title="Total Products"   value={s.totalProducts}                 icon={Package}     color="green" />
        <StatCard title="Low Stock Alerts" value={s.lowStockItems}                 icon={AlertTriangle} color="red"
          subtitle="Items below 10 units" />
      </div>

      {/* Charts row */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Sales area chart */}
        <div className="card lg:col-span-2">
          <h3 className="font-semibold text-gray-800 mb-4">Sales — Last 7 Days</h3>
          <ResponsiveContainer width="100%" height={220}>
            <AreaChart data={s.salesChartData}>
              <defs>
                <linearGradient id="grad" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#10b981" stopOpacity={0.2} />
                  <stop offset="95%" stopColor="#10b981" stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
              <XAxis dataKey="date" tick={{ fontSize: 12 }} tickFormatter={d => d.slice(5)} />
              <YAxis tick={{ fontSize: 12 }} tickFormatter={v => `₹${v}`} />
              <Tooltip formatter={(v: number) => [formatCurrency(v), 'Sales']} />
              <Area type="monotone" dataKey="sales" stroke="#10b981" strokeWidth={2} fill="url(#grad)" />
            </AreaChart>
          </ResponsiveContainer>
        </div>

        {/* Low stock */}
        <div className="card">
          <h3 className="font-semibold text-gray-800 mb-4 flex items-center gap-2">
            <AlertTriangle size={16} className="text-amber-500" /> Low Stock
          </h3>
          {s.lowStockAlerts.length === 0 ? (
            <p className="text-sm text-gray-400 text-center py-8">All items are stocked well 🎉</p>
          ) : (
            <div className="space-y-2">
              {s.lowStockAlerts.slice(0, 6).map((a, i) => (
                <div key={i} className="flex items-center justify-between py-2 border-b border-gray-50 last:border-0">
                  <div className="min-w-0">
                    <p className="text-sm font-medium text-gray-800 truncate">{a.productName}</p>
                    <p className="text-xs text-gray-500">{a.branchName}</p>
                  </div>
                  <span className={`badge-${a.quantity === 0 ? 'red' : 'yellow'} ml-2 shrink-0`}>
                    {a.quantity} left
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Recent orders */}
      <div className="card">
        <h3 className="font-semibold text-gray-800 mb-4">Recent Transactions</h3>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-gray-100">
                {['Order #', 'Customer', 'Amount', 'Payment', 'Status', 'Date'].map(h => (
                  <th key={h} className="text-left py-2 px-3 text-xs text-gray-500 font-semibold uppercase tracking-wide">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-50">
              {s.recentOrders.map(o => (
                <tr key={o.id} className="hover:bg-gray-50/50">
                  <td className="py-3 px-3 font-mono text-xs text-gray-600">{o.orderNumber}</td>
                  <td className="py-3 px-3 text-gray-700">{o.customerName}</td>
                  <td className="py-3 px-3 font-semibold text-gray-900">{formatCurrency(o.total)}</td>
                  <td className="py-3 px-3 text-gray-600">{o.paymentMethod}</td>
                  <td className="py-3 px-3"><span className={getStatusColor(o.status)}>{o.status}</span></td>
                  <td className="py-3 px-3 text-gray-500">{formatDateTime(o.createdAt)}</td>
                </tr>
              ))}
              {s.recentOrders.length === 0 && (
                <tr><td colSpan={6} className="text-center py-8 text-gray-400">No transactions yet</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
