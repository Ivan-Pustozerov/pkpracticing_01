<template>
  <div class="function-detail">
    <!-- Хлебные крошки -->
    <nav class="breadcrumb">
      <router-link to="/functions">
        <i class="fas fa-list"></i> Мои функции
      </router-link>
      <span> / </span>
      <span>{{ functionName }}</span>
    </nav>

    <!-- Заголовок -->
    <div class="header">
      <h1>{{ functionName }}</h1>
      <div class="actions">
        <button @click="goBack" class="btn-back">
          <i class="fas fa-arrow-left"></i> Назад
        </button>
        <button @click="goToVisualization" class="btn-visualize">
          <i class="fas fa-chart-line"></i> Перейти к графику
        </button>
      </div>
    </div>

    <!-- Основная информация -->
    <div class="content">
      <div class="info-card">
        <h2><i class="fas fa-info-circle"></i> Основная информация</h2>
        <div class="info-grid">
          <div class="info-item">
            <span class="label"><i class="fas fa-hashtag"></i> ID:</span>
            <span class="value">{{ functionId }}</span>
          </div>
          <div class="info-item">
            <span class="label"><i class="fas fa-tag"></i> Тип:</span>
            <span class="value type-badge" :class="functionType">
              <i :class="functionType === 'analytic' ? 'fas fa-calculator' : 'fas fa-table'"></i>
              {{ functionType === 'analytic' ? 'Аналитическая' : 'Табличная' }}
            </span>
          </div>
          <div v-if="functionType === 'analytic'" class="info-item">
            <span class="label"><i class="fas fa-square-root-alt"></i> Формула:</span>
            <span class="value formula">{{ functionExpression }}</span>
          </div>
          <div v-if="functionType === 'tabulated'" class="info-item">
            <span class="label"><i class="fas fa-dot-circle"></i> Точек:</span>
            <span class="value">{{ pointCount }}</span>
          </div>
          <div class="info-item">
            <span class="label"><i class="fas fa-calendar-plus"></i> Создана:</span>
            <span class="value">{{ createdAt }}</span>
          </div>
          <div class="info-item">
            <span class="label"><i class="fas fa-user"></i> Владелец:</span>
            <span class="value">{{ ownerName }}</span>
          </div>
        </div>
      </div>

      <!-- Действия -->
      <div class="actions-card">
        <h2><i class="fas fa-bolt"></i> Действия</h2>
        <div class="actions-grid">
          <button @click="goToVisualization" class="btn-action btn-primary">
            <span class="icon"><i class="fas fa-chart-line"></i></span>
            <span class="text">Построить график</span>
          </button>
          <button @click="editFunction" class="btn-action btn-secondary">
            <span class="icon"><i class="fas fa-edit"></i></span>
            <span class="text">Редактировать</span>
          </button>
          <button @click="deleteFunction" class="btn-action btn-danger">
            <span class="icon"><i class="fas fa-trash-alt"></i></span>
            <span class="text">Удалить</span>
          </button>
          <button @click="exportFunction" class="btn-action btn-success">
            <span class="icon"><i class="fas fa-download"></i></span>
            <span class="text">Экспорт</span>
          </button>
          <button @click="shareFunction" class="btn-action btn-info">
            <span class="icon"><i class="fas fa-share-alt"></i></span>
            <span class="text">Поделиться</span>
          </button>
        </div>
      </div>

      <!-- Быстрый предпросмотр графика -->
      <div class="preview-card">
        <h2><i class="fas fa-eye"></i> Быстрый предпросмотр</h2>
        <p>Для полного графика с настройками перейдите по кнопке выше</p>
        <div class="preview-placeholder">
          <div class="preview-text"><i class="fas fa-chart-line fa-2x"></i> График функции</div>
          <button @click="goToVisualization" class="btn-preview">
            <i class="fas fa-external-link-alt"></i> Открыть полный график
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import apiClient from '@/api/client'

const route = useRoute()
const router = useRouter()

const functionId = ref(route.params.id)
const functionName = ref('')
const functionType = ref('')
const functionExpression = ref('')
const pointCount = ref(0)
const createdAt = ref('')
const ownerName = ref('')

// Загрузка данных функции
const loadFunctionData = async () => {
  try {
    const response = await apiClient.get(`/api/functions/${functionId.value}`)
    const data = response.data

    functionName.value = data.name || 'Без названия'
    functionType.value = data.type || 'analytic'

    if (data.type === 'analytic') {
      functionExpression.value = data.functionExpression || 'Не указано'
    } else if (data.type === 'tabulated') {
      pointCount.value = data.xVals?.length || 0
    }

    if (data.createdAt) {
      createdAt.value = new Date(data.createdAt).toLocaleDateString('ru-RU')
    }

    if (data.owner) {
      ownerName.value = data.owner.username || 'Неизвестно'
    }
  } catch (error) {
    console.error('Ошибка загрузки функции:', error)
  }
}

// Навигация
const goBack = () => {
  router.push('/functions')
}

const goToVisualization = () => {
  router.push(`/functions/${functionId.value}/visualize`)
}

const editFunction = () => {
  router.push(`/functions/${functionId.value}/edit`)
}

const deleteFunction = async () => {
  if (confirm('Вы уверены, что хотите удалить эту функцию?')) {
    try {
      await apiClient.delete(`/api/functions/${functionId.value}`)
      router.push('/functions')
    } catch (error) {
      console.error('Ошибка удаления:', error)
      alert('Не удалось удалить функцию')
    }
  }
}

const exportFunction = () => {
  alert('Экспорт (будет реализован позже)')
}

const shareFunction = () => {
  alert('Поделиться (будет реализован позже)')
}

onMounted(() => {
  loadFunctionData()
})
</script>

<style scoped>
.function-detail {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.breadcrumb {
  margin-bottom: 20px;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.breadcrumb a {
  color: var(--color-primary);
  text-decoration: none;
}

.breadcrumb a:hover {
  text-decoration: underline;
}

.breadcrumb i {
  margin-right: 5px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header h1 {
  margin: 0;
  color: var(--color-text-primary);
}

.header .actions {
  display: flex;
  gap: 10px;
}

.header button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-back {
  background-color: #6c757d;
  color: white;
}

.btn-visualize {
  background-color: var(--color-primary);
  color: white;
}

.content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: auto auto;
  gap: 20px;
}

.info-card {
  grid-column: 1;
  grid-row: 1;
  background: var(--color-bg-card);
  border-radius: 10px;
  padding: 20px;
  box-shadow: var(--box-shadow);
}

.info-card h2 {
  margin: 0 0 20px 0;
  color: var(--color-text-primary);
  display: flex;
  align-items: center;
  gap: 10px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.label {
  font-weight: bold;
  color: var(--color-text-secondary);
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.value {
  color: var(--color-text-primary);
}

.type-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
}

.type-badge.analytic {
  background-color: rgba(52, 152, 219, 0.1);
  color: var(--color-primary);
}

.type-badge.tabulated {
  background-color: rgba(155, 89, 182, 0.1);
  color: #9b59b6;
}

.value.formula {
  font-family: 'Courier New', monospace;
  background-color: rgba(0, 0, 0, 0.05);
  padding: 5px 10px;
  border-radius: 4px;
  font-size: 14px;
}

.actions-card {
  grid-column: 2;
  grid-row: 1;
  background: var(--color-bg-card);
  border-radius: 10px;
  padding: 20px;
  box-shadow: var(--box-shadow);
}

.actions-card h2 {
  margin: 0 0 20px 0;
  color: var(--color-text-primary);
  display: flex;
  align-items: center;
  gap: 10px;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.btn-action {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 15px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  color: white;
  transition: transform 0.3s, box-shadow 0.3s;
}

.btn-action:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.2);
}

.btn-primary { background-color: var(--color-primary); }
.btn-secondary { background-color: #6c757d; }
.btn-danger { background-color: #dc3545; }
.btn-success { background-color: #28a745; }
.btn-info { background-color: #17a2b8; }

.icon {
  font-size: 20px;
}

.text {
  font-size: 12px;
  text-align: center;
}

.preview-card {
  grid-column: 1 / span 2;
  grid-row: 2;
  background: var(--color-bg-card);
  border-radius: 10px;
  padding: 20px;
  box-shadow: var(--box-shadow);
}

.preview-card h2 {
  margin: 0 0 10px 0;
  color: var(--color-text-primary);
  display: flex;
  align-items: center;
  gap: 10px;
}

.preview-card p {
  color: var(--color-text-secondary);
  margin: 0 0 20px 0;
}

.preview-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: rgba(0, 0, 0, 0.03);
  border-radius: 8px;
  border: 2px dashed var(--color-border);
}

.preview-text {
  color: var(--color-text-muted);
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
  font-size: 18px;
}

.btn-preview {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background-color: var(--color-primary);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}
</style>