import type { Stats } from '../types';
import { Server, Users, Key, Activity } from 'lucide-react';

interface StatsOverviewProps {
  stats: Stats;
}

export function StatsOverview({ stats }: StatsOverviewProps) {
  const cards = [
    {
      label: 'Servers Online',
      value: stats.totalServersOnline,
      icon: Server,
      color: 'text-emerald-400',
      bg: 'bg-emerald-500/10',
    },
    {
      label: 'Players Online',
      value: stats.totalPlayersOnline,
      icon: Users,
      color: 'text-blue-400',
      bg: 'bg-blue-500/10',
    },
    {
      label: 'Total Licenses',
      value: stats.totalLicenses,
      icon: Key,
      color: 'text-purple-400',
      bg: 'bg-purple-500/10',
    },
    {
      label: 'Active Licenses',
      value: stats.activeLicenses,
      icon: Activity,
      color: 'text-amber-400',
      bg: 'bg-amber-500/10',
    },
  ];

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      {cards.map((card) => (
        <div
          key={card.label}
          className="bg-gray-900 border border-gray-800 rounded-xl p-5 hover:border-gray-700 transition-colors"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-400">{card.label}</p>
              <p className="text-3xl font-bold mt-1">{card.value}</p>
            </div>
            <div className={`${card.bg} p-3 rounded-lg`}>
              <card.icon className={`w-6 h-6 ${card.color}`} />
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}
