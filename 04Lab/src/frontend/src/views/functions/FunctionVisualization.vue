<template>
  <div class="visualization-page">
    <!-- Хлебные крошки -->
    <nav class="breadcrumb">
      <router-link to="/functions">
        <i class="fas fa-list"></i> Мои функции
      </router-link>
      <span> / </span>
      <span><i class="fas fa-chart-line"></i> {{ functionName }}</span>
    </nav>

    <!-- Заголовок -->
    <div class="header">
      <h1>{{ functionName }}</h1>
      <div class="function-type" :class="functionType">
        <i :class="functionType === 'analytic' ? 'fas fa-calculator' : 'fas fa-table'"></i>
        {{ functionType === 'analytic' ? 'Аналитическая функция' : 'Табличная функция' }}
      </div>
    </div>

    <!-- Основной контент -->
    <div class="content">
      <!-- График -->
      <div class="chart-section">
        <FunctionChart
          v-if="functionId"
          :function-id="functionId"
          :function-type="functionType"
          :function-name="functionName"
        />
      </div>

      <!-- Информация о функции -->
      <div class="info-section">
        <!-- Детали функции -->
        <div class="info-card">
          <h3><i class="fas fa-info-circle"></i> Информация о функции</h3>
          <div class="info-item">
            <span class="label"><i class="fas fa-hashtag"></i> ID:</span>
            <span class="value">{{ functionId }}</span>
          </div>
          <div class="info-item">
            <span class="label"><i class="fas fa-tag"></i> Тип:</span>
            <span class="value">{{ functionType === 'analytic' ? 'Аналитическая' : 'Табличная' }}</span>
          </div>
          <div v-if="functionType === 'analytic'" class="info-item">
            <span class="label"><i class="fas fa-square-root-alt"></i> Формула:</span>
            <span class="value formula">{{ functionExpression }}</span>
          </div>
          <div v-if="functionType === 'tabulated'" class="info-item">
            <span class="label"><i class="fas fa-dot-circle"></i> Количество точек:</span>
            <span class="value">{{ pointCount }}</span>
          </div>
          <div class="info-item">
            <span class="label"><i class="fas fa-calendar-plus"></i> Создана:</span>
            <span class="value">{{ createdAt }}</span>
          </div>
        </div>

        <!-- Действия -->
        <div class="actions-card">
          <h3><i class="fas fa-bolt"></i> Действия</h3>
          <button @click="goBack" class="btn-back">
            <i class="fas fa-arrow-left"></i> Назад к списку
          </button>
          <button @click="editFunction" class="btn-edit">
            <i class="fas fa-edit"></i> Редактировать
          </button>
          <button @click="confirmDelete" class="btn-delete">
            <i class="fas fa-trash-alt"></i> Удалить
          </button>
          <button @click="exportData" class="btn-export">
            <i class="fas fa-download"></i> Экспорт данных
          </button>
        </div>
      </div>
    </div>

    <!-- Модальное окно для удаления -->
    <div v-if="showDeleteModal" class="modal-overlay">
      <div class="modal">
        <div class="modal-header delete-header">
          <i class="fas fa-exclamation-triangle"></i>
          <h3>Подтверждение удаления</h3>
          <button @click="closeDeleteModal" class="modal-close">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <p>Вы действительно хотите удалить функцию <strong>{{ functionName }}</strong> (ID: {{ functionId }})?</p>
          <p class="warning-text"><i class="fas fa-exclamation-circle"></i> Это действие нельзя будет отменить.</p>
        </div>
        <div class="modal-footer">
          <button @click="closeDeleteModal" class="btn-cancel">
            Отмена
          </button>
          <button @click="performDelete" class="btn-confirm-delete">
            <i class="fas fa-trash-alt"></i> Удалить
          </button>
        </div>
      </div>
    </div>

    <!-- Модальное окно для экспорта -->
    <div v-if="showExportModal" class="modal-overlay">
      <div class="modal export-modal">
        <div class="modal-header export-header">
          <i class="fas fa-gift"></i>
          <h3>С Наступающим!!!!</h3>
          <button @click="closeExportModal" class="modal-close">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body export-body">
          <div class="new-year-content">
            <!-- Новогодняя картинка -->
            <div class="holiday-image">
              <img
                src="/images/new-year.jpg"
                alt="Новый год"
                class="holiday-picture"
              >
              <p class="image-caption">До релиза: скоро:3</p>
            </div>

            <div class="fun-message">
              <p>Над сервисом экспорта ведутся работы</p>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeExportModal" class="btn-ok">
            <i class="fas fa-smile"></i> Ждем с нетерпением!
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import FunctionChart from '@/components/FunctionChart.vue'
import apiClient from '@/api/client'

const route = useRoute()
const router = useRouter()

const functionId = ref(route.params.id)
const functionName = ref('')
const functionType = ref('')
const functionExpression = ref('')
const pointCount = ref(0)
const createdAt = ref('')
const showDeleteModal = ref(false)
const showExportModal = ref(false)

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
  } catch (error) {
    console.error('Ошибка загрузки данных функции:', error)
  }
}

// Действия
const goBack = () => {
  router.push('/functions')
}

const editFunction = () => {
  router.push(`/functions/${functionId.value}/edit`)
}

// Удаление функции через модальное окно
const confirmDelete = () => {
  showDeleteModal.value = true
}

const closeDeleteModal = () => {
  showDeleteModal.value = false
}

const performDelete = async () => {
  try {
    await apiClient.delete(`/api/functions/${functionId.value}`)
    router.push('/functions')
  } catch (error) {
    console.error('Ошибка удаления:', error)
    alert('Не удалось удалить функцию')
    closeDeleteModal()
  }
}

const exportData = () => {
  showExportModal.value = true
}

const closeExportModal = () => {
  showExportModal.value = false
}

onMounted(() => {
  loadFunctionData()
})
</script>

<style scoped>
.visualization-page {
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
  font-size: 12px;
}

.header {
  margin-bottom: 30px;
}

.header h1 {
  margin: 0 0 10px 0;
  color: var(--color-text-primary);
}

.function-type {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 15px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: bold;
}

.function-type.analytic {
  background-color: rgba(52, 152, 219, 0.1);
  color: var(--color-primary);
}

.function-type.tabulated {
  background-color: rgba(155, 89, 182, 0.1);
  color: #9b59b6;
}

.function-type i {
  font-size: 12px;
}

.content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
}

@media (max-width: 1024px) {
  .content {
    grid-template-columns: 1fr;
  }
}

.chart-section {
  background: var(--color-bg-card);
  border-radius: 10px;
  padding: 20px;
  box-shadow: var(--box-shadow);
}

.info-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.info-card, .actions-card {
  background: var(--color-bg-card);
  border-radius: 10px;
  padding: 20px;
  box-shadow: var(--box-shadow);
}

.info-card h3, .actions-card h3 {
  margin: 0 0 20px 0;
  color: var(--color-text-primary);
  display: flex;
  align-items: center;
  gap: 10px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--color-border);
}

.info-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
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
  text-align: right;
}

.value.formula {
  font-family: 'Courier New', monospace;
  background-color: rgba(0, 0, 0, 0.05);
  padding: 5px 10px;
  border-radius: 4px;
  font-size: 14px;
}

.actions-card button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  width: 100%;
  margin-bottom: 10px;
  padding: 12px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
  text-align: center;
  transition: all 0.3s;
}

.actions-card button:hover {
  opacity: 0.9;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.actions-card button:last-child {
  margin-bottom: 0;
}

.btn-back {
  background-color: #6C757D;
  color: white;
}

.btn-edit {
  background-color: #FF9800;
  color: white;
}

.btn-delete {
  background-color: #F44336;
  color: white;
}

.btn-export {
  background-color: #4CAF50;
  color: white;
}

/* Модальное окно для удаления */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: var(--color-bg-card);
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

.modal-header {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px 25px;
  color: white;
}

.modal-header h3 {
  margin: 0;
  flex: 1;
}

.modal-header i {
  font-size: 24px;
}

.delete-header {
  background: linear-gradient(135deg, #F44336 0%, #d32f2f 100%);
}

.modal-close {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  font-size: 20px;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.2s;
}

.modal-close:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.modal-body {
  padding: 25px;
}

.modal-body p {
  margin: 0 0 15px 0;
  color: var(--color-text-primary);
}

.warning-text {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #F44336;
  font-weight: 500;
  padding: 15px;
  background-color: rgba(244, 67, 54, 0.1);
  border-radius: 8px;
}

.modal-footer {
  display: flex;
  justify-content: center;
  padding: 20px 25px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.btn-cancel {
  padding: 10px 25px;
  background: none;
  border: 1px solid var(--color-border);
  color: var(--color-text-secondary);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.btn-confirm-delete {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 25px;
  background: linear-gradient(135deg, #F44336 0%, #d32f2f 100%);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-confirm-delete:hover {
  background: linear-gradient(135deg, #d32f2f 0%, #b71c1c 100%);
}

/* Модальное окно для экспорта */
.export-modal {
  background: linear-gradient(135deg, #1a472a 0%, #2e8b57 100%);
  max-width: 550px;
  border: 3px solid #ffd700;
}

.export-header {
  background: linear-gradient(135deg, #b22222 0%, #8b0000 100%);
  color: white;
  text-align: center;
}

.export-header h3 {
  font-size: 24px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.export-header i {
  font-size: 28px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.export-body {
  padding: 30px;
  text-align: center;
  color: white;
}

.new-year-content {
  position: relative;
}

/* Центральная картинка - УВЕЛИЧЕННАЯ И БЕЗ РАМКИ */
.holiday-image {
  margin: 10px 0 25px 0;
}

.holiday-picture {
  width: 100%;
  max-width: 400px;
  height: auto;
  object-fit: contain;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
  display: block;
  margin: 0 auto;
  background-color: rgba(255, 255, 255, 0.05);
}

.holiday-picture:hover {
  transform: scale(1.02);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.4);
}

.image-caption {
  margin-top: 20px !important;
  font-size: 22px !important;
  font-weight: bold;
  color: #ffd700;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
  background: rgba(139, 0, 0, 0.3);
  padding: 12px 20px;
  border-radius: 10px;
  border: 2px solid #ffd700;
}

.fun-message {
  margin-top: 25px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  border-left: 5px solid #ffd700;
}

.fun-message p {
  font-size: 18px !important;
  font-weight: bold;
  color: #ffd700;
  margin: 0 !important;
}

.btn-ok {
  padding: 10px 25px;
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
  color: #8b0000;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-ok:hover {
  background: linear-gradient(135deg, #ffed4e 0%, #ffd700 100%);
}
</style>