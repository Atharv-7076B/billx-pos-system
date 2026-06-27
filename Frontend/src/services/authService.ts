import api from "../api/axios";
import { AuthResponse, LoginRequest, UserDto } from "../types";

export const authService = {
  login: async (data: LoginRequest) => {
    const res = await api.post<AuthResponse>("/auth/login", data);
    return res.data;
  },
  signup: async (data: { name: string; email: string; password: string }) => {
    const res = await api.post<AuthResponse>("/auth/register", data);
    return res.data;
  },
};
