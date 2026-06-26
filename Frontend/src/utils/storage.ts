import { UserDto } from '../types'

const TOKEN_KEY = 'billx_token'
const USER_KEY = 'billx_user'
const STORE_KEY = 'billx_store_id'

export const storage = {
  getToken: () => localStorage.getItem(TOKEN_KEY),
  setToken: (token: string) => localStorage.setItem(TOKEN_KEY, token),
  removeToken: () => localStorage.removeItem(TOKEN_KEY),

  getUser: (): UserDto | null => {
    const u = localStorage.getItem(USER_KEY)
    try { return u ? JSON.parse(u) : null } catch { return null }
  },
  setUser: (user: UserDto) => localStorage.setItem(USER_KEY, JSON.stringify(user)),
  removeUser: () => localStorage.removeItem(USER_KEY),

  getStoreId: (): number | null => {
    const s = localStorage.getItem(STORE_KEY)
    return s ? parseInt(s) : null
  },
  setStoreId: (id: number) => localStorage.setItem(STORE_KEY, String(id)),

  clear: () => {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
    localStorage.removeItem(STORE_KEY)
  },
}
