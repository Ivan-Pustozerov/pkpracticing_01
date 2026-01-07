<template>
  <div class="create-function-view">
    <div class="page-header">
      <h1><i class="fas fa-plus-circle"></i> Создать функцию</h1>
      <p class="page-subtitle">Добавьте новую математическую функцию</p>
    </div>

    <div v-if="!selectedType" class="function-types">
      <div class="type-card" @click="selectType('analytic')">
        <i class="fas fa-square-root-alt fa-3x"></i>
        <h3>Аналитическая</h3>
        <p>Функция заданная формулой f(x) = ...</p>
        <div class="features">
          <span><i class="fas fa-check-circle"></i> x² + 2x + 1</span>
          <span><i class="fas fa-check-circle"></i> sin(x), cos(x)</span>
          <span><i class="fas fa-check-circle"></i> Автоматический график</span>
        </div>
      </div>

      <div class="type-card" @click="selectType('tabulated')">
        <i class="fas fa-table fa-3x"></i>
        <h3>Табличная</h3>
        <p>Функция заданная таблицей значений</p>
        <div class="features">
          <span><i class="fas fa-check-circle"></i> Ручной ввод точек</span>
          <span><i class="fas fa-check-circle"></i> Импорт данных</span>
          <span><i class="fas fa-check-circle"></i> Эксперименты</span>
        </div>
      </div>
    </div>

    <div v-else class="function-form">
      <div class="form-header">
        <h3>
          <i :class="selectedType === 'analytic' ? 'fas fa-square-root-alt' : 'fas fa-table'"></i>
          Создание {{ selectedType === 'analytic' ? 'аналитической' : 'табличной' }} функции
        </h3>
        <button @click="selectedType = null" class="btn btn-secondary">
          <i class="fas fa-arrow-left"></i> Назад к выбору
        </button>
      </div>

      <!-- Форма аналитической функции -->
      <div v-if="selectedType === 'analytic'" class="analytic-form">
        <div class="form-group">
          <label for="name">
            <i class="fas fa-heading"></i> Название функции *
          </label>
          <input
            id="name"
            v-model="analyticForm.name"
            type="text"
            placeholder="Например: Квадратичная парабола"
            required
          >
        </div>

        <div class="form-group">
          <label for="expression">
            <i class="fas fa-calculator"></i> Математическое выражение *
          </label>
          <div class="expression-input">
            <span class="prefix">f(x) =</span>
            <input
              id="expression"
              v-model="analyticForm.functionExpression"
              type="text"
              placeholder="x^2 + 2*x + 1"
              required
            >
          </div>
          <div class="examples">
            <p class="examples-label">Примеры:</p>
            <div class="example-buttons">
              <button type="button" @click="setExample('x^2')" class="btn-example">
                <i class="fas fa-superscript"></i> x²
              </button>
              <button type="button" @click="setExample('sin(x)')" class="btn-example">
                <i class="fas fa-wave-square"></i> sin(x)
              </button>
              <button type="button" @click="setExample('exp(x)')" class="btn-example">
                <i class="fas fa-chart-line"></i> eˣ
              </button>
              <button type="button" @click="setExample('1/(1+exp(-x))')" class="btn-example">
                <i class="fas fa-sigma"></i> Сигмоида
              </button>
            </div>
          </div>
        </div>

        <div class="form-group">
          <label for="description">
            <i class="fas fa-align-left"></i> Описание (необязательно)
          </label>
          <textarea
            id="description"
            v-model="analyticForm.description"
            rows="3"
            placeholder="Описание вашей функции..."
          ></textarea>
        </div>

        <div class="form-actions">
          <button @click="selectedType = null" class="btn btn-secondary">
            <i class="fas fa-times"></i> Отмена
          </button>
          <button @click="createAnalyticFunction" :disabled="creating" class="btn btn-primary">
            <i class="fas fa-plus"></i> {{ creating ? 'Создание...' : 'Создать функцию' }}
          </button>
        </div>
      </div>

      <!-- Форма табличной функции -->
      <div v-if="selectedType === 'tabulated'" class="tabulated-form">
        <div class="form-group">
          <label for="tab-name">
            <i class="fas fa-heading"></i> Название функции *
          </label>
          <input
            id="tab-name"
            v-model="tabulatedForm.name"
            type="text"
            placeholder="Например: Экспериментальные данные"
            required
          >
        </div>

        <div class="form-group">
          <label>
            <i class="fas fa-list-ol"></i> Таблица значений *
          </label>
          <div class="points-table">
            <div class="table-header">
              <div class="column">x</div>
              <div class="column">y</div>
              <div class="column actions"><i class="fas fa-trash"></i></div>
            </div>
            <div v-for="(point, index) in tabulatedForm.points" :key="index" class="table-row">
              <input
                v-model.number="point.x"
                type="number"
                step="0.01"
                placeholder="x"
                class="point-input"
              >
              <input
                v-model.number="point.y"
                type="number"
                step="0.01"
                placeholder="y"
                class="point-input"
              >
              <button @click="removePoint(index)" class="btn-remove">
                <i class="fas fa-times"></i>
              </button>
            </div>
            <div class="table-actions">
              <button @click="addPoint" class="btn-add">
                <i class="fas fa-plus"></i> Добавить точку
              </button>
              <button @click="clearPoints" class="btn-clear">
                <i class="fas fa-broom"></i> Очистить все
              </button>
            </div>
          </div>
          <p class="help-text">
            <i class="fas fa-info-circle"></i>
            Добавьте как минимум 2 точки для построения графика
          </p>
        </div>

        <div class="quick-import">
          <h4><i class="fas fa-file-import"></i> Быстрый импорт</h4>
          <div class="import-buttons">
            <button @click="importLinear" class="btn-import">
              <i class="fas fa-chart-line"></i> Линейная y = 2x
            </button>
            <button @click="importQuadratic" class="btn-import">
              <i class="fas fa-chart-line"></i> Квадратичная y = x²
            </button>
            <button @click="importSine" class="btn-import">
              <i class="fas fa-wave-square"></i> Синус y = sin(x)
            </button>
          </div>
        </div>

        <div class="form-group">
          <label for="tab-description">
            <i class="fas fa-align-left"></i> Описание (необязательно)
          </label>
          <textarea
            id="tab-description"
            v-model="tabulatedForm.description"
            rows="2"
            placeholder="Описание ваших данных..."
          ></textarea>
        </div>

        <div class="form-actions">
          <button @click="selectedType = null" class="btn btn-secondary">
            <i class="fas fa-times"></i> Отмена
          </button>
          <button
            @click="createTabulatedFunction"
            :disabled="creating || tabulatedForm.points.length < 2"
            class="btn btn-primary"
          >
            <i class="fas fa-plus"></i>
            {{ creating ? 'Создание...' : `Создать (${tabulatedForm.points.length} точек)` }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import apiClient from '@/api/client'

const router = useRouter()
const selectedType = ref<'analytic' | 'tabulated' | null>(null)
const creating = ref(false)

// Данные для аналитической функции
const analyticForm = reactive({
  name: '',
  functionExpression: '',
  description: '',
  type: 'analytic' as const
})

// Данные для табличной функции
const tabulatedForm = reactive({
  name: '',
  points: [{ x: 0, y: 0 }, { x: 1, y: 1 }],
  description: '',
  type: 'tabulated' as const
})

const selectType = (type: 'analytic' | 'tabulated') => {
  selectedType.value = type
}

// Быстрые примеры для аналитической функции
const setExample = (expression: string) => {
  analyticForm.functionExpression = expression
}

// Управление точками для табличной функции
const addPoint = () => {
  tabulatedForm.points.push({ x: 1, y: 0 })
}

const removePoint = (index: number) => {
  tabulatedForm.points.splice(index, 1)
}

const clearPoints = () => {
  tabulatedForm.points = [{ x: 0, y: 0 },{ x: 1, y: 0 }]
}

// Быстрый импорт данных
const importLinear = () => {
  tabulatedForm.points = []
  for (let i = 0; i < 10; i++) {
    tabulatedForm.points.push({ x: i, y: 2 * i })
  }
}

const importQuadratic = () => {
  tabulatedForm.points = []
  for (let i = -5; i <= 5; i++) {
    tabulatedForm.points.push({ x: i, y: i * i })
  }
}

const importSine = () => {
  tabulatedForm.points = []
  for (let i = 0; i < 20; i++) {
    const x = i * 0.5
    tabulatedForm.points.push({ x, y: Math.sin(x) })
  }
}

// Создание аналитической функции
const createAnalyticFunction = async () => {
  if (!analyticForm.name || !analyticForm.functionExpression) {
    alert('Заполните обязательные поля')
    return
  }

  creating.value = true

  try {
    const response = await apiClient.post('/api/functions/analytic', {
      name: analyticForm.name,
      functionExpression: analyticForm.functionExpression,
      type: 'analytic',
      description: analyticForm.description
    })

    alert('Функция успешно создана!')
    router.push(`/functions/${response.data.id}/visualize`)

  } catch (error: any) {
    console.error('Ошибка создания функции:', error)
    alert(error.response?.data?.message || 'Ошибка при создании функции')
  } finally {
    creating.value = false
  }
}

// Создание табличной функции
const createTabulatedFunction = async () => {
  if (!tabulatedForm.name || tabulatedForm.points.length < 2) {
    alert('Заполните название и добавьте минимум 2 точки')
    return
  }

  creating.value = true

  try {
    // Преобразуем точки в массивы xVals и yVals
    const xVals = tabulatedForm.points.map(p => p.x)
    const yVals = tabulatedForm.points.map(p => p.y)

      console.log('Отправка табличной функции:')
      console.log('xVals:', xVals)
      console.log('yVals:', yVals)
      console.log('Тип xVals:', typeof xVals[0])

      const response = await apiClient.post('/api/functions/tabulated', {
        name: tabulatedForm.name,
        type: 'tabulated',
        xvals: xVals,  // Убедитесь что это массив чисел
        yvals: yVals,  // Убедитесь что это массив чисел
        description: tabulatedForm.description
     })

    alert('Табличная функция успешно создана!')
    router.push(`/functions/${response.data.id}/visualize`)

  } catch (error: any) {
    console.error('Ошибка создания функции:', error)
    alert(error.response?.data?.message || 'Ошибка при создании функции')
  } finally {
    creating.value = false
  }
}
</script>

<style scoped>
.create-function-view {
  padding: 20px;
  max-width: 1000px;
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

.function-types {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 25px;
  margin-top: 30px;
}

.type-card {
  background: var(--color-bg-card);
  border-radius: 15px;
  padding: 30px;
  text-align: center;
  box-shadow: var(--box-shadow);
  cursor: pointer;
  transition: all var(--transition-speed);
  border: 2px solid transparent;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.type-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--box-shadow-lg);
  border-color: var(--color-primary);
}

.type-card i {
  font-size: 48px;
  color: var(--color-primary);
  margin-bottom: 20px;
}

.type-card h3 {
  color: var(--color-text-primary);
  margin-bottom: 10px;
}

.type-card p {
  color: var(--color-text-secondary);
  margin: 0 0 15px 0;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 8px;
  text-align: left;
  width: 100%;
  margin-top: 15px;
}

.features span {
  color: var(--color-text-secondary);
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.features i {
  font-size: 12px;
  color: #2ecc71; /* Зеленый для галочек */
  margin: 0;
}

.function-form {
  background: var(--color-bg-card);
  border-radius: 15px;
  padding: 30px;
  margin-top: 30px;
  box-shadow: var(--box-shadow);
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.form-header h3 {
  margin: 0;
  color: var(--color-text-primary);
  display: flex;
  align-items: center;
  gap: 10px;
}

.form-group {
  margin-bottom: 25px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: var(--color-text-primary);
  display: flex;
  align-items: center;
  gap: 8px;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  font-size: 16px;
  background-color: var(--color-bg-card);
  color: var(--color-text-primary);
  transition: border-color var(--transition-speed);
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: var(--color-primary);
}

.form-group input::placeholder,
.form-group textarea::placeholder {
  color: var(--color-text-muted);
  opacity: 0.7;
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
  font-family: inherit;
}

.expression-input {
  display: flex;
  align-items: center;
  gap: 10px;
}

.prefix {
  font-weight: 600;
  color: var(--color-text-primary);
  white-space: nowrap;
}

.expression-input input {
  flex: 1;
}

.examples {
  margin-top: 15px;
}

.examples-label {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 0 0 10px 0;
}

.example-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.btn-example {
  padding: 8px 15px;
  background: var(--color-bg-hover);
  border: 1px solid var(--color-border);
  border-radius: 6px;
  color: var(--color-text-primary);
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all var(--transition-speed);
}

.btn-example:hover {
  background: var(--color-bg-active);
  border-color: var(--color-primary);
}

.points-table {
  border: 1px solid var(--color-border);
  border-radius: 8px;
  overflow: hidden;
  background: var(--color-bg-card);
}

.table-header {
  display: grid;
  grid-template-columns: 1fr 1fr 50px;
  background: var(--color-bg-hover);
  padding: 12px 15px;
  font-weight: 600;
  color: var(--color-text-primary);
  border-bottom: 1px solid var(--color-border);
}

.column.actions {
  text-align: center;
}

.table-row {
  display: grid;
  grid-template-columns: 1fr 1fr 50px;
  padding: 10px 15px;
  border-bottom: 1px solid var(--color-border);
  align-items: center;
  transition: background-color var(--transition-speed);
}

.table-row:hover {
  background-color: var(--color-bg-hover);
}

.table-row:last-child {
  border-bottom: none;
}

.point-input {
  border: 1px solid var(--color-border);
  border-radius: 4px;
  padding: 8px;
  width: 100%;
  background-color: var(--color-bg-card);
  color: var(--color-text-primary);
  transition: border-color var(--transition-speed);
}

.point-input:focus {
  outline: none;
  border-color: var(--color-primary);
}

.point-input::placeholder {
  color: var(--color-text-muted);
}

.btn-remove {
  background: none;
  border: none;
  color: #e74c3c;
  cursor: pointer;
  padding: 5px;
  transition: color var(--transition-speed);
}

.btn-remove:hover {
  color: #c0392b;
}

.table-actions {
  padding: 15px;
  background: var(--color-bg-hover);
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  border-top: 1px solid var(--color-border);
}

.btn-add, .btn-clear {
  padding: 8px 15px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  transition: all var(--transition-speed);
}

.btn-add {
  background: var(--color-primary);
  color: white;
}

.btn-add:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.btn-clear {
  background: var(--color-text-muted);
  color: white;
}

.btn-clear:hover {
  opacity: 0.9;
}

.help-text {
  margin-top: 8px;
  font-size: 14px;
  color: var(--color-text-secondary);
  display: flex;
  align-items: center;
  gap: 8px;
}

.help-text i {
  color: var(--color-primary);
}

.quick-import {
  margin: 25px 0;
  padding: 20px;
  background: var(--color-bg-hover);
  border-radius: 8px;
  border: 1px solid var(--color-border);
}

.quick-import h4 {
  margin: 0 0 15px 0;
  color: var(--color-text-primary);
  display: flex;
  align-items: center;
  gap: 10px;
}

.import-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.btn-import {
  padding: 10px 15px;
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: 6px;
  color: var(--color-text-primary);
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all var(--transition-speed);
}

.btn-import:hover {
  background: var(--color-bg-active);
  border-color: var(--color-primary);
}

.form-actions {
  display: flex;
  gap: 15px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid var(--color-border);
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 8px;
  border: none;
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-speed);
  font-family: inherit;
}

.btn-primary {
  background: var(--gradient-primary);
  color: var(--color-text-light);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.btn-secondary {
  background: var(--color-text-muted);
  color: var(--color-text-light);
}

.btn-secondary:hover:not(:disabled) {
  background: var(--color-text-secondary);
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none !important;
  box-shadow: none !important;
}

/* Адаптивность */
@media (max-width: 768px) {
  .function-types {
    grid-template-columns: 1fr;
  }

  .form-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .form-header h3 {
    font-size: 1.2rem;
  }

  .form-actions {
    flex-direction: column;
  }

  .btn {
    width: 100%;
    justify-content: center;
  }

  .import-buttons {
    flex-direction: column;
  }

  .btn-import {
    width: 100%;
    justify-content: center;
  }
}

/* Анимации */
.type-card {
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Кастомный скроллбар */
.points-table::-webkit-scrollbar {
  width: 6px;
}

.points-table::-webkit-scrollbar-track {
  background: var(--color-bg-hover);
  border-radius: 3px;
}

.points-table::-webkit-scrollbar-thumb {
  background: var(--color-primary);
  border-radius: 3px;
}

.points-table::-webkit-scrollbar-thumb:hover {
  background: var(--color-primary-dark, #2980b9);
}

/* Подсветка обязательных полей */
.form-group input:required,
.form-group textarea:required {
  border-left: 3px solid var(--color-primary);
}

/* Подсветка при ошибке */
.form-group input:invalid:not(:focus):not(:placeholder-shown),
.form-group textarea:invalid:not(:focus):not(:placeholder-shown) {
  border-color: #e74c3c;
  background-color: rgba(231, 76, 60, 0.05);
}
</style>