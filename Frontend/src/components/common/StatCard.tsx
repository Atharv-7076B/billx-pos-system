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
  green:  { bg: 'bg-emerald-50',  icon: 'bg-emerald-500',  text: 'text-emerald-600' },
  blue:   { bg: 'bg-blue-50',     icon: 'bg-blue-500',     text: 'text-blue-600' },
  amber:  { bg: 'bg-amber-50',    icon: 'bg-amber-500',    text: 'text-amber-600' },
  red:    { bg: 'bg-red-50',      icon: 'bg-red-500',      text: 'text-red-600' },
  purple: { bg: 'bg-purple-50',   icon: 'bg-purple-500',   text: 'text-purple-600' },
}

export default function StatCard({ title, value, subtitle, icon: Icon, color = 'green', trend }: StatCardProps) {
  const c = colors[color]
  return (
    <div className="card flex items-start gap-4">
      <div className={clsx('p-3 rounded-xl shrink-0', c.bg)}>
        <div className={clsx('p-2 rounded-lg', c.icon)}>
          <Icon className="w-5 h-5 text-white" />
        </div>
      </div>
      <div className="min-w-0 flex-1">
        <p className="text-sm text-gray-500 font-medium">{title}</p>
        <p className="text-2xl font-bold text-gray-900 mt-0.5 leading-none">{value}</p>
        {subtitle && <p className="text-xs text-gray-500 mt-1">{subtitle}</p>}
        {trend && (
          <p className={clsx('text-xs font-medium mt-1', trend.value >= 0 ? 'text-emerald-600' : 'text-red-500')}>
            {trend.value >= 0 ? '↑' : '↓'} {Math.abs(trend.value)}% {trend.label}
          </p>
        )}
      </div>
    </div>
  )
}
