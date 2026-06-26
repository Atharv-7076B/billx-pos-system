import { ReactNode } from 'react'

interface PageHeaderProps {
  title: string
  subtitle?: string
  action?: ReactNode
}

export default function PageHeader({ title, subtitle, action }: PageHeaderProps) {
  return (
    <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6 pb-5 border-b border-gray-100 dark:border-gray-800/60">
      <div>
        <h2 className="text-2xl font-extrabold tracking-tight text-gray-900 dark:text-white">{title}</h2>
        {subtitle && <p className="text-sm font-medium text-gray-500 dark:text-gray-400 mt-1">{subtitle}</p>}
      </div>
      {action && <div className="shrink-0 flex items-center gap-2">{action}</div>}
    </div>
  )
}
