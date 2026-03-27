import { useEffect, useState, useCallback } from 'react';
import type { ServerInstance, Stats } from '../types';
import { getServers, getStats } from '../api/client';

const REFRESH_INTERVAL = 30_000; // 30 seconds

export function useServers() {
  const [servers, setServers] = useState<ServerInstance[]>([]);
  const [stats, setStats] = useState<Stats | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const refresh = useCallback(async () => {
    try {
      const [serversData, statsData] = await Promise.all([
        getServers(),
        getStats(),
      ]);
      setServers(serversData);
      setStats(statsData);
      setError(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    refresh();
    const interval = setInterval(refresh, REFRESH_INTERVAL);
    return () => clearInterval(interval);
  }, [refresh]);

  return { servers, stats, loading, error, refresh };
}
