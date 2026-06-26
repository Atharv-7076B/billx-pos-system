export default function LoadingSpinner({ className = 'h-64' }: { className?: string }) {
  return (
    <div className={`flex items-center justify-center ${className}`}>
      <div className="flex flex-col items-center gap-3">
        <div className="w-8 h-8 border-3 border-primary-200 border-t-primary-600 rounded-full animate-spin" style={{ borderWidth: 3 }} />
        <p className="text-sm text-gray-500">Loading…</p>
      </div>
    </div>
  )
}

export function PageLoader() {
  return (
    <div className="fixed inset-0 flex items-center justify-center bg-white z-50">
      <div className="flex flex-col items-center gap-4">
        <div className="w-12 h-12 border-4 border-primary-100 border-t-primary-600 rounded-full animate-spin" />
        <p className="text-gray-500 font-medium">BillX POS</p>
      </div>
    </div>
  )
}
