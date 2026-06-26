import { format, parseISO } from 'date-fns'

export const formatCurrency = (amount: number) =>
  new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(amount)

export const formatDate = (date?: string) => {
  if (!date) return '—'
  try { return format(parseISO(date), 'dd MMM yyyy') } catch { return date }
}

export const formatDateTime = (date?: string) => {
  if (!date) return '—'
  try { return format(parseISO(date), 'dd MMM yyyy, hh:mm a') } catch { return date }
}

export const formatRole = (role: string) =>
  role?.replace('ROLE_', '').replace(/_/g, ' ') ?? '—'

export const getStatusColor = (status: string) => {
  const map: Record<string, string> = {
    COMPLETED: 'badge-green', ACTIVE: 'badge-green', PAID: 'badge-green',
    PENDING: 'badge-yellow', PROCESSING: 'badge-yellow',
    CANCELLED: 'badge-red', BLOCKED: 'badge-red', FAILED: 'badge-red',
    REFUNDED: 'badge-blue', STOCK_IN: 'badge-green', STOCK_OUT: 'badge-red',
  }
  return map[status] ?? 'badge-gray'
}

export const generateSKU = (name: string) => {
  const prefix = name.substring(0, 3).toUpperCase().replace(/\s/g, '')
  const suffix = Math.floor(Math.random() * 9000 + 1000)
  return `${prefix}-${suffix}`
}
