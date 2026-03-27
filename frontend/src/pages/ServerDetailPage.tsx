import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getServerDetail } from '../api/client';
import type { ServerDetail } from '../types';
import { StatusBadge } from '../components/StatusBadge';
import { PlayerChart } from '../components/PlayerChart';
import {
  ArrowLeft, Users, Cpu, HardDrive, Clock, Globe,
  Shield, Coffee, MonitorSmartphone, Loader2
} from 'lucide-react';

function formatUptime(ms: number): string {
  const totalSeconds = Math.floor(ms / 1000);
  const days = Math.floor(totalSeconds / 86400);
  const hours = Math.floor((totalSeconds % 86400) / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  if (days > 0) return `${days}d ${hours}h ${minutes}m`;
  if (hours > 0) return `${hours}h ${minutes}m`;
  return `${minutes}m`;
}

export function ServerDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [detail, setDetail] = useState<ServerDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [hours, setHours] = useState(24);

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    getServerDetail(id, hours)
      .then(setDetail)
      .catch(() => navigate('/'))
      .finally(() => setLoading(false));
  }, [id, hours, navigate]);

  if (loading || !detail) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-950">
        <Loader2 className="w-8 h-8 text-brand-500 animate-spin" />
      </div>
    );
  }

  const { server, history } = detail;

  const infoItems = [
    { icon: Users, label: 'Players', value: `${server.playerCount} / ${server.maxPlayers}` },
    { icon: Cpu, label: 'TPS', value: server.tps.toFixed(1) },
    { icon: HardDrive, label: 'Memory', value: `${server.usedMemoryMb} / ${server.maxMemoryMb} MB` },
    { icon: Clock, label: 'Uptime', value: formatUptime(server.uptimeMs) },
    { icon: Globe, label: 'Version', value: `${server.serverSoftware} ${server.mcVersion}` },
    { icon: Shield, label: 'Online Mode', value: server.onlineMode ? 'Yes' : 'No' },
    { icon: Coffee, label: 'Java', value: server.javaVersion },
    { icon: MonitorSmartphone, label: 'OS', value: server.osName },
  ];

  return (
    <div className="min-h-screen bg-gray-950">
      <header className="border-b border-gray-800 bg-gray-950/80 backdrop-blur-sm sticky top-0 z-10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center gap-4">
          <button
            onClick={() => navigate('/')}
            className="p-2 rounded-lg hover:bg-gray-800 transition-colors text-gray-400 hover:text-white"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>
          <div className="flex items-center gap-3">
            <h1 className="text-lg font-semibold">
              {server.serverIp}
              {server.serverPort !== 25565 && (
                <span className="text-gray-500">:{server.serverPort}</span>
              )}
            </h1>
            <StatusBadge status={server.status} />
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
        {/* Server info */}
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-sm font-medium text-gray-400">Server Information</h2>
            <div className="flex items-center gap-2">
              <span className="text-xs px-2.5 py-1 rounded bg-gray-800 text-brand-400 border border-gray-700">
                {server.productId}
              </span>
              <span className="text-xs text-gray-500">v{server.pluginVersion}</span>
            </div>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
            {infoItems.map((item) => (
              <div key={item.label} className="flex items-center gap-3">
                <div className="bg-gray-800 p-2 rounded-lg">
                  <item.icon className="w-4 h-4 text-gray-400" />
                </div>
                <div>
                  <p className="text-xs text-gray-500">{item.label}</p>
                  <p className="text-sm font-medium">{item.value}</p>
                </div>
              </div>
            ))}
          </div>

          <div className="mt-4 pt-4 border-t border-gray-800 flex items-center gap-6 text-xs text-gray-500">
            <span>Owner: <span className="text-gray-300">{server.licenseOwner}</span></span>
            <span>License: <span className="text-gray-300">{server.licenseKey}</span></span>
            <span>First seen: <span className="text-gray-300">{new Date(server.firstSeen).toLocaleDateString()}</span></span>
          </div>
        </div>

        {/* Time range selector */}
        <div className="flex items-center gap-2">
          <span className="text-sm text-gray-400">History:</span>
          {[1, 6, 12, 24, 48, 168].map((h) => (
            <button
              key={h}
              onClick={() => setHours(h)}
              className={`px-3 py-1.5 rounded-md text-xs font-medium transition-colors ${
                hours === h
                  ? 'bg-brand-600 text-white'
                  : 'bg-gray-900 border border-gray-800 text-gray-400 hover:text-white hover:bg-gray-800'
              }`}
            >
              {h < 24 ? `${h}h` : `${h / 24}d`}
            </button>
          ))}
        </div>

        {/* Charts */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
          <PlayerChart data={history} dataKey="playerCount" label="Player Count" color="#34d399" />
          <PlayerChart data={history} dataKey="tps" label="TPS" color="#60a5fa" />
          <PlayerChart data={history} dataKey="usedMemoryMb" label="Memory (MB)" color="#c084fc" />
        </div>
      </main>
    </div>
  );
}
