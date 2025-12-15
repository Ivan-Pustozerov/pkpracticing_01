<!-- src/components/FunctionChart.vue -->
<template>
  <div class="function-chart">
    <!-- Управление параметрами -->
    <div class="chart-controls">
      <div class="controls-group">
        <h3>Параметры построения</h3>
        <div class="range-inputs">
          <div class="input-group">
            <label>От:</label>
            <input v-model.number="range.from" type="number" step="0.1">
          </div>
          <div class="input-group">
            <label>До:</label>
            <input v-model.number="range.to" type="number" step="0.1">
          </div>
          <div class="input-group">
            <label>Шаг:</label>
            <input v-model.number="range.step" type="number" step="0.01" min="0.01">
          </div>
          <button @click="calculate" :disabled="isCalculating" class="btn-calculate">
            {{ isCalculating ? '🔄 Вычисляем...' : '📈 Построить график' }}
          </button>
          <button @click="resetZoom" class="btn-reset">
            🔄 Сбросить масштаб
          </button>
        </div>
      </div>

      <div class="controls-group">
        <h3>Внешний вид</h3>
        <div class="appearance-controls">
          <div class="input-group">
            <label>Цвет линии:</label>
            <input v-model="lineColor" type="color">
          </div>
          <div class="input-group">
            <label>Толщина:</label>
            <select v-model.number="lineWidth">
              <option value="1">Тонкая</option>
              <option value="2">Средняя</option>
              <option value="3">Толстая</option>
            </select>
          </div>
          <div class="input-group">
            <label>
              <input v-model="showPoints" type="checkbox">
              Показывать точки
            </label>
          </div>
        </div>
      </div>
    </div>

    <!-- Контейнер графика -->
    <div class="chart-wrapper">
      <canvas ref="chartCanvas"></canvas>
    </div>

    <!-- Таблица значений -->
    <div v-if="points.length > 0" class="points-table">
      <div class="table-header">
        <h3>📊 Таблица значений</h3>
        <span class="points-count">{{ points.length }} точек</span>
      </div>
      <div class="table-container">
        <table>
          <thead>
            <tr>
              <th>x</th>
              <th>y = f(x)</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(point, index) in displayedPoints" :key="index">
              <td>{{ point.x.toFixed(2) }}</td>
              <td>{{ point.y.toFixed(2) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-if="points.length > 20" class="table-footer">
        Показано {{ displayedPoints.length }} из {{ points.length }} точек
        <button @click="showAll = !showAll" class="btn-toggle">
          {{ showAll ? 'Скрыть' : 'Показать все' }}
        </button>
      </div>
    </div>

    <!-- Ошибки -->
    <div v-if="error" class="error-message">
      <strong>⚠️ Ошибка:</strong> {{ error }}
    </div>

    <!-- Статус -->
    <div v-if="isCalculating" class="status-message">
      <div class="spinner-small"></div>
      Вычисление графика...
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, computed, nextTick } from 'vue'
import { Chart, registerables } from 'chart.js'
import zoomPlugin from 'chartjs-plugin-zoom'
import apiClient from '@/api/client'

// Регистрация компонентов Chart.js
Chart.register(...registerables, zoomPlugin)

const props = defineProps({
  functionId: {
    type: Number,
    required: true
  },
  functionType: {
    type: String,
    required: true
  },
  functionName: {
    type: String,
    default: 'Функция'
  }
})

const chartCanvas = ref(null)
const chartInstance = ref(null)
const points = ref([])
const error = ref('')
const isCalculating = ref(false)
const showAll = ref(false)

// Диапазон по умолчанию
const range = ref({
  from: -10,
  to: 10,
  step: 0.5
})

// Настройки отображения
const lineColor = ref('#2196F3')
const lineWidth = ref(2)
const showPoints = ref(false)

// Отображаемые точки (первые 20 или все)
const displayedPoints = computed(() => {
  if (showAll.value) return points.value
  return points.value.slice(0, 20)
})

// Вычисление точек графика
const calculate = async () => {
  isCalculating.value = true
  error.value = ''

  try {
    const response = await apiClient.post(`/api/functions/${props.functionId}/calculate`, range.value)
    points.value = response.data

    if (points.value.length === 0) {
      error.value = 'В указанном диапазоне нет точек'
      return
    }

    // Всегда создаем новый график при расчете новых данных
    createChart()
  } catch (err) {
    error.value = err.response?.data?.message || err.message || 'Ошибка при вычислении функции'
    console.error('Ошибка:', err)
  } finally {
    isCalculating.value = false
  }
}

// Создание графика
const createChart = () => {
  // Уничтожаем старый график если есть
  if (chartInstance.value) {
    chartInstance.value.destroy()
    chartInstance.value = null
  }

  // Ждем следующего тика для гарантии что canvas очищен
  nextTick(() => {
    if (!chartCanvas.value) return

    const ctx = chartCanvas.value.getContext('2d')

    // Находим min и max значений для осей
    const xValues = points.value.map(p => p.x)
    const yValues = points.value.map(p => p.y)
    const xMin = Math.min(...xValues)
    const xMax = Math.max(...xValues)
    const yMin = Math.min(...yValues)
    const yMax = Math.max(...yValues)

    // Добавляем отступы
    const xPadding = (xMax - xMin) * 0.1
    const yPadding = (yMax - yMin) * 0.1

    const data = {
      datasets: [{
        label: props.functionName,
        data: points.value.map(p => ({ x: p.x, y: p.y })),
        borderColor: lineColor.value,
        backgroundColor: lineColor.value + '20',
        borderWidth: lineWidth.value,
        tension: 0.1,
        fill: true,
        pointRadius: showPoints.value ? 3 : 0,
        pointHoverRadius: 5,
        pointBackgroundColor: lineColor.value
      }]
    }

    const options = {
      responsive: true,
      maintainAspectRatio: false,
      animation: {
        duration: 0 // Отключаем анимацию для быстрого отклика
      },
      plugins: {
        legend: {
          display: true,
          position: 'top'
        },
        tooltip: {
          callbacks: {
            label: (context) => {
              const point = context.raw
              return `${props.functionName}: (${point.x.toFixed(2)}, ${point.y.toFixed(2)})`
            }
          }
        },
        zoom: {
          pan: {
            enabled: true,
            mode: 'xy',
            modifierKey: null,
            threshold: 1, // Минимальное расстояние для начала перемещения - 1 пиксель
            speed: 1.0, // Нормальная скорость перемещения
            scaleMode: 'xy'
          },
          zoom: {
            wheel: {
              enabled: true,
              speed: 0.1,
              modifierKey: null
            },
            pinch: {
              enabled: true
            },
            drag: {
              enabled: false
            },
            mode: 'xy'
          },
          limits: {
            x: {
              min: xMin - xPadding * 10,
              max: xMax + xPadding * 10
            },
            y: {
              min: yMin - yPadding * 10,
              max: yMax + yPadding * 10
            }
          }
        }
      },
      scales: {
        x: {
          type: 'linear',
          title: {
            display: true,
            text: 'x',
            color: '#666'
          },
          grid: {
            color: 'rgba(0,0,0,0.1)'
          },
          min: xMin - xPadding,
          max: xMax + xPadding
        },
        y: {
          title: {
            display: true,
            text: `f(x)`,
            color: '#666'
          },
          grid: {
            color: 'rgba(0,0,0,0.1)'
          },
          min: yMin - yPadding,
          max: yMax + yPadding
        }
      },
      interaction: {
        intersect: false,
        mode: 'nearest',
        axis: 'xy'
      },
      events: ['mousemove', 'mouseout', 'click', 'touchstart', 'touchmove'],
      hover: {
        intersect: false
      }
    }

    chartInstance.value = new Chart(ctx, {
      type: 'line',
      data: data,
      options: options
    })

    // Добавляем обработчик для предотвращения скролла страницы при зумировании
    const canvas = chartCanvas.value
    const preventScrollHandler = (e) => {
      if (e.ctrlKey) {
        e.preventDefault()
      }
    }
    canvas.addEventListener('wheel', preventScrollHandler, { passive: false })
  })
}

// Обновление стилей графика
const updateChartStyle = () => {
  if (!chartInstance.value || points.value.length === 0) return

  // Обновляем стили
  chartInstance.value.data.datasets[0].borderColor = lineColor.value
  chartInstance.value.data.datasets[0].backgroundColor = lineColor.value + '20'
  chartInstance.value.data.datasets[0].borderWidth = lineWidth.value
  chartInstance.value.data.datasets[0].pointRadius = showPoints.value ? 3 : 0
  chartInstance.value.data.datasets[0].pointBackgroundColor = lineColor.value
  chartInstance.value.data.datasets[0].label = props.functionName

  // Обновляем график без анимации
  chartInstance.value.update('none')
}

// Сброс масштаба
const resetZoom = () => {
  if (chartInstance.value) {
    chartInstance.value.resetZoom()
  }
}

// Автоматическое построение при загрузке
onMounted(() => {
  calculate()
})

// Очистка
onUnmounted(() => {
  if (chartInstance.value) {
    chartInstance.value.destroy()
  }
})

// Реактивность на изменение настроек отображения
watch([lineColor, lineWidth, showPoints], () => {
  if (chartInstance.value && points.value.length > 0) {
    updateChartStyle()
  }
})

// Реактивность на изменение названия функции
watch(() => props.functionName, () => {
  if (chartInstance.value) {
    chartInstance.value.data.datasets[0].label = props.functionName
    chartInstance.value.update('none')
  }
})
</script>

<style scoped>
.function-chart {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

.chart-controls {
  display: flex;
  flex-wrap: wrap;
  gap: 30px;
  margin-bottom: 30px;
  padding: 25px;
  background: #f8f9fa;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.controls-group {
  flex: 1;
  min-width: 350px;
}

.controls-group h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

.range-inputs, .appearance-controls {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: center;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.input-group label {
  font-size: 14px;
  color: #5a6c7d;
  font-weight: 500;
}

.input-group input[type="number"] {
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  width: 120px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.input-group input[type="number"]:focus {
  border-color: #2196F3;
  outline: none;
}

.input-group input[type="color"] {
  width: 60px;
  height: 45px;
  padding: 3px;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
}

.input-group select {
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: white;
  width: 140px;
  font-size: 14px;
  cursor: pointer;
}

button {
  padding: 12px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: 45px;
}

button:hover {
  opacity: 0.9;
  transform: translateY(-2px);
}

button:active {
  transform: translateY(0);
}

button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.btn-calculate {
  background: linear-gradient(135deg, #2196F3, #1976D2);
  color: white;
  box-shadow: 0 4px 12px rgba(33, 150, 243, 0.3);
}

.btn-reset {
  background: linear-gradient(135deg, #6C757D, #5a6268);
  color: white;
  box-shadow: 0 4px 12px rgba(108, 117, 125, 0.3);
}

.btn-toggle {
  background-color: transparent;
  color: #2196F3;
  border: 2px solid #2196F3;
  padding: 6px 12px;
  font-size: 13px;
  height: auto;
}

.chart-wrapper {
  height: 600px;
  width: 100%;
  margin-bottom: 30px;
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  position: relative;
}

.chart-wrapper canvas {
  width: 100% !important;
  height: 100% !important;
}

.points-table {
  margin-top: 40px;
  background: white;
  border-radius: 10px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.table-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 20px;
}

.points-count {
  color: #6C757D;
  font-size: 14px;
  font-weight: 500;
}

.table-container {
  overflow-x: auto;
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid #eaeaea;
  border-radius: 6px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  position: sticky;
  top: 0;
  background: #f8f9fa;
  box-shadow: 0 1px 0 #eee;
}

th, td {
  padding: 12px 20px;
  text-align: center;
  border-bottom: 1px solid #eaeaea;
}

th {
  background-color: #f1f3f5;
  font-weight: 600;
  color: #2c3e50;
  font-size: 15px;
}

td {
  color: #495057;
  font-size: 14px;
}

tbody tr:hover {
  background-color: #f8f9fa;
}

.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 2px solid #eaeaea;
  color: #6C757D;
  font-size: 14px;
  font-weight: 500;
}

.error-message {
  margin-top: 25px;
  padding: 18px;
  background-color: #FFEBEE;
  color: #C62828;
  border-radius: 8px;
  border: 1px solid #FFCDD2;
  font-size: 15px;
}

.status-message {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 15px;
  color: #5a6c7d;
  font-size: 15px;
  font-weight: 500;
}

.spinner-small {
  width: 18px;
  height: 18px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #2196F3;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Адаптивность */
@media (max-width: 768px) {
  .chart-controls {
    flex-direction: column;
    gap: 20px;
  }

  .controls-group {
    min-width: 100%;
  }

  .range-inputs, .appearance-controls {
    flex-direction: column;
    align-items: stretch;
  }

  .input-group input[type="number"],
  .input-group select {
    width: 100%;
  }

  button {
    width: 100%;
    justify-content: center;
  }

  .chart-wrapper {
    height: 400px;
  }
}
</style>