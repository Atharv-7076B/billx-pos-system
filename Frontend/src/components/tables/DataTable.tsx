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
    <div className="card p-0 overflow-hidden">
      {searchable && (
        <div className="px-4 py-3 border-b border-gray-100">
          <div className="relative max-w-xs">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              className="input pl-9 py-1.5 text-sm"
              placeholder={searchPlaceholder}
              value={searchVal}
              onChange={e => handleSearch(e.target.value)}
            />
          </div>
        </div>
      )}

      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-50 border-b border-gray-100">
              {columns.map(col => (
                <th key={col.key} className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide whitespace-nowrap">
                  {col.label}
                </th>
              ))}
              {actions && <th className="px-4 py-3 text-right text-xs font-semibold text-gray-500 uppercase tracking-wide">Actions</th>}
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-50">
            {paged.length === 0 ? (
              <tr><td colSpan={columns.length + (actions ? 1 : 0)} className="text-center py-12 text-gray-400">{emptyMessage}</td></tr>
            ) : paged.map((row, i) => (
              <tr key={i} className="hover:bg-gray-50/50 transition-colors">
                {columns.map(col => (
                  <td key={col.key} className="px-4 py-3 text-gray-700 whitespace-nowrap">
                    {col.render ? col.render(row) : String(row[col.key] ?? '—')}
                  </td>
                ))}
                {actions && <td className="px-4 py-3 text-right">{actions(row)}</td>}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {totalPages > 1 && (
        <div className="px-4 py-3 border-t border-gray-100 flex items-center justify-between text-sm text-gray-500">
          <span>Showing {(page-1)*pageSize+1}–{Math.min(page*pageSize, filtered.length)} of {filtered.length}</span>
          <div className="flex items-center gap-1">
            <button onClick={() => setPage(p => p-1)} disabled={page === 1}
              className="p-1.5 hover:bg-gray-100 rounded-lg disabled:opacity-40 disabled:cursor-not-allowed">
              <ChevronLeft size={16} />
            </button>
            <span className="px-3 py-1 bg-primary-50 text-primary-700 rounded-lg font-medium">{page}</span>
            <button onClick={() => setPage(p => p+1)} disabled={page === totalPages}
              className="p-1.5 hover:bg-gray-100 rounded-lg disabled:opacity-40 disabled:cursor-not-allowed">
              <ChevronRight size={16} />
            </button>
          </div>
        </div>
      )}
    </div>
  )
}
