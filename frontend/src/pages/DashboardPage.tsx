import { useState } from 'react';
import { useServers } from '../hooks/useServers';
import { StatsOverview } from '../components/StatsOverview';
import { ServerCard } from '../components/ServerCard';
import { clearToken } from '../api/client';
import { useNavigate } from 'react-router-dom';
import { RefreshCw, LogOut, Filter, Loader2, AlertCircle } from 'lucide-react';

type StatusFilter = 'ALL' | 'ONLINE' | 'OFFLINE' | 'SHUTDOWN';

export function DashboardPage() {
  const { servers, stats, loading, error, refresh } = useServers();
  const [filter, setFilter] = useState<StatusFilter>('ALL');
  const [refreshing, setRefreshing] = useState(false);
  const navigate = useNavigate();

  const handleRefresh = async () => {
    setRefreshing(true);
    await refresh();
    setRefreshing(false);
  };

  const handleLogout = () => {
    clearToken();
    navigate('/login');
  };

  const filteredServers = filter === 'ALL'
    ? servers
    : servers.filter((s) => s.status === filter);

  const onlineCount = servers.filter((s) => s.status === 'ONLINE').length;
  const offlineCount = servers.filter((s) => s.status !== 'ONLINE').length;

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-950">
        <Loader2 className="w-8 h-8 text-brand-500 animate-spin" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-950">
        <div className="text-center">
          <AlertCircle className="w-12 h-12 text-red-400 mx-auto mb-4" />
          <p className="text-red-400 text-lg">{error}</p>
          <button onClick={handleRefresh} className="mt-4 text-brand-400 hover:underline">
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-950">
      {/* Top nav */}
      <header className="border-b border-gray-800 bg-gray-950/80 backdrop-blur-sm sticky top-0 z-10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-lg bg-brand-600 flex items-center justify-center font-bold text-sm">
              K
            </div>
            <h1 className="text-lg font-semibold">Kzics Dashboard</h1>
          </div>

          <div className="flex items-center gap-3">
            <button
              onClick={handleRefresh}
              className="p-2 rounded-lg hover:bg-gray-800 transition-colors text-gray-400 hover:text-white"
              title="Refresh"
            >
              <RefreshCw className={`w-4 h-4 ${refreshing ? 'animate-spin' : ''}`} />
            </button>
            <button
              onClick={handleLogout}
              className="p-2 rounded-lg hover:bg-gray-800 transition-colors text-gray-400 hover:text-red-400"
              title="Logout"
            >
              <LogOut className="w-4 h-4" />
            </button>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
        {/* Stats */}
        {stats && <StatsOverview stats={stats} />}

        {/* Product breakdown */}
        {stats && stats.productBreakdown.length > 0 && (
          <div className="bg-gray-900 border border-gray-800 rounded-xl p-5">
            <h2 className="text-sm font-medium text-gray-400 mb-3">Product Breakdown</h2>
            <div className="flex flex-wrap gap-3">
              {stats.productBreakdown.map((p) => (
                <div
                  key={p.productId}
                  className="bg-gray-800 border border-gray-700 rounded-lg px-4 py-2.5 flex items-center gap-4"
                >
                  <span className="font-medium text-brand-400">{p.productId}</span>
                  <span className="text-sm text-gray-400">
                    {p.serverCount} server{p.serverCount !== 1 ? 's' : ''}
                  </span>
                  <span className="text-sm text-gray-500">
                    {p.playerCount} player{p.playerCount !== 1 ? 's' : ''}
                  </span>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Filter + Server list */}
        <div>
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold">
              Servers
              <span className="text-gray-500 text-sm ml-2">
                ({onlineCount} online, {offlineCount} offline)
              </span>
            </h2>
            <div className="flex items-center gap-1 bg-gray-900 border border-gray-800 rounded-lg p-1">
              {(['ALL', 'ONLINE', 'OFFLINE', 'SHUTDOWN'] as StatusFilter[]).map((f) => (
                <button
                  key={f}
                  onClick={() => setFilter(f)}
                  className={`px-3 py-1.5 rounded-md text-xs font-medium transition-colors ${
                    filter === f
                      ? 'bg-brand-600 text-white'
                      : 'text-gray-400 hover:text-white hover:bg-gray-800'
                  }`}
                >
                  {f}
                </button>
              ))}
            </div>
          </div>

          {filteredServers.length === 0 ? (
            <div className="bg-gray-900 border border-gray-800 rounded-xl p-12 text-center">
              <Filter className="w-10 h-10 text-gray-700 mx-auto mb-3" />
              <p className="text-gray-500">No servers found with this filter.</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
              {filteredServers.map((server) => (
                <ServerCard key={server.id} server={server} />
              ))}
            </div>
          )}
        </div>
      </main>
    </div>
  );
}
