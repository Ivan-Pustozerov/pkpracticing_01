/**
 * Моковый сервис статистики (временное решение)
 * Имитирует работу с API до реализации бэкенда
 */
import type {
  UserStatisticDTO,
  ErrorStatisticDTO,
  FunctionPopularityDTO,
  FunctionDetailStatisticDTO
} from '@/types/statistic'

// Моковые данные статистики пользователей
const mockUserStatistics: Record<number, UserStatisticDTO> = {
  1: {
    id: 1,
    reg_time: '2024-01-01T10:00:00Z',
    avrg_time_day: 'PT2H30M', // 2 часа 30 минут
    func_count: 15
  },
  2: {
    id: 2,
    reg_time: '2024-01-02T14:30:00Z',
    avrg_time_day: 'PT1H45M', // 1 час 45 минут
    func_count: 8
  },
  3: {
    id: 3,
    reg_time: '2024-01-03T09:15:00Z',
    avrg_time_day: 'PT3H20M', // 3 часа 20 минут
    func_count: 12
  },
  4: {
    id: 4,
    reg_time: '2024-01-04T16:45:00Z',
    avrg_time_day: 'PT4H10M', // 4 часа 10 минут
    func_count: 20
  }
}

// Моковые данные ошибок
const mockErrorStatistics: ErrorStatisticDTO[] = [
  { time: '2024-01-15T09:30:00Z', code: 404, type: 'Функция не найдена' },
  { time: '2024-01-15T10:15:00Z', code: 500, type: 'Внутренняя ошибка сервера' },
  { time: '2024-01-15T11:45:00Z', code: 400, type: 'Некорректный запрос' },
  { time: '2024-01-15T14:20:00Z', code: 403, type: 'Доступ запрещен' },
  { time: '2024-01-15T15:30:00Z', code: 404, type: 'Пользователь не найден' },
  { time: '2024-01-15T16:45:00Z', code: 422, type: 'Ошибка валидации' },
  { time: '2024-01-16T08:15:00Z', code: 500, type: 'Ошибка базы данных' },
  { time: '2024-01-16T09:30:00Z', code: 400, type: 'Неверный формат данных' }
]

// Моковые данные популярности функций
const mockFunctionPopularity: Record<number, FunctionPopularityDTO[]> = {
  1: [
    { function_id: 101, function_name: 'Квадратичная парабола', function_type: 'analytic', open_count: 42, last_opened: '2024-01-16T14:30:00Z' },
    { function_id: 102, function_name: 'Синусоида', function_type: 'analytic', open_count: 38, last_opened: '2024-01-16T13:15:00Z' },
    { function_id: 103, function_name: 'Экспонента', function_type: 'analytic', open_count: 25, last_opened: '2024-01-15T16:45:00Z' },
    { function_id: 104, function_name: 'Экспериментальные данные', function_type: 'tabulated', open_count: 18, last_opened: '2024-01-14T11:20:00Z' },
    { function_id: 105, function_name: 'Гипербола', function_type: 'analytic', open_count: 12, last_opened: '2024-01-13T09:30:00Z' }
  ],
  2: [
    { function_id: 201, function_name: 'Линейная функция', function_type: 'analytic', open_count: 28, last_opened: '2024-01-16T10:15:00Z' },
    { function_id: 202, function_name: 'Логарифм', function_type: 'analytic', open_count: 15, last_opened: '2024-01-15T14:30:00Z' },
    { function_id: 203, function_name: 'Табличные данные 1', function_type: 'tabulated', open_count: 8, last_opened: '2024-01-14T16:20:00Z' }
  ]
}

// Моковые детальные данные по функциям
const mockFunctionDetails: Record<number, FunctionDetailStatisticDTO> = {
  101: {
    function_id: 101,
    function_name: 'Квадратичная парабола',
    function_type: 'analytic',
    owner_id: 1,
    owner_name: 'Администратор Системы',
    created_at: '2024-01-05T10:00:00Z',
    last_opened: '2024-01-16T14:30:00Z',
    open_count: 42,
    total_time_spent: 'PT12H45M',
    avg_time_per_visit: 'PT18M'
  },
  102: {
    function_id: 102,
    function_name: 'Синусоида',
    function_type: 'analytic',
    owner_id: 1,
    owner_name: 'Администратор Системы',
    created_at: '2024-01-06T14:30:00Z',
    last_opened: '2024-01-16T13:15:00Z',
    open_count: 38,
    total_time_spent: 'PT10H20M',
    avg_time_per_visit: 'PT16M'
  }
}

class MockStatisticService {
  // Имитация задержки сети
  private delay(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms))
  }

  /**
   * Получить статистику пользователя по ID
   */
  async getUserStatistic(userId: number): Promise<UserStatisticDTO> {
    await this.delay(300) // Имитация сетевой задержки

    const statistic = mockUserStatistics[userId]
    if (!statistic) {
      throw new Error(`Статистика для пользователя с ID ${userId} не найдена`)
    }

    return statistic
  }

  /**
   * Получить статистику ошибок
   */
  async getErrorStatistics(): Promise<ErrorStatisticDTO[]> {
    await this.delay(400)
    return [...mockErrorStatistics].sort((a, b) =>
      new Date(b.time).getTime() - new Date(a.time).getTime()
    )
  }

  /**
   * Получить популярность функций пользователя
   */
  async getUserFunctionPopularity(userId: number): Promise<FunctionPopularityDTO[]> {
    await this.delay(350)

    const popularity = mockFunctionPopularity[userId]
    if (!popularity) {
      throw new Error(`Статистика популярности функций для пользователя с ID ${userId} не найдена`)
    }

    return [...popularity].sort((a, b) => b.open_count - a.open_count)
  }

  /**
   * Получить детальную статистику по функции
   */
  async getFunctionDetailStatistic(functionId: number): Promise<FunctionDetailStatisticDTO> {
    await this.delay(300)

    const detail = mockFunctionDetails[functionId]
    if (!detail) {
      throw new Error(`Детальная статистика для функции с ID ${functionId} не найдена`)
    }

    return detail
  }

  /**
   * Получить топ-5 самых популярных функций среди всех пользователей
   */
  async getTopFunctions(): Promise<FunctionPopularityDTO[]> {
    await this.delay(400)

    const allFunctions: FunctionPopularityDTO[] = []
    Object.values(mockFunctionPopularity).forEach(userFunctions => {
      allFunctions.push(...userFunctions)
    })

    // Группируем по function_id и суммируем open_count
    const functionMap = new Map<number, FunctionPopularityDTO>()
    allFunctions.forEach(func => {
      if (functionMap.has(func.function_id)) {
        const existing = functionMap.get(func.function_id)!
        existing.open_count += func.open_count
      } else {
        functionMap.set(func.function_id, { ...func })
      }
    })

    return Array.from(functionMap.values())
      .sort((a, b) => b.open_count - a.open_count)
      .slice(0, 5)
  }

  /**
   * Получить статистику использования по дням (для графика)
   */
  async getDailyUsage(): Promise<{ date: string; visits: number; errors: number }[]> {
    await this.delay(300)

    // Генерируем данные за последние 7 дней
    const today = new Date()
    const dailyData = []

    for (let i = 6; i >= 0; i--) {
      const date = new Date(today)
      date.setDate(today.getDate() - i)
      const dateStr = date.toISOString().split('T')[0]

      // Случайные данные для демонстрации
      dailyData.push({
        date: dateStr,
        visits: Math.floor(Math.random() * 50) + 20,
        errors: Math.floor(Math.random() * 10) + 1
      })
    }

    return dailyData
  }
}

export default new MockStatisticService()