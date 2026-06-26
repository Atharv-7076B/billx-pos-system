import { ReactNode, useState } from 'react'
import { Search, ChevronLeft, ChevronRight } from 'lucide-react'

export interface Column<T> {
  key: string
  label: string
  render?: (row: T) => ReactNode
  sortable?: boolean
}

interface DataTableProps<T> {
  data: T[]
  columns: Column<T>[]
  searchable?: boolean
  searchPlaceholder?: string
  onSearch?: (query: string) => void
  pageSize?: number
  actions?: (row: T) => ReactNode
  emptyMessage?: string
}

export default function DataTable<T extends Record<string, unknown>>({
  data, columns, searchable, searchPlaceholder = 'Search…', onSearch,
  pageSize = 10, actions, emptyMessage = 'No records found'
}: DataTableProps<T>) {
  const [page, setPage] = useState(1)
  const [searchVal, setSearchVal] = useState('')

  const filtered = searchVal && !onSearch
    ? data.filter(row =>
        columns.some(col => String(row[col.key] ?? '').toLowerCase().includes(searchVal.toLowerCase()))
      )
    : data

  const totalPages = Math.ceil(filtered.length / pageSize)
  const paged = filtered.slice((page - 1) * pageSize, page * pageSize)

  const handleSearch = (v: string) => {
    setSearchVal(v)
    setPage(1)
    onSearch?.(v)
  }

  return (
    <div className="card p-0 overflow-hidden border border-gray-100/80 dark:border-gray-800/80 shadow-sm dark:shadow-none bg-white dark:bg-gray-900 transition-all duration-300">
      {searchable && (
        <div className="px-5 py-4 border-b border-gray-100 dark:border-gray-800 bg-gray-50/30 dark:bg-gray-900/30 flex items-center justify-between">
          <div className="relative w-full max-w-xs">
            <Search size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400 dark:text-gray-500" />
            <input
              className="input pl-10 py-2 text-sm bg-white dark:bg-gray-950 border border-gray-200 dark:border-gray-800/80 rounded-xl focus:ring-primary-500/20 focus:border-primary-500"
              placeholder={searchPlaceholder}
              value={searchVal}
              onChange={e => handleSearch(e.target.value)}
            />
          </div>
        </div>
      )}

      <div className="overflow-x-auto scrollbar-thin">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-50 dark:bg-gray-900/60 border-b border-gray-100 dark:border-gray-800">
              {columns.map(col => (
                <th key={col.key} className="text-left px-5 py-3.5 text-xs font-bold text-gray-500 dark:text-gray-400 uppercase tracking-wider whitespace-nowrap">
                   {col.label}
                </th>
              ))}
              {actions && <th className="px-5 py-3.5 text-right text-xs font-bold text-gray-500 dark:text-gray-400 uppercase tracking-wider">Actions</th>}
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100/50 dark:divide-gray-800/50">
            {paged.length === 0 ? (
              <tr>
                <td colSpan={columns.length + (actions ? 1 : 0)} className="text-center py-16 text-gray-400 dark:text-gray-500 font-medium">
                  {emptyMessage}
                </td>
              </tr>
            ) : paged.map((row, i) => (
              <tr key={i} className="hover:bg-gray-50/40 dark:hover:bg-gray-800/25 transition-colors border-b border-gray-100/40 dark:border-gray-800/30 last:border-0">
                {columns.map(col => (
                  <td key={col.key} className="px-5 py-3.5 text-gray-700 dark:text-gray-300 font-medium whitespace-nowrap">
                     {col.render ? col.render(row) : String(row[col.key] ?? '—')}
                  </td>
                ))}
                {actions && <td className="px-5 py-3.5 text-right whitespace-nowrap">{actions(row)}</td>}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {totalPages > 1 && (
        <div className="px-5 py-4 border-t border-gray-100 dark:border-gray-800 flex items-center justify-between text-sm text-gray-500 dark:text-gray-400 bg-gray-50/20 dark:bg-gray-900/20">
          <span>
            Showing <span className="font-semibold text-gray-700 dark:text-gray-300">{(page - 1) * pageSize + 1}</span>–<span className="font-semibold text-gray-700 dark:text-gray-300">{Math.min(page * pageSize, filtered.length)}</span> of <span className="font-semibold text-gray-700 dark:text-gray-300">{filtered.length}</span>
          </span>
          <div className="flex items-center gap-1.5">
            <button
              onClick={() => setPage(p => p - 1)}
              disabled={page === 1}
              className="p-1.5 hover:bg-gray-100 dark:hover:bg-gray-800/80 rounded-xl text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white border border-gray-200/60 dark:border-gray-800 disabled:opacity-40 disabled:cursor-not-allowed transition-all duration-200"
            >
              <ChevronLeft size={16} />
            </button>
            <span className="px-3.5 py-1 bg-primary-500 text-white rounded-xl font-bold shadow-sm shadow-primary-500/20 text-xs">
              {page}
            </span>
            <button
              onClick={() => setPage(p => p + 1)}
              disabled={page === totalPages}
              className="p-1.5 hover:bg-gray-100 dark:hover:bg-gray-800/80 rounded-xl text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white border border-gray-200/60 dark:border-gray-800 disabled:opacity-40 disabled:cursor-not-allowed transition-all duration-200"
            >
              <ChevronRight size={16} />
            </button>
          </div>
        </div>
      )}
    </div>
  )
}
