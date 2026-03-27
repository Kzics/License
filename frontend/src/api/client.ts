import type { LoginResponse, ServerDetail, ServerInstance, Stats } from '../types';

const API_BASE = '/api';

function getToken(): string | null {
  return localStorage.getItem('kzics_token');
}

export function setToken(token: string) {
  localStorage.setItem('kzics_token', token);
}

export function clearToken() {
  localStorage.removeItem('kzics_token');
}

export function isAuthenticated(): boolean {
  return getToken() !== null;
}

async function fetchApi<T>(path: string, options?: RequestInit): Promise<T> {
  const token = getToken();
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options?.headers as Record<string, string> || {}),
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers,
  });

  if (response.status === 401) {
    clearToken();
    window.location.href = '/login';
    throw new Error('Unauthorized');
  }

  if (!response.ok) {
    throw new Error(`HTTP ${response.status}: ${response.statusText}`);
  }

  return response.json();
}

export async function login(username: string, password: string): Promise<LoginResponse> {
  const response = await fetchApi<LoginResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  });
  setToken(response.token);
  return response;
}

export async function getStats(): Promise<Stats> {
  return fetchApi<Stats>('/stats');
}

export async function getServers(status?: string): Promise<ServerInstance[]> {
  const query = status ? `?status=${status}` : '';
  return fetchApi<ServerInstance[]>(`/servers${query}`);
}

export async function getServerDetail(id: string, hours = 24): Promise<ServerDetail> {
  return fetchApi<ServerDetail>(`/servers/${id}?hours=${hours}`);
}
