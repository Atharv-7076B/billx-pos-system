import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import {
  BarChart, Bar, LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip,
  ResponsiveContainer, PieChart, Pie, Cell, Legend,
} from 'recharts'
import { useAuth } from '../context/AuthContext'
import { reportService } from '../services/reportService'
import LoadingSpinner from '../components/common/LoadingSpinner'
import PageHeader from '../components/common/PageHeader'
import { formatCurrency } from '../utils/formatters'

const COLORS = ['#10b981', '#047857', '#34d399', '#6ee7b7', '#a7f3d0']

export default function ReportsPage() {
  const { storeId } = useAuth()
  const [chartDays, setChartDays] = useState(30)

  const { data: chartData = [], isLoading: chartLoading } = useQuery({
    queryKey: ['chart', storeId, chartDays],
    queryFn: () => reportService.getChartData(storeId!, chartDays),
    enabled: !!storeId,
  })
  const { data: inventoryReport, isLoading: invLoading } = useQuery({
    queryKey: ['inventory-report', storeId],
    queryFn: () => reportService.getInventoryReport(storeId!),
    enabled: !!storeId,
  })
  const { data: productReport } = useQuery({
    queryKey: ['product-report', storeId],
    queryFn: () => reportService.getProductReport(storeId!),
    enabled: !!storeId,
  })

  const formattedChart = (chartData as any[]).map((d: any) => ({ date: d.date?.slice(5), sales: d.sales, orders: d.orders }))
  const topProducts = ((productReport as any)?.products ?? []).slice(0, 5)
  const invItems = ((inventoryReport as any)?.inventory ?? [])
  const lowStock = invItems.filter((i: any) => i.lowStock)

  return (
    <div className="space-y-6">
      <PageHeader title="Reports & Analytics" subtitle="Sales performance and inventory insights" />

      {/* Chart period selector */}
      <div className="flex gap-2">
        {[7, 14, 30, 90].map(d => (
          <button key={d} onClick={() => setChartDays(d)}
            className={`px-3 py-1.5 text-sm rounded-lg font-medium transition-colors ${chartDays === d ? 'bg-primary-600 text-white' : 'bg-white border text-gray-600 hover:bg-gray-50'}`}>
            {d}d
          </button>
        ))}
      </div>

      {/* Sales line chart */}
      <div className="card">
        <h3 className="font-semibold text-gray-800 mb-4">Revenue Trend</h3>
        {chartLoading ? <LoadingSpinner className="h-48" /> : (
          <ResponsiveContainer width="100%" height={260}>
            <LineChart data={formattedChart}>
              <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
              <XAxis dataKey="date" tick={{ fontSize: 11 }} />
              <YAxis tick={{ fontSize: 11 }} tickFormatter={v => `₹${v}`} />
              <Tooltip formatter={(v: number) => [formatCurrency(v), 'Revenue']} />
              <Line type="monotone" dataKey="sales" stroke="#10b981" strokeWidth={2.5} dot={false} />
            </LineChart>
          </ResponsiveContainer>
        )}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Orders bar chart */}
        <div className="card">
          <h3 className="font-semibold text-gray-800 mb-4">Daily Orders</h3>
          <ResponsiveContainer width="100%" height={220}>
            <BarChart data={formattedChart}>
              <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
              <XAxis dataKey="date" tick={{ fontSize: 11 }} />
              <YAxis tick={{ fontSize: 11 }} />
              <Tooltip />
              <Bar dataKey="orders" fill="#10b981" radius={[4,4,0,0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Top products pie */}
        {topProducts.length > 0 && (
          <div className="card">
            <h3 className="font-semibold text-gray-800 mb-4">Top Products by Sales</h3>
            <ResponsiveContainer width="100%" height={220}>
              <PieChart>
                <Pie data={topProducts} dataKey="totalRevenue" nameKey="productName" cx="50%" cy="50%" outerRadius={80} label={({ name, percent }: any) => `${name?.substring(0,10)} ${(percent*100).toFixed(0)}%`}>
                  {topProducts.map((_: any, i: number) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
                </Pie>
                <Tooltip formatter={(v: number) => [formatCurrency(v), 'Revenue']} />
              </PieChart>
            </ResponsiveContainer>
          </div>
        )}
      </div>

      {/* Low stock table */}
      {lowStock.length > 0 && (
        <div className="card">
          <h3 className="font-semibold text-gray-800 mb-4">Low Stock Items ({lowStock.length})</h3>
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead><tr className="border-b border-gray-100 text-left text-xs text-gray-500 uppercase">
                <th className="py-2 px-3">Product</th><th className="py-2 px-3">SKU</th>
                <th className="py-2 px-3">Branch</th><th className="py-2 px-3">Qty</th>
              </tr></thead>
              <tbody>
                {lowStock.map((i: any, idx: number) => (
                  <tr key={idx} className="border-b border-gray-50 hover:bg-gray-50/50">
                    <td className="py-2.5 px-3 font-medium">{i.productName}</td>
                    <td className="py-2.5 px-3 text-gray-500 font-mono text-xs">{i.sku}</td>
                    <td className="py-2.5 px-3 text-gray-600">{i.branchName}</td>
                    <td className="py-2.5 px-3">
                      <span className={`font-bold ${i.quantity === 0 ? 'text-red-600' : 'text-amber-600'}`}>{i.quantity}</span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  )
}
