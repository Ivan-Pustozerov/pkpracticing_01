<template>
  <div class="statistics-view">
    <div class="page-header">
      <h1><i class="fas fa-chart-line"></i> Статистика системы</h1>
      <p class="page-subtitle">Аналитика использования и мониторинг ошибок</p>
    </div>

    <div class="tabs">
      <button
        @click="activeTab = 'users'"
        :class="['tab-btn', activeTab === 'users' ? 'active' : '']"
      >
        <i class="fas fa-users"></i> Пользователи
      </button>
      <button
        @click="activeTab = 'errors'"
        :class="['tab-btn', activeTab === 'errors' ? 'active' : '']"
      >
        <i class="fas fa-exclamation-triangle"></i> Ошибки
      </button>
      <button
        @click="activeTab = 'functions'"
        :class="['tab-btn', activeTab === 'functions' ? 'active' : '']"
      >
        <i class="fas fa-chart-pie"></i> Функции
      </button>
    </div>

    <!-- Вкладка пользователей -->
    <div v-if="activeTab === 'users'" class="tab-content">
      <div class="section-header">
        <h2><i class="fas fa-user-chart"></i> Статистика пользователей</h2>
        <div class="controls">
          <div class="user-selector">
            <label for="user-select"><i class="fas fa-user"></i> Выберите пользователя:</label>
            <select id="user-select" v-model="selectedUserId" @change="loadUserStatistics">
              <option value="" disabled>Выберите пользователя...</option>
              <option v-for="user in availableUsers" :key="user.id" :value="user.id">
                {{ user.name || 'Без имени' }} (ID: {{ user.id }})
              </option>
            </select>
          </div>
          <button @click="refreshData" class="btn btn-sm" :disabled="loadingUser">
            <i class="fas fa-sync-alt" :class="{ 'fa-spin': loadingUser }"></i>
            Обновить
          </button>
        </div>
      </div>

      <div v-if="loadingUser" class="loading">
        <i class="fas fa-spinner fa-spin fa-2x"></i>
        <p>Загрузка статистики...</p>
      </div>

      <div v-else-if="selectedUserId && userStatistic" class="user-statistics">
        <!-- Основные метрики -->
        <div class="metrics-grid">
          <div class="metric-card">
            <div class="metric-icon">
              <i class="fas fa-calendar-plus"></i>
            </div>
            <div class="metric-info">
              <h3>Дата регистрации</h3>
              <p class="metric-value">{{ formatDate(userStatistic.reg_time) }}</p>
              <p class="metric-subtitle">Пользователь в системе</p>
            </div>
          </div>

          <div class="metric-card">
            <div class="metric-icon">
              <i class="fas fa-clock"></i>
            </div>
            <div class="metric-info">
              <h3>Среднее время в день</h3>
              <p class="metric-value">{{ formatDuration(userStatistic.avrg_time_day) }}</p>
              <p class="metric-subtitle">Ежедневная активность</p>
            </div>
          </div>

          <div class="metric-card">
            <div class="metric-icon">
              <i class="fas fa-function"></i>
            </div>
            <div class="metric-info">
              <h3>Количество функций</h3>
              <p class="metric-value">{{ userStatistic.func_count }}</p>
              <p class="metric-subtitle">Создано функций</p>
            </div>
          </div>
        </div>

        <!-- Популярность функций пользователя -->
        <div v-if="userFunctionPopularity.length > 0" class="section">
          <h3><i class="fas fa-star"></i> Популярные функции</h3>
          <div class="popularity-table">
            <div class="table-header">
              <div class="col-name">Название функции</div>
              <div class="col-type">Тип</div>
              <div class="col-count">Открытий</div>
              <div class="col-last">Последний раз</div>
            </div>
            <div v-for="func in userFunctionPopularity" :key="func.function_id" class="table-row">
              <div class="col-name">
                <i :class="func.function_type === 'analytic' ? 'fas fa-square-root-alt' : 'fas fa-table'"></i>
                {{ func.function_name }}
              </div>
              <div class="col-type">
                <span :class="['type-badge', func.function_type]">
                  {{ func.function_type === 'analytic' ? 'Аналитическая' : 'Табличная' }}
                </span>
              </div>
              <div class="col-count">
                <span class="count-badge">{{ func.open_count }}</span>
              </div>
              <div class="col-last">
                {{ func.last_opened ? formatDate(func.last_opened) : 'Нет данных' }}
              </div>
            </div>
          </div>
        </div>

        <div v-else class="empty-section">
          <i class="fas fa-chart-pie fa-3x"></i>
          <p>Нет данных о популярности функций</p>
        </div>
      </div>

      <div v-else class="empty-state">
        <i class="fas fa-chart-bar fa-4x"></i>
        <h3>Выберите пользователя</h3>
        <p>Выберите пользователя из списка, чтобы увидеть статистику</p>
      </div>
    </div>

    <!-- Вкладка ошибок -->
    <div v-if="activeTab === 'errors'" class="tab-content">
      <div class="section-header">
        <h2><i class="fas fa-bug"></i> Статистика ошибок</h2>
        <div class="controls">
          <div class="date-filter">
            <label><i class="fas fa-calendar-alt"></i> Период:</label>
            <select v-model="errorPeriod" @change="loadErrorStatistics">
              <option value="today">Сегодня</option>
              <option value="week">Неделя</option>
              <option value="month">Месяц</option>
              <option value="all">Все время</option>
            </select>
          </div>
          <button @click="refreshErrorData" class="btn btn-sm" :disabled="loadingErrors">
            <i class="fas fa-sync-alt" :class="{ 'fa-spin': loadingErrors }"></i>
            Обновить
          </button>
        </div>
      </div>

      <div v-if="loadingErrors" class="loading">
        <i class="fas fa-spinner fa-spin fa-2x"></i>
        <p>Загрузка статистики ошибок...</p>
      </div>

      <div v-else class="error-statistics">
        <!-- Сводка по ошибкам -->
        <div class="error-summary">
          <div class="summary-card">
            <div class="summary-icon error">
              <i class="fas fa-times-circle"></i>
            </div>
            <div class="summary-info">
              <h3>Всего ошибок</h3>
              <p class="summary-value">{{ errorStatistics.length }}</p>
            </div>
          </div>
          <div class="summary-card">
            <div class="summary-icon warning">
              <i class="fas fa-exclamation-circle"></i>
            </div>
            <div class="summary-info">
              <h3>Ошибок 4xx</h3>
              <p class="summary-value">{{ clientErrors }}</p>
            </div>
          </div>
          <div class="summary-card">
            <div class="summary-icon critical">
              <i class="fas fa-skull-crossbones"></i>
            </div>
            <div class="summary-info">
              <h3>Ошибок 5xx</h3>
              <p class="summary-value">{{ serverErrors }}</p>
            </div>
          </div>
          <div class="summary-card">
            <div class="summary-icon info">
              <i class="fas fa-info-circle"></i>
            </div>
            <div class="summary-info">
              <h3>Последняя ошибка</h3>
              <p class="summary-value">{{ lastErrorTime ? formatTime(lastErrorTime) : 'Нет' }}</p>
            </div>
          </div>
        </div>

        <!-- Таблица ошибок -->
        <div class="error-table">
          <div class="table-header">
            <div class="col-time">Время</div>
            <div class="col-code">Код</div>
            <div class="col-type">Тип ошибки</div>
            <div class="col-severity">Серьезность</div>
          </div>
          <div v-for="error in errorStatistics" :key="error.time" class="table-row">
            <div class="col-time">
              <i class="fas fa-clock"></i>
              {{ formatDateTime(error.time) }}
            </div>
            <div class="col-code">
              <span :class="['code-badge', getErrorSeverity(error.code)]">
                {{ error.code }}
              </span>
            </div>
            <div class="col-type">
              <i class="fas fa-exclamation"></i>
              {{ error.type }}
            </div>
            <div class="col-severity">
              <span :class="['severity-badge', getErrorSeverity(error.code)]">
                {{ getErrorSeverityText(error.code) }}
              </span>
            </div>
          </div>
        </div>

        <div v-if="errorStatistics.length === 0" class="empty-section success">
          <i class="fas fa-check-circle fa-3x"></i>
          <h3>Ошибок не обнаружено!</h3>
          <p>Система работает стабильно за выбранный период</p>
        </div>
      </div>
    </div>

    <!-- Вкладка функций -->
    <div v-if="activeTab === 'functions'" class="tab-content">
      <div class="section-header">
        <h2><i class="fas fa-chart-pie"></i> Популярность функций</h2>
        <div class="controls">
          <button @click="loadTopFunctions" class="btn btn-sm" :disabled="loadingFunctions">
            <i class="fas fa-sync-alt" :class="{ 'fa-spin': loadingFunctions }"></i>
            Обновить
          </button>
        </div>
      </div>

      <div v-if="loadingFunctions" class="loading">
        <i class="fas fa-spinner fa-spin fa-2x"></i>
        <p>Загрузка статистики функций...</p>
      </div>

      <div v-else class="function-statistics">
        <!-- Топ функций -->
        <div class="top-functions">
          <h3><i class="fas fa-crown"></i> Топ-5 популярных функций</h3>

          <div v-if="topFunctions.length > 0" class="top-list">
            <div v-for="(func, index) in topFunctions" :key="func.function_id" class="top-item">
              <div class="rank">{{ index + 1 }}</div>
              <div class="function-info">
                <div class="function-name">
                  <i :class="func.function_type === 'analytic' ? 'fas fa-square-root-alt' : 'fas fa-table'"></i>
                  {{ func.function_name }}
                </div>
                <div class="function-stats">
                  <span class="stat"><i class="fas fa-eye"></i> {{ func.open_count }} открытий</span>
                  <span class="stat"><i class="fas fa-layer-group"></i> {{ func.function_type === 'analytic' ? 'Аналитическая' : 'Табличная' }}</span>
                </div>
              </div>
              <div class="popularity-bar">
                <div
                  class="bar-fill"
                  :style="{ width: calculatePopularityPercentage(func.open_count) + '%' }"
                ></div>
                <span class="bar-label">{{ func.open_count }}</span>
              </div>
            </div>
          </div>

          <div v-else class="empty-section">
            <i class="fas fa-chart-line fa-3x"></i>
            <p>Нет данных о популярности функций</p>
          </div>
        </div>

        <!-- Распределение по типам -->
        <div class="type-distribution">
          <h3><i class="fas fa-chart-pie"></i> Распределение по типам</h3>
          <div class="distribution-chart">
            <div class="pie-chart">
              <div
                v-for="(segment, index) in typeSegments"
                :key="segment.type"
                class="pie-segment"
                :style="getPieSegmentStyle(index, typeSegments.length, segment.count, totalFunctions)"
                :title="`${segment.type}: ${segment.count} функций`"
              ></div>
            </div>
            <div class="chart-legend">
              <div v-for="segment in typeSegments" :key="segment.type" class="legend-item">
                <span class="legend-color" :style="{ backgroundColor: getTypeColor(segment.type) }"></span>
                <span class="legend-label">{{ segment.type === 'analytic' ? 'Аналитические' : 'Табличные' }}</span>
                <span class="legend-count">{{ segment.count }} ({{ segment.percentage }}%)</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Информация о демо-режиме -->
    <div class="demo-notice">
      <i class="fas fa-flask"></i>
      <p><strong>Демо-режим:</strong> Используются моковые данные. Реальные данные появятся после реализации бэкенда.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import type {
  UserStatisticDTO,
  ErrorStatisticDTO,
  FunctionPopularityDTO,
  User
} from '@/types/api'

// Моковые данные пользователей (временно)
const availableUsers: User[] = [
  { id: 1, name: 'Администратор Системы', email: 'admin@example.com', isAdmin: true, createdAt: '2024-01-01T10:00:00Z' },
  { id: 2, name: 'Иван Петров', email: 'ivan.petrov@example.com', isAdmin: false, createdAt: '2024-01-02T14:30:00Z' },
  { id: 3, name: 'Мария Сидорова', email: 'maria.sidorova@example.com', isAdmin: false, createdAt: '2024-01-03T09:15:00Z' },
  { id: 4, name: 'Алексей Смирнов', email: 'alexey.smirnov@example.com', isAdmin: true, createdAt: '2024-01-04T16:45:00Z' },
]

// Моковые данные статистики пользователей
const mockUserStatistics: Record<number, UserStatisticDTO> = {
  1: { id: 1, reg_time: '2024-01-01T10:00:00Z', avrg_time_day: 'PT2H30M', func_count: 15 },
  2: { id: 2, reg_time: '2024-01-02T14:30:00Z', avrg_time_day: 'PT1H45M', func_count: 8 },
  3: { id: 3, reg_time: '2024-01-03T09:15:00Z', avrg_time_day: 'PT3H20M', func_count: 12 },
  4: { id: 4, reg_time: '2024-01-04T16:45:00Z', avrg_time_day: 'PT4H10M', func_count: 20 }
}

// Моковые данные популярности функций
const mockFunctionPopularity: Record<number, FunctionPopularityDTO[]> = {
  1: [
    { function_id: 101, function_name: 'Квадратичная парабола', function_type: 'analytic', open_count: 42, last_opened: '2024-01-16T14:30:00Z' },
    { function_id: 102, function_name: 'Синусоида', function_type: 'analytic', open_count: 38, last_opened: '2024-01-16T13:15:00Z' },
    { function_id: 103, function_name: 'Экспонента', function_type: 'analytic', open_count: 25, last_opened: '2024-01-15T16:45:00Z' },
    { function_id: 104, function_name: 'Экспериментальные данные', function_type: 'tabulated', open_count: 18, last_opened: '2024-01-14T11:20:00Z' },
  ],
  2: [
    { function_id: 201, function_name: 'Линейная функция', function_type: 'analytic', open_count: 28, last_opened: '2024-01-16T10:15:00Z' },
    { function_id: 202, function_name: 'Логарифм', function_type: 'analytic', open_count: 15, last_opened: '2024-01-15T14:30:00Z' },
  ],
  3: [
    { function_id: 301, function_name: 'Гипербола', function_type: 'analytic', open_count: 35, last_opened: '2024-01-16T09:30:00Z' },
  ],
  4: [
    { function_id: 401, function_name: 'Табличные данные 1', function_type: 'tabulated', open_count: 22, last_opened: '2024-01-16T16:20:00Z' },
    { function_id: 402, function_name: 'Табличные данные 2', function_type: 'tabulated', open_count: 18, last_opened: '2024-01-15T11:45:00Z' },
    { function_id: 403, function_name: 'Квадратное уравнение', function_type: 'analytic', open_count: 14, last_opened: '2024-01-14T15:30:00Z' },
  ]
}

// Моковые данные ошибок
const mockErrorStatistics: ErrorStatisticDTO[] = [
  { time: '2024-01-16T09:30:00Z', code: 404, type: 'Функция не найдена' },
  { time: '2024-01-16T10:15:00Z', code: 500, type: 'Внутренняя ошибка сервера' },
  { time: '2024-01-16T11:45:00Z', code: 400, type: 'Некорректный запрос' },
  { time: '2024-01-16T14:20:00Z', code: 403, type: 'Доступ запрещен' },
  { time: '2024-01-15T15:30:00Z', code: 404, type: 'Пользователь не найден' },
  { time: '2024-01-15T16:45:00Z', code: 422, type: 'Ошибка валидации' },
  { time: '2024-01-15T08:15:00Z', code: 500, type: 'Ошибка базы данных' },
  { time: '2024-01-14T09:30:00Z', code: 400, type: 'Неверный формат данных' },
]

// Состояние компонента
const activeTab = ref<'users' | 'errors' | 'functions'>('users')
const selectedUserId = ref<number | null>(1)
const userStatistic = ref<UserStatisticDTO | null>(null)
const userFunctionPopularity = ref<FunctionPopularityDTO[]>([])
const errorStatistics = ref<ErrorStatisticDTO[]>([])
const topFunctions = ref<FunctionPopularityDTO[]>([])
const loadingUser = ref(false)
const loadingErrors = ref(false)
const loadingFunctions = ref(false)
const errorPeriod = ref('week')

// Вычисляемые свойства
const clientErrors = computed(() =>
  errorStatistics.value.filter(e => e.code >= 400 && e.code < 500).length
)

const serverErrors = computed(() =>
  errorStatistics.value.filter(e => e.code >= 500).length
)

const lastErrorTime = computed(() =>
  errorStatistics.value.length > 0
    ? errorStatistics.value[0].time
    : null
)

const totalFunctions = computed(() =>
  topFunctions.value.reduce((sum, func) => sum + func.open_count, 0)
)

const typeSegments = computed(() => {
  const analytics = topFunctions.value.filter(f => f.function_type === 'analytic').length
  const tabulated = topFunctions.value.filter(f => f.function_type === 'tabulated').length

  return [
    { type: 'analytic', count: analytics, percentage: Math.round((analytics / topFunctions.value.length) * 100) },
    { type: 'tabulated', count: tabulated, percentage: Math.round((tabulated / topFunctions.value.length) * 100) }
  ]
})

// Методы
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

const loadUserStatistics = async () => {
  if (!selectedUserId.value) return

  loadingUser.value = true
  await delay(500) // Имитация загрузки

  try {
    // Моковый запрос статистики пользователя
    userStatistic.value = mockUserStatistics[selectedUserId.value] || null

    // Моковый запрос популярности функций
    userFunctionPopularity.value = mockFunctionPopularity[selectedUserId.value] || []
    userFunctionPopularity.value.sort((a, b) => b.open_count - a.open_count)
  } catch (error) {
    console.error('Ошибка загрузки статистики:', error)
    alert('Ошибка при загрузке статистики пользователя')
  } finally {
    loadingUser.value = false
  }
}

const loadErrorStatistics = async () => {
  loadingErrors.value = true
  await delay(500)

  try {
    // Фильтрация по периоду (моковая реализация)
    const now = new Date()
    let filteredErrors = [...mockErrorStatistics]

    if (errorPeriod.value === 'today') {
      const today = now.toISOString().split('T')[0]
      filteredErrors = mockErrorStatistics.filter(e => e.time.startsWith(today))
    } else if (errorPeriod.value === 'week') {
      const weekAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000)
      filteredErrors = mockErrorStatistics.filter(e => new Date(e.time) > weekAgo)
    } else if (errorPeriod.value === 'month') {
      const monthAgo = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000)
      filteredErrors = mockErrorStatistics.filter(e => new Date(e.time) > monthAgo)
    }

    errorStatistics.value = filteredErrors.sort((a, b) =>
      new Date(b.time).getTime() - new Date(a.time).getTime()
    )
  } catch (error) {
    console.error('Ошибка загрузки ошибок:', error)
    alert('Ошибка при загрузке статистики ошибок')
  } finally {
    loadingErrors.value = false
  }
}

const loadTopFunctions = async () => {
  loadingFunctions.value = true
  await delay(600)

  try {
    // Собираем все функции из всех пользователей
    const allFunctions: FunctionPopularityDTO[] = []
    Object.values(mockFunctionPopularity).forEach(userFunctions => {
      allFunctions.push(...userFunctions)
    })

    // Группируем и суммируем
    const functionMap = new Map<number, FunctionPopularityDTO>()
    allFunctions.forEach(func => {
      if (functionMap.has(func.function_id)) {
        const existing = functionMap.get(func.function_id)!
        existing.open_count += func.open_count
      } else {
        functionMap.set(func.function_id, { ...func })
      }
    })

    topFunctions.value = Array.from(functionMap.values())
      .sort((a, b) => b.open_count - a.open_count)
      .slice(0, 5)
  } catch (error) {
    console.error('Ошибка загрузки функций:', error)
    alert('Ошибка при загрузке статистики функций')
  } finally {
    loadingFunctions.value = false
  }
}

const refreshData = async () => {
  if (activeTab.value === 'users') {
    await loadUserStatistics()
  } else if (activeTab.value === 'errors') {
    await loadErrorStatistics()
  } else if (activeTab.value === 'functions') {
    await loadTopFunctions()
  }
}

const refreshErrorData = () => {
  loadErrorStatistics()
}

// Вспомогательные функции форматирования
const formatDate = (dateStr: string): string => {
  return new Date(dateStr).toLocaleDateString('ru-RU', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  })
}

const formatDateTime = (dateStr: string): string => {
  return new Date(dateStr).toLocaleString('ru-RU', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatTime = (dateStr: string): string => {
  return new Date(dateStr).toLocaleTimeString('ru-RU', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatDuration = (duration: string): string => {
  // Простая реализация для ISO Duration
  const match = duration.match(/PT(\d+H)?(\d+M)?/)
  if (!match) return duration

  const hours = match[1] ? parseInt(match[1]) : 0
  const minutes = match[2] ? parseInt(match[2]) : 0

  if (hours > 0 && minutes > 0) {
    return `${hours}ч ${minutes}м`
  } else if (hours > 0) {
    return `${hours}ч`
  } else if (minutes > 0) {
    return `${minutes}м`
  }

  return duration
}

const getErrorSeverity = (code: number): string => {
  if (code >= 500) return 'critical'
  if (code >= 400) return 'error'
  if (code >= 300) return 'warning'
  return 'info'
}

const getErrorSeverityText = (code: number): string => {
  if (code >= 500) return 'Критическая'
  if (code >= 400) return 'Ошибка'
  if (code >= 300) return 'Предупреждение'
  return 'Информация'
}

const calculatePopularityPercentage = (count: number): number => {
  if (topFunctions.value.length === 0) return 0
  const maxCount = Math.max(...topFunctions.value.map(f => f.open_count))
  return Math.round((count / maxCount) * 100)
}

const getTypeColor = (type: string): string => {
  return type === 'analytic' ? '#667eea' : '#f5576c'
}

const getPieSegmentStyle = (index: number, total: number, value: number, totalValue: number) => {
  const percentage = (value / totalValue) * 100
  const startAngle = index === 0 ? 0 : (typeSegments.value.slice(0, index).reduce((sum, s) => sum + (s.count / totalValue * 100), 0) / 100) * 360

  return {
    backgroundColor: getTypeColor(typeSegments.value[index].type),
    transform: `rotate(${startAngle}deg)`,
    clipPath: `conic-gradient(transparent 0%, transparent ${percentage}%, var(--color-bg-card) ${percentage}%)`
  }
}

// Хуки жизненного цикла
onMounted(() => {
  // Загружаем начальные данные
  loadUserStatistics()
  loadErrorStatistics()
  loadTopFunctions()
})
</script>

<style scoped>
.statistics-view {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h1 {
  margin: 0 0 10px 0;
  color: var(--color-text-primary);
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-subtitle {
  color: var(--color-text-secondary);
  margin: 0;
}

.tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
  border-bottom: 2px solid var(--color-border);
  padding-bottom: 10px;
  flex-wrap: wrap;
}

.tab-btn {
  padding: 12px 24px;
  background: none;
  border: none;
  border-radius: 8px 8px 0 0;
  font-weight: 600;
  cursor: pointer;
  color: var(--color-text-secondary);
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all var(--transition-speed);
}

.tab-btn:hover {
  color: var(--color-primary);
  background: var(--color-bg-hover);
}

.tab-btn.active {
  color: var(--color-primary);
  background: var(--color-bg-hover);
  border-bottom: 3px solid var(--color-primary);
}

.tab-content {
  background: var(--color-bg-card);
  border-radius: 15px;
  padding: 30px;
  box-shadow: var(--box-shadow);
  margin-bottom: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  flex-wrap: wrap;
  gap: 15px;
}

.section-header h2 {
  margin: 0;
  color: var(--color-text-primary);
  display: flex;
  align-items: center;
  gap: 10px;
}

.controls {
  display: flex;
  gap: 15px;
  align-items: center;
  flex-wrap: wrap;
}

.user-selector, .date-filter {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-selector label, .date-filter label {
  font-weight: 600;
  color: var(--color-text-primary);
  white-space: nowrap;
}

select {
  padding: 8px 15px;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  background: var(--color-bg-card);
  color: var(--color-text-primary);
  font-size: 14px;
  min-width: 200px;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 8px;
  border: none;
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-speed);
}

.btn-sm {
  padding: 6px 12px;
  font-size: 14px;
}

.btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.loading {
  text-align: center;
  padding: 60px 20px;
}

.loading i {
  color: var(--color-primary);
  margin-bottom: 20px;
}

.loading p {
  color: var(--color-text-secondary);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--color-text-secondary);
}

.empty-state i {
  font-size: 48px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-state h3 {
  margin: 0 0 10px 0;
  color: var(--color-text-primary);
}

/* Метрики пользователя */
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.metric-card {
  background: var(--color-bg-hover);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
  transition: transform var(--transition-speed);
}

.metric-card:hover {
  transform: translateY(-5px);
}

.metric-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-primary), #667eea);
  display: flex;
  align-items: center;
  justify-content: center;
}

.metric-icon i {
  font-size: 24px;
  color: white;
}

.metric-info h3 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: var(--color-text-secondary);
  font-weight: 600;
}

.metric-value {
  margin: 0 0 4px 0;
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-primary);
}

.metric-subtitle {
  margin: 0;
  font-size: 12px;
  color: var(--color-text-muted);
}

/* Таблицы */
.section {
  margin-top: 40px;
}

.section h3 {
  margin: 0 0 20px 0;
  color: var(--color-text-primary);
  display: flex;
  align-items: center;
  gap: 10px;
}

.popularity-table, .error-table {
  border: 1px solid var(--color-border);
  border-radius: 10px;
  overflow: hidden;
}

.table-header {
  display: grid;
  background: var(--color-bg-hover);
  padding: 15px 20px;
  font-weight: 600;
  color: var(--color-text-primary);
  border-bottom: 1px solid var(--color-border);
}

.table-row {
  display: grid;
  padding: 15px 20px;
  align-items: center;
  border-bottom: 1px solid var(--color-border);
  transition: background-color var(--transition-speed);
}

.table-row:hover {
  background-color: var(--color-bg-hover);
}

.table-row:last-child {
  border-bottom: none;
}

/* Для таблицы популярности функций */
.popularity-table .table-header {
  grid-template-columns: 3fr 1fr 1fr 2fr;
}

.popularity-table .table-row {
  grid-template-columns: 3fr 1fr 1fr 2fr;
}

.type-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.type-badge.analytic {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  border: 1px solid rgba(102, 126, 234, 0.3);
}

.type-badge.tabulated {
  background: rgba(245, 87, 108, 0.1);
  color: #f5576c;
  border: 1px solid rgba(245, 87, 108, 0.3);
}

.count-badge {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
  padding: 4px 12px;
  border-radius: 15px;
  font-weight: 600;
  font-size: 14px;
}

/* Для таблицы ошибок */
.error-table .table-header {
  grid-template-columns: 2fr 1fr 3fr 1.5fr;
}

.error-table .table-row {
  grid-template-columns: 2fr 1fr 3fr 1.5fr;
}

.code-badge {
  display: inline-block;
  padding: 6px 12px;
  border-radius: 8px;
  font-weight: 700;
  font-size: 14px;
  color: white;
}

.code-badge.info { background: #3498db; }
.code-badge.warning { background: #f39c12; }
.code-badge.error { background: #e74c3c; }
.code-badge.critical { background: #c0392b; }

.severity-badge {
  display: inline-block;
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.severity-badge.info {
  background: rgba(52, 152, 219, 0.1);
  color: #3498db;
  border: 1px solid rgba(52, 152, 219, 0.3);
}

.severity-badge.warning {
  background: rgba(243, 156, 18, 0.1);
  color: #f39c12;
  border: 1px solid rgba(243, 156, 18, 0.3);
}

.severity-badge.error {
  background: rgba(231, 76, 60, 0.1);
  color: #e74c3c;
  border: 1px solid rgba(231, 76, 60, 0.3);
}

.severity-badge.critical {
  background: rgba(192, 57, 43, 0.1);
  color: #c0392b;
  border: 1px solid rgba(192, 57, 43, 0.3);
}

/* Сводка по ошибкам */
.error-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.summary-card {
  background: var(--color-bg-hover);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
}

.summary-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.summary-icon.error { background: linear-gradient(135deg, #e74c3c, #c0392b); }
.summary-icon.warning { background: linear-gradient(135deg, #f39c12, #e67e22); }
.summary-icon.critical { background: linear-gradient(135deg, #2c3e50, #34495e); }
.summary-icon.info { background: linear-gradient(135deg, #3498db, #2980b9); }

.summary-icon i {
  font-size: 24px;
  color: white;
}

.summary-info h3 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: var(--color-text-secondary);
  font-weight: 600;
}

.summary-value {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-primary);
}

.empty-section {
  text-align: center;
  padding: 40px 20px;
  color: var(--color-text-secondary);
  border: 2px dashed var(--color-border);
  border-radius: 10px;
  margin-top: 20px;
}

.empty-section i {
  font-size: 48px;
  margin-bottom: 15px;
  opacity: 0.5;
}

.empty-section.success {
  border-color: #2ecc71;
  background: rgba(46, 204, 113, 0.05);
}

.empty-section.success i {
  color: #2ecc71;
  opacity: 1;
}

/* Статистика функций */
.function-statistics {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
}

@media (max-width: 1024px) {
  .function-statistics {
    grid-template-columns: 1fr;
  }
}

.top-functions, .type-distribution {
  background: var(--color-bg-hover);
  border-radius: 12px;
  padding: 25px;
}

.top-functions h3, .type-distribution h3 {
  margin: 0 0 20px 0;
  color: var(--color-text-primary);
  display: flex;
  align-items: center;
  gap: 10px;
}

.top-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.top-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: var(--color-bg-card);
  border-radius: 10px;
  border: 1px solid var(--color-border);
}

.rank {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 18px;
}

.function-info {
  flex: 1;
}

.function-name {
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 5px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.function-stats {
  display: flex;
  gap: 15px;
  font-size: 13px;
  color: var(--color-text-secondary);
}

.function-stats .stat {
  display: flex;
  align-items: center;
  gap: 5px;
}

.popularity-bar {
  width: 120px;
  height: 24px;
  background: var(--color-border);
  border-radius: 12px;
  position: relative;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 12px;
  transition: width 1s ease-out;
}

.bar-label {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 12px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

/* Круговая диаграмма */
.distribution-chart {
  display: flex;
  align-items: center;
  gap: 30px;
}

.pie-chart {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  position: relative;
  background: conic-gradient(
    #667eea 0% 60%,
    #f5576c 60% 100%
  );
}

.pie-segment {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
}

.chart-legend {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 4px;
}

.legend-label {
  flex: 1;
  color: var(--color-text-primary);
  font-weight: 600;
}

.legend-count {
  color: var(--color-text-secondary);
  font-size: 14px;
}

/* Демо-уведомление */
.demo-notice {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  padding: 15px 20px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  gap: 15px;
  margin-top: 30px;
}

.demo-notice i {
  font-size: 24px;
}

.demo-notice p {
  margin: 0;
  font-size: 14px;
}

/* Адаптивность */
@media (max-width: 768px) {
  .statistics-view {
    padding: 15px;
  }

  .tabs {
    flex-direction: column;
  }

  .tab-btn {
    width: 100%;
    justify-content: center;
  }

  .section-header {
    flex-direction: column;
    align-items: stretch;
  }

  .controls {
    flex-direction: column;
    align-items: stretch;
  }

  .user-selector, .date-filter {
    flex-direction: column;
    align-items: stretch;
  }

  select {
    width: 100%;
  }

  .metrics-grid {
    grid-template-columns: 1fr;
  }

  .error-summary {
    grid-template-columns: 1fr;
  }

  .popularity-table .table-header,
  .popularity-table .table-row,
  .error-table .table-header,
  .error-table .table-row {
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .distribution-chart {
    flex-direction: column;
    text-align: center;
  }

  .demo-notice {
    flex-direction: column;
    text-align: center;
  }
}

/* Анимации */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.tab-content {
  animation: fadeIn 0.3s ease-out;
}

.fa-spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>