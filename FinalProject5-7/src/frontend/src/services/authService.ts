/**
 * Сервис для работы с аутентификацией
 * Простая обертка над API, для более сложной логики используйте store
 */

import apiClient from '@/api/client'
import type { LoginRequest, UserRequest, AuthResponse } from '@/types/api'

class AuthService {
  /**
   * Вход в систему
   * @param credentials - объект с name и password
   * @returns Promise с данными аутентификации
   */
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    try {
      const response = await apiClient.post<AuthResponse>('/api/auth/login', credentials)
      return response.data
    } catch (error) {
      console.error('AuthService login error:', error)
      throw error
    }
  }

  /**
   * Регистрация нового пользователя
   * @param userData - данные для регистрации
   * @returns Promise с данными аутентификации
   */
  async register(userData: UserRequest): Promise<AuthResponse> {
    try {
      const response = await apiClient.post<AuthResponse>('/api/auth/register', userData)
      return response.data
    } catch (error) {
      console.error('AuthService register error:', error)
      throw error
    }
  }

  /**
   * Проверка токена (опционально)
   * Можно отправить тестовый запрос к защищенному эндпоинту
   */
  async validateToken(): Promise<boolean> {
    try {
      await apiClient.get('/api/functions')
      return true
    } catch (error) {
      console.error('Token validation failed:', error)
      return false
    }
  }

  /**
   * Выход из системы (очистка на клиенте)
   * На сервере JWT не хранится, поэтому просто удаляем локально
   */
  logout(): void {
    localStorage.removeItem('mathfunctions_token')
  }
}

// Экспортируем singleton экземпляр
const authService = new AuthService()
export default authService