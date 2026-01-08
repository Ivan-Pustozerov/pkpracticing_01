/**
 * Настроенный экземпляр Axios для работы с API
 * Включает автоматическое добавление JWT токена и обработку ошибок
 */
import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, InternalAxiosRequestConfig } from 'axios'
import { useAuthStore } from '@/stores/auth'

// Базовый URL API (в разработке проксируется через Vite)
const API_BASE_URL = import.meta.env.VITE_API_URL || '/'

// Создаем экземпляр axios с настройками
const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 10000, // 10 секунд таймаут
  withCredentials: false // Для JWT не нужны куки
})

// Интерцептор запросов - добавляем JWT токен в заголовки
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const authStore = useAuthStore()
    const token = authStore.token

    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Интерцептор ответов - обработка ошибок авторизации
apiClient.interceptors.response.use(
  (response) => {
    // Успешный ответ - просто возвращаем
    return response
  },
  (error) => {
    // Обработка ошибки 401 (Unauthorized) - токен истек или недействителен
    if (error.response?.status === 401) {
      const authStore = useAuthStore()
      console.log('Токен истек или недействителен, выполняется выход')
      authStore.logout()

      // Редирект на страницу логина, если мы не на ней
      if (!window.location.pathname.includes('/login')) {
        window.location.href = '/login'
      }
    }

    // Пробрасываем ошибку дальше для обработки в компонентах
    return Promise.reject(error)
  }
)

export default apiClient