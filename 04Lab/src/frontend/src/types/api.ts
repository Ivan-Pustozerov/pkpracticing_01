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
// === СТАТИСТИКА (новые интерфейсы) ===

// Статистика пользователя (StatisticToClientDTO)
export interface UserStatisticDTO {
  id: number
  reg_time: string           // Время регистрации (ISO строка)
  avrg_time_day: string      // Среднее время в день (Duration ISO)
  func_count: number         // Количество функций
}

// Статистика ошибок (ErrorsDTO)
export interface ErrorStatisticDTO {
  time: string               // Время ошибки (ISO строка)
  code: number               // Код ошибки
  type: string               // Тип/сообщение ошибки
}

// Популярность функций пользователя
export interface FunctionPopularityDTO {
  function_id: number        // ID функции
  function_name: string      // Название функции
  function_type: 'analytic' | 'tabulated'  // Тип функции
  open_count: number         // Количество открытий
  last_opened?: string       // Последнее открытие (ISO строка, опционально)
}

// Детальная статистика по функции
export interface FunctionDetailStatisticDTO {
  function_id: number
  function_name: string
  function_type: 'analytic' | 'tabulated'
  owner_id: number
  owner_name: string
  created_at: string
  last_opened: string
  open_count: number
  total_time_spent: string    // Общее время (Duration ISO)
  avg_time_per_visit: string  // Среднее время за посещение (Duration ISO)
}

// Данные для графика использования по дням
export interface DailyUsageDTO {
  date: string               // Дата в формате YYYY-MM-DD
  visits: number             // Количество посещений
  errors: number             // Количество ошибок
}

// Общая статистика системы
export interface SystemOverviewDTO {
  total_users: number
  total_functions: number
  total_errors: number
  avg_session_duration: string
  most_popular_function?: string
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