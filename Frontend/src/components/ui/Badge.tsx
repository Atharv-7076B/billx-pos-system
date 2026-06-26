import { getStatusColor } from '../../utils/formatters'

export default function Badge({ status }: { status: string }) {
  return (
    <span className={getStatusColor(status)}>
      {status?.replace(/_/g, ' ')}
    </span>
  )
}
