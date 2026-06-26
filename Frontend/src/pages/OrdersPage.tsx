import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import toast from 'react-hot-toast'
import { ClipboardList, Eye, XCircle } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { orderService } from '../services/orderService'
import { OrderDto } from '../types'
import PageHeader from '../components/common/PageHeader'
import Modal from '../components/ui/Modal'
import DataTable, { Column } from '../components/tables/DataTable'
import LoadingSpinner from '../components/common/LoadingSpinner'
import Badge from '../components/ui/Badge'
import { formatCurrency, formatDateTime } from '../utils/formatters'

export default function OrdersPage() {
  const { storeId } = useAuth()
  const qc = useQueryClient()
  const [viewOrder, setViewOrder] = useState<OrderDto | null>(null)

  const { data: orders = [], isLoading } = useQuery({
    queryKey: ['orders', storeId],
    queryFn: () => orderService.getByStore(storeId!),
    enabled: !!storeId,
  })

  const cancelMut = useMutation({
    mutationFn: (id: number) => orderService.cancel(id),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['orders'] }); toast.success('Order cancelled') },
    onError: (e: any) => toast.error(e.response?.data?.message ?? 'Cannot cancel'),
  })

  const columns: Column<OrderDto>[] = [
    { key: 'orderNumber', label: 'Order #',  render: r => <span className="font-mono text-xs font-semibold text-gray-700">{r.orderNumber}</span> },
    { key: 'customer',    label: 'Customer', render: r => <span className="text-gray-700">{r.customer?.name ?? 'Walk-in'}</span> },
    { key: 'total',       label: 'Total',    render: r => <span className="font-bold text-gray-900">{formatCurrency(r.total)}</span> },
    { key: 'paymentMethod', label: 'Payment', render: r => <span className="badge-gray">{r.paymentMethod}</span> },
    { key: 'orderStatus',  label: 'Status',  render: r => <Badge status={r.orderStatus ?? ''} /> },
    { key: 'createdAt',   label: 'Date',     render: r => <span className="text-gray-500 text-xs">{formatDateTime(r.createdAt)}</span> },
  ]

  if (isLoading) return <LoadingSpinner />

  return (
    <div>
      <PageHeader title="Orders" subtitle={`${orders.length} total orders`} />

      <DataTable data={orders as any[]} columns={columns as any} searchable searchPlaceholder="Search orders…"
        actions={(o: OrderDto) => (
          <div className="flex gap-1 justify-end">
            <button onClick={() => setViewOrder(o)} className="p-1.5 text-gray-400 hover:text-primary-600 hover:bg-primary-50 rounded-lg"><Eye size={15}/></button>
            {o.orderStatus === 'COMPLETED' && (
              <button onClick={() => confirm('Cancel order?') && cancelMut.mutate(o.id!)}
                className="p-1.5 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg"><XCircle size={15}/></button>
            )}
          </div>
        )} />

      <Modal open={!!viewOrder} onClose={() => setViewOrder(null)} title={`Order ${viewOrder?.orderNumber}`} size="lg">
        {viewOrder && (
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4 text-sm">
              <div><p className="text-gray-500">Customer</p><p className="font-medium">{viewOrder.customer?.name ?? 'Walk-in'}</p></div>
              <div><p className="text-gray-500">Cashier</p><p className="font-medium">{viewOrder.cashierName ?? '—'}</p></div>
              <div><p className="text-gray-500">Payment</p><p className="font-medium">{viewOrder.paymentMethod}</p></div>
              <div><p className="text-gray-500">Status</p><Badge status={viewOrder.orderStatus ?? ''} /></div>
              <div><p className="text-gray-500">Date</p><p className="font-medium">{formatDateTime(viewOrder.createdAt)}</p></div>
              <div><p className="text-gray-500">Branch</p><p className="font-medium">{viewOrder.branchName ?? '—'}</p></div>
            </div>

            <div className="border-t border-gray-100 pt-4">
              <table className="w-full text-sm">
                <thead><tr className="text-gray-500 border-b"><th className="text-left py-1.5">Item</th><th className="text-center py-1.5">Qty</th><th className="text-right py-1.5">Price</th><th className="text-right py-1.5">Total</th></tr></thead>
                <tbody>
                  {viewOrder.items?.map((item, i) => (
                    <tr key={i} className="border-b border-gray-50">
                      <td className="py-2">{item.productName}</td>
                      <td className="py-2 text-center">{item.quantity}</td>
                      <td className="py-2 text-right">{formatCurrency(item.unitPrice)}</td>
                      <td className="py-2 text-right font-medium">{formatCurrency(item.total)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <div className="bg-gray-50 rounded-xl p-4 space-y-1.5 text-sm">
              <div className="flex justify-between"><span className="text-gray-500">Subtotal</span><span>{formatCurrency(viewOrder.subtotal)}</span></div>
              {viewOrder.discountAmount > 0 && <div className="flex justify-between text-red-500"><span>Discount</span><span>-{formatCurrency(viewOrder.discountAmount)}</span></div>}
              {viewOrder.taxAmount > 0 && <div className="flex justify-between text-gray-500"><span>Tax ({viewOrder.taxPercent}%)</span><span>{formatCurrency(viewOrder.taxAmount)}</span></div>}
              <div className="flex justify-between font-bold text-base border-t border-gray-200 pt-2 mt-2"><span>Total</span><span className="text-primary-600">{formatCurrency(viewOrder.total)}</span></div>
            </div>
          </div>
        )}
      </Modal>
    </div>
  )
}
