import React, { createContext, useContext, useState, useCallback } from 'react'
import { UserDto, AuthResponse } from '../types'
import { storage } from '../utils/storage'

interface AuthContextType {
  user: UserDto | null
  token: string | null
  storeId: number | null
  isAuthenticated: boolean
  login: (res: AuthResponse) => void
  logout: () => void
  setStoreId: (id: number) => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<UserDto | null>(storage.getUser)
  const [token, setToken] = useState<string | null>(storage.getToken)
  const [storeId, setStoreIdState] = useState<number | null>(storage.getStoreId)

  const login = useCallback((res: AuthResponse) => {
    storage.setToken(res.jwt)
    storage.setUser(res.userDto)
    setToken(res.jwt)
    setUser(res.userDto)
    if (res.userDto.storeId) {
      storage.setStoreId(res.userDto.storeId)
      setStoreIdState(res.userDto.storeId)
    }
  }, [])

  const logout = useCallback(() => {
    storage.clear()
    setToken(null)
    setUser(null)
    setStoreIdState(null)
  }, [])

  const setStoreId = useCallback((id: number) => {
    storage.setStoreId(id)
    setStoreIdState(id)
  }, [])

  return (
    <AuthContext.Provider value={{ user, token, storeId, isAuthenticated: !!token, login, logout, setStoreId }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
