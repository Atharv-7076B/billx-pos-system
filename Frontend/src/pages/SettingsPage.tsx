import PageHeader from '../components/common/PageHeader'
import { Settings, Bell, Shield, Palette, Globe } from 'lucide-react'

const sections = [
  { icon: Palette, title: 'Appearance', desc: 'Theme, colors, and display preferences', badge: 'Coming soon' },
  { icon: Bell,    title: 'Notifications', desc: 'Alert preferences for low stock and sales', badge: 'Coming soon' },
  { icon: Shield,  title: 'Security', desc: 'Change password, two-factor authentication', badge: 'Coming soon' },
  { icon: Globe,   title: 'Localization', desc: 'Currency, timezone, and language settings', badge: 'Coming soon' },
]

export default function SettingsPage() {
  return (
    <div className="max-w-2xl">
      <PageHeader title="Settings" subtitle="Manage your account and application preferences" />
      <div className="space-y-3">
        {sections.map(({ icon: Icon, title, desc, badge }) => (
          <div key={title} className="card flex items-center gap-4 hover:border-primary-200 transition-colors border cursor-pointer group">
            <div className="p-3 rounded-xl bg-primary-50 group-hover:bg-primary-100 transition-colors">
              <Icon size={20} className="text-primary-600" />
            </div>
            <div className="flex-1 min-w-0">
              <div className="flex items-center gap-2">
                <p className="font-semibold text-gray-800">{title}</p>
                {badge && <span className="badge-gray text-[10px]">{badge}</span>}
              </div>
              <p className="text-sm text-gray-500 mt-0.5">{desc}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
