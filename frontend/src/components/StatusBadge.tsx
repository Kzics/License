import type { ServerInstance } from '../types';

interface StatusBadgeProps {
  status: ServerInstance['status'];
}

export function StatusBadge({ status }: StatusBadgeProps) {
  const styles = {
    ONLINE: 'bg-emerald-500/20 text-emerald-400 border-emerald-500/30',
    OFFLINE: 'bg-red-500/20 text-red-400 border-red-500/30',
    SHUTDOWN: 'bg-gray-500/20 text-gray-400 border-gray-500/30',
  };

  const dots = {
    ONLINE: 'bg-emerald-400',
    OFFLINE: 'bg-red-400',
    SHUTDOWN: 'bg-gray-400',
  };

  return (
    <span className={`inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-medium border ${styles[status]}`}>
      <span className={`w-1.5 h-1.5 rounded-full ${dots[status]} ${status === 'ONLINE' ? 'animate-pulse' : ''}`} />
      {status}
    </span>
  );
}
