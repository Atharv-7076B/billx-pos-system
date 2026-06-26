import { Link } from 'react-router-dom'
import { Home, Zap } from 'lucide-react'

export default function NotFoundPage() {
  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
      <div className="text-center">
        <div className="w-16 h-16 bg-primary-100 rounded-2xl flex items-center justify-center mx-auto mb-6">
          <Zap size={32} className="text-primary-600" />
        </div>
        <h1 className="text-7xl font-black text-gray-200 mb-2">404</h1>
        <h2 className="text-2xl font-bold text-gray-800 mb-2">Page not found</h2>
        <p className="text-gray-500 mb-8">The page you're looking for doesn't exist or has been moved.</p>
        <Link to="/" className="btn-primary inline-flex">
          <Home size={16} /> Go to Dashboard
        </Link>
      </div>
    </div>
  )
}
