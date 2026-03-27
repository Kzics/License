export interface ServerInstance {
  id: string;
  serverIp: string;
  serverPort: number;
  motd: string;
  playerCount: number;
  maxPlayers: number;
  mcVersion: string;
  serverSoftware: string;
  pluginVersion: string;
  tps: number;
  usedMemoryMb: number;
  maxMemoryMb: number;
  onlineMode: boolean;
  javaVersion: string;
  osName: string;
  uptimeMs: number;
  productId: string;
  status: 'ONLINE' | 'OFFLINE' | 'SHUTDOWN';
  licenseOwner: string;
  licenseKey: string;
  firstSeen: string;
  lastSeen: string;
}

export interface HeartbeatLog {
  playerCount: number;
  tps: number;
  usedMemoryMb: number;
  recordedAt: string;
}

export interface ServerDetail {
  server: ServerInstance;
  history: HeartbeatLog[];
}

export interface ProductStats {
  productId: string;
  serverCount: number;
  playerCount: number;
}

export interface Stats {
  totalServersOnline: number;
  totalPlayersOnline: number;
  totalLicenses: number;
  activeLicenses: number;
  productBreakdown: ProductStats[];
}

export interface LoginResponse {
  token: string;
  username: string;
  expiresIn: number;
}
