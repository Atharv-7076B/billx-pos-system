import { LucideIcon } from 'lucide-react'
import clsx from 'clsx'

interface StatCardProps {
  title: string
  value: string | number
  subtitle?: string
  icon: LucideIcon
  color?: 'green' | 'blue' | 'amber' | 'red' | 'purple'
  trend?: { value: number; label: string }
}

const colors = {
  green:  { bg: 'bg-emerald-50 dark:bg-emerald-950/25',  icon: 'bg-emerald-500 dark:bg-emerald-600',  text: 'text-emerald-600 dark:text-emerald-400' },
  blue:   { bg: 'bg-blue-50 dark:bg-blue-950/25',        icon: 'bg-blue-500 dark:bg-blue-600',        text: 'text-blue-600 dark:text-blue-400' },
  amber:  { bg: 'bg-amber-50 dark:bg-amber-950/25',      icon: 'bg-amber-500 dark:bg-amber-600',      text: 'text-amber-600 dark:text-amber-400' },
  red:    { bg: 'bg-red-50 dark:bg-red-950/25',          icon: 'bg-red-500 dark:bg-red-600',          text: 'text-red-600 dark:text-red-400' },
  purple: { bg: 'bg-purple-50 dark:bg-purple-950/25',    icon: 'bg-purple-500 dark:bg-purple-600',    text: 'text-purple-600 dark:text-purple-400' },
}

export default function StatCard({ title, value, subtitle, icon: Icon, color = 'green', trend }: StatCardProps) {
  const c = colors[color]
  return (
    <div className="card flex items-start gap-4 hover:border-primary-500/20 dark:hover:border-primary-500/20 transition-all duration-300 hover:shadow-card-md hover:-translate-y-0.5 group">
      <div className={clsx('p-3 rounded-2xl shrink-0 transition-transform duration-300 group-hover:scale-105', c.bg)}>
        <div className={clsx('p-2.5 rounded-xl text-white shadow-sm', c.icon)}>
          <Icon className="w-5 h-5" />
        </div>
      </div>
      <div className="min-w-0 flex-1">
        <p className="text-xs text-gray-400 dark:text-gray-500 font-bold uppercase tracking-wider">{title}</p>
        <p className="text-2xl font-extrabold text-gray-900 dark:text-white mt-1 leading-none tracking-tight">{value}</p>
        {subtitle && <p className="text-xs text-gray-500 dark:text-gray-400 mt-1.5 font-medium">{subtitle}</p>}
        {trend && (
          <p className={clsx('text-xs font-semibold mt-1.5 flex items-center gap-1', trend.value >= 0 ? 'text-emerald-600 dark:text-emerald-400' : 'text-red-500 dark:text-red-400')}>
            <span>{trend.value >= 0 ? '↑' : '↓'}</span>
            <span>{Math.abs(trend.value)}%</span>
            <span className="text-gray-400 dark:text-gray-500 font-normal">{trend.label}</span>
          </p>
        )}
      </div>
    </div>
  )
}
