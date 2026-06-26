import { useState, useEffect } from 'react'
import { Bell, Sun, Moon } from 'lucide-react'
import { useAuth } from '../../context/AuthContext'
import { formatRole } from '../../utils/formatters'

interface NavbarProps { title: string }

export default function Navbar({ title }: NavbarProps) {
  const { user } = useAuth()
  const [theme, setTheme] = useState(() => localStorage.getItem('theme') ?? 'light')

  useEffect(() => {
    if (theme === 'dark') {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
    localStorage.setItem('theme', theme)
  }, [theme])

  const toggleTheme = () => setTheme(prev => prev === 'light' ? 'dark' : 'light')

  return (
    <header className="bg-white/80 dark:bg-gray-900/80 backdrop-blur-md border-b border-gray-200/80 dark:border-gray-800/80 px-6 py-4 flex items-center justify-between sticky top-0 z-40 transition-colors duration-300">
      <h1 className="text-xl font-bold tracking-tight text-gray-800 dark:text-white">{title}</h1>
      <div className="flex items-center gap-3">
        <button
          onClick={toggleTheme}
          aria-label="Toggle theme"
          className="p-2 text-gray-500 dark:text-gray-400 hover:text-primary-500 dark:hover:text-primary-400 hover:bg-gray-100 dark:hover:bg-gray-800/70 rounded-xl transition-all duration-200 border border-transparent hover:border-gray-200/80 dark:hover:border-gray-800/80 group overflow-hidden"
        >
          <div className="transition-transform duration-300 group-hover:rotate-12">
            {theme === 'light' ? <Moon size={18} /> : <Sun size={18} className="text-amber-500" />}
          </div>
        </button>
        <button className="relative p-2 text-gray-500 dark:text-gray-400 hover:text-primary-500 dark:hover:text-primary-400 hover:bg-gray-100 dark:hover:bg-gray-800/70 rounded-xl transition-all duration-200 border border-transparent hover:border-gray-200/80 dark:hover:border-gray-800/80">
          <Bell size={18} />
          <span className="absolute top-1.5 right-1.5 block h-2 w-2 rounded-full bg-red-500 ring-2 ring-white dark:ring-gray-900" />
        </button>
        <div className="flex items-center gap-2.5 pl-3 border-l border-gray-200 dark:border-gray-800">
          <div className="relative">
            <div className="w-8 h-8 rounded-xl bg-primary-100 dark:bg-primary-950/30 flex items-center justify-center text-primary-700 dark:text-primary-400 font-bold text-sm border border-primary-200/40 dark:border-primary-800/30 shadow-inner">
              {user?.fullName?.[0]?.toUpperCase() ?? 'U'}
            </div>
            <span className="absolute -bottom-0.5 -right-0.5 block h-2.5 w-2.5 rounded-full ring-2 ring-white dark:ring-gray-900 bg-lime-500" />
          </div>
          <div className="hidden sm:block text-left">
            <p className="text-sm font-semibold text-gray-800 dark:text-gray-200 leading-none">{user?.fullName}</p>
            <p className="text-xs text-gray-400 dark:text-gray-500 mt-1 font-medium">{formatRole(user?.role ?? '')}</p>
          </div>
        </div>
      </div>
    </header>
  )
}
