/**
 * TypeScript интерфейсы для работы с API Spring бэкенда
 * Соответствуют DTO классам в Java проекте
 */

// === Пользователи (UserEntity -> UserResponse) ===
export interface User {
  id: number
  name: string      // Имя пользователя
  email: string     // Email
  isAdmin: boolean  // Роль администратора
}

export interface UserRequest {
  name: string      // Имя для регистрации
  email: string     // Email
  password: string  // Пароль
}

// === Аутентификация (LoginRequest/AuthResponse) ===
export interface LoginRequest {
  name: string      // Поле должно называться "name" (не "username") для соответствия JSON
  password: string
}

export interface AuthResponse {
  token: string     // JWT токен
  type: string      // Тип токена (обычно "Bearer")
  user: User        // Информация о пользователе
}

// === Базовый интерфейс математической функции ===
export interface MathFunctionBase {
  id: number
  type: 'analytic' | 'tabulated'  // Тип функции
  name: string                    // Название функции
  ownerId: number                 // ID владельца
}

export interface MathFunctionRequest {
  userId?: number                 // ID пользователя (опционально)
  type: 'analytic' | 'tabulated' // Тип функции
  name: string                   // Название
}

// === Аналитическая функция ===
export interface AnalyticFunctionRequest extends MathFunctionRequest {
  functionExpression: string      // Математическое выражение
}

export interface AnalyticFunctionResponse extends MathFunctionBase {
  functionExpression: string      // Выражение функции
}

// === Табличная функция ===
export interface TabulatedFunctionRequest extends MathFunctionRequest {
  xVals: number[]    // Массив значений X
  yVals: number[]    // Массив значений Y
}

export interface TabulatedFunctionResponse extends MathFunctionBase {
  xVals: number[]    // Значения X
  yVals: number[]    // Значения Y
}

// === Ответ API (обертка) ===
export interface ApiResponse<T = any> {
  data?: T
  message?: string
  status?: string
  timestamp?: string
}

// === Ошибка API ===
export interface ApiError {
  message: string
  status: number
  timestamp: string
  path?: string
}