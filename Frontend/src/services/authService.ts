import api from '../api/axios'
import { AuthResponse, LoginRequest, UserDto } from '../types'

export const authService = {
  login: async (data: LoginRequest) => {
    const res = await api.post<AuthResponse>('/auth/login', data)
    return res.data
  },
  signup: async (data: Partial<UserDto> & { password: string }) => {
    const res = await api.post<AuthResponse>('/auth/signup', data)
    return res.data
  },
}
