/**
 * Хранилище Pinia для управления состоянием аутентификации
 * Сохраняет JWT токен и информацию о пользователе
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Ref } from 'vue'
import { jwtDecode } from 'jwt-decode'
import type { JwtPayload } from 'jwt-decode'
import apiClient from '@/api/client'
import type { LoginRequest, UserRequest, AuthResponse, User } from '@/types/api'

// Интерфейс для декодированного JWT токена (под вашу реализацию)
interface CustomJwtPayload extends JwtPayload {
  sub: string     // Имя пользователя (username)
  roles?: string[] // Роли пользователя
}

export const useAuthStore = defineStore('auth', () => {
  // === СОСТОЯНИЕ ===
  const token: Ref<string | null> = ref(localStorage.getItem('mathfunctions_token'))
  const user: Ref<User | null> = ref(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  // === ГЕТТЕРЫ (computed свойства) ===
  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.isAdmin || false)
  const currentUser = computed(() => user.value)
  const isLoading = computed(() => loading.value)

  // === ДЕЙСТВИЯ (actions) ===

  /**
   * Вход в систему
   * @param credentials - объект с name и password
   * @returns Promise с данными аутентификации
   */
  const login = async (credentials: LoginRequest) => {
    loading.value = true
    error.value = null

    try {
      // Отправляем запрос на /api/auth/login
      const response = await apiClient.post<AuthResponse>('/api/auth/login', credentials)
      const { token: newToken, user: userData } = response.data

      // Сохраняем токен и информацию о пользователе
      setToken(newToken)
      user.value = userData

      console.log('Успешный вход, пользователь:', userData.name)
      return response.data
    } catch (err: any) {
      // Обработка ошибок
      const errorMessage = err.response?.data?.message || 'Ошибка авторизации. Проверьте логин и пароль.'
      error.value = errorMessage
      throw new Error(errorMessage)
    } finally {
      loading.value = false
    }
  }

  /**
   * Регистрация нового пользователя
   * @param userData - данные для регистрации
   * @returns Promise с данными аутентификации
   */
  const register = async (userData: UserRequest) => {
    loading.value = true
    error.value = null

    try {
      // Отправляем запрос на /api/auth/register
      const response = await apiClient.post<AuthResponse>('/api/auth/register', userData)
      const { token: newToken, user: registeredUser } = response.data

      setToken(newToken)
      user.value = registeredUser

      console.log('Успешная регистрация, пользователь:', registeredUser.name)
      return response.data
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Ошибка регистрации'
      error.value = errorMessage
      throw new Error(errorMessage)
    } finally {
      loading.value = false
    }
  }

  /**
   * Выход из системы - очищает токен и данные пользователя
   */
  const logout = () => {
    token.value = null
    user.value = null
    localStorage.removeItem('mathfunctions_token')
    console.log('Выполнен выход из системы')
  }

  /**
   * Установка JWT токена и сохранение в localStorage
   * @param newToken - JWT токен
   */
  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('mathfunctions_token', newToken)

    // Декодируем токен для получения дополнительной информации
    try {
      const decoded = jwtDecode<CustomJwtPayload>(newToken)
      console.log('Декодированный токен:', {
        username: decoded.sub,
        expires: decoded.exp ? new Date(decoded.exp * 1000) : 'нет даты',
        roles: decoded.roles
      })

      // Проверяем срок действия токена
      if (decoded.exp && decoded.exp * 1000 < Date.now()) {
        console.warn('Токен истек, выполняется выход')
        logout()
      }
    } catch (err) {
      console.warn('Ошибка декодирования токена:', err)
    }
  }

  /**
   * Проверка авторизации при загрузке приложения
   * Восстанавливает состояние из localStorage
   */
  const checkAuth = () => {
    const storedToken = localStorage.getItem('mathfunctions_token')

    if (storedToken) {
      try {
        const decoded = jwtDecode<CustomJwtPayload>(storedToken)
        const isExpired = decoded.exp ? decoded.exp * 1000 < Date.now() : false

        if (isExpired) {
          console.log('Токен истек, выполняется выход')
          logout()
        } else {
          token.value = storedToken
          // Информацию о пользователе загрузим при первом обращении к API
          console.log('Авторизация восстановлена из localStorage')
        }
      } catch {
        console.log('Недействительный токен, выполняется выход')
        logout()
      }
    }
  }

  /**
   * Очистка ошибки
   */
  const clearError = () => {
    error.value = null
  }

  // Инициализация при создании store
  checkAuth()

  return {
    // Состояние
    token,
    user,
    loading,
    error,

    // Геттеры
    isAuthenticated,
    isAdmin,
    currentUser,
    isLoading,

    // Действия
    login,
    register,
    logout,
    checkAuth,
    setToken,
    clearError
  }
})