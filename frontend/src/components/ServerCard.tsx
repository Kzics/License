import { useNavigate } from 'react-router-dom';
import type { ServerInstance } from '../types';
import { StatusBadge } from './StatusBadge';
import { Users, Cpu, HardDrive, Clock } from 'lucide-react';

interface ServerCardProps {
  server: ServerInstance;
}

function formatUptime(ms: number): string {
  const hours = Math.floor(ms / 3600000);
  const minutes = Math.floor((ms % 3600000) / 60000);
  if (hours > 24) {
    const days = Math.floor(hours / 24);
    return `${days}d ${hours % 24}h`;
  }
  return `${hours}h ${minutes}m`;
}

function formatTps(tps: number): { text: string; color: string } {
  const clamped = Math.min(20, tps);
  const text = clamped.toFixed(1);
  if (clamped >= 19) return { text, color: 'text-emerald-400' };
  if (clamped >= 15) return { text, color: 'text-yellow-400' };
  return { text, color: 'text-red-400' };
}

function timeAgo(isoDate: string): string {
  const diff = Date.now() - new Date(isoDate).getTime();
  const seconds = Math.floor(diff / 1000);
  if (seconds < 60) return `${seconds}s ago`;
  const minutes = Math.floor(seconds / 60);
  if (minutes < 60) return `${minutes}m ago`;
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `${hours}h ago`;
  const days = Math.floor(hours / 24);
  return `${days}d ago`;
}

export function ServerCard({ server }: ServerCardProps) {
  const navigate = useNavigate();
  const tps = formatTps(server.tps);
  const memPercent = server.maxMemoryMb > 0
    ? Math.round((server.usedMemoryMb / server.maxMemoryMb) * 100)
    : 0;

  return (
    <div
      onClick={() => navigate(`/servers/${server.id}`)}
      className="bg-gray-900 border border-gray-800 rounded-xl p-5 hover:border-brand-600 transition-all cursor-pointer group"
    >
      {/* Header */}
      <div className="flex items-start justify-between mb-4">
        <div className="min-w-0 flex-1">
          <div className="flex items-center gap-2 mb-1">
            <h3 className="text-lg font-semibold truncate group-hover:text-brand-400 transition-colors">
              {server.serverIp}
              {server.serverPort !== 25565 && (
                <span className="text-gray-500">:{server.serverPort}</span>
              )}
            </h3>
          </div>
          <div className="flex items-center gap-2">
            <span className="text-xs px-2 py-0.5 rounded bg-gray-800 text-gray-400 border border-gray-700">
              {server.productId}
            </span>
            <span className="text-xs text-gray-500">
              v{server.pluginVersion}
            </span>
          </div>
        </div>
        <StatusBadge status={server.status} />
      </div>

      {/* Metrics grid */}
      <div className="grid grid-cols-2 gap-3">
        <div className="flex items-center gap-2">
          <Users className="w-4 h-4 text-gray-500" />
          <span className="text-sm">
            <span className="text-white font-medium">{server.playerCount}</span>
            <span className="text-gray-500">/{server.maxPlayers}</span>
          </span>
        </div>

        <div className="flex items-center gap-2">
          <Cpu className="w-4 h-4 text-gray-500" />
          <span className={`text-sm font-medium ${tps.color}`}>
            {tps.text} TPS
          </span>
        </div>

        <div className="flex items-center gap-2">
          <HardDrive className="w-4 h-4 text-gray-500" />
          <span className="text-sm">
            <span className="text-white font-medium">{server.usedMemoryMb}</span>
            <span className="text-gray-500">/{server.maxMemoryMb} MB</span>
            <span className="text-gray-600 ml-1">({memPercent}%)</span>
          </span>
        </div>

        <div className="flex items-center gap-2">
          <Clock className="w-4 h-4 text-gray-500" />
          <span className="text-sm text-gray-400">
            {formatUptime(server.uptimeMs)}
          </span>
        </div>
      </div>

      {/* Footer */}
      <div className="mt-4 pt-3 border-t border-gray-800 flex items-center justify-between">
        <span className="text-xs text-gray-500">
          {server.serverSoftware} {server.mcVersion}
        </span>
        <span className="text-xs text-gray-600">
          {server.status === 'ONLINE' ? `Last: ${timeAgo(server.lastSeen)}` : `Seen: ${timeAgo(server.lastSeen)}`}
        </span>
      </div>
    </div>
  );
}
