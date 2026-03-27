import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import type { HeartbeatLog } from '../types';

interface PlayerChartProps {
  data: HeartbeatLog[];
  dataKey: 'playerCount' | 'tps' | 'usedMemoryMb';
  label: string;
  color: string;
}

export function PlayerChart({ data, dataKey, label, color }: PlayerChartProps) {
  const chartData = data.map((d) => ({
    ...d,
    time: new Date(d.recordedAt).toLocaleTimeString('fr-FR', {
      hour: '2-digit',
      minute: '2-digit',
    }),
  }));

  if (chartData.length === 0) {
    return (
      <div className="bg-gray-900 border border-gray-800 rounded-xl p-5">
        <h3 className="text-sm font-medium text-gray-400 mb-4">{label}</h3>
        <div className="h-48 flex items-center justify-center text-gray-600">
          No data available
        </div>
      </div>
    );
  }

  return (
    <div className="bg-gray-900 border border-gray-800 rounded-xl p-5">
      <h3 className="text-sm font-medium text-gray-400 mb-4">{label}</h3>
      <ResponsiveContainer width="100%" height={200}>
        <LineChart data={chartData}>
          <CartesianGrid strokeDasharray="3 3" stroke="#1f2937" />
          <XAxis
            dataKey="time"
            stroke="#4b5563"
            fontSize={11}
            tickLine={false}
            interval="preserveStartEnd"
          />
          <YAxis
            stroke="#4b5563"
            fontSize={11}
            tickLine={false}
            axisLine={false}
          />
          <Tooltip
            contentStyle={{
              backgroundColor: '#111827',
              border: '1px solid #374151',
              borderRadius: '8px',
              color: '#fff',
            }}
          />
          <Line
            type="monotone"
            dataKey={dataKey}
            stroke={color}
            strokeWidth={2}
            dot={false}
            activeDot={{ r: 4, stroke: color, fill: '#111827', strokeWidth: 2 }}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}
