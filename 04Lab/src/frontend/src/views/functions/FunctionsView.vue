<template>
  <div class="functions-view">
    <!-- Заголовок -->
    <div class="header">
      <h1><i class="fas fa-chart-line"></i> Мои функции</h1>
      <p>Список всех ваших математических функций</p>
      <button @click="createFunction" class="btn-create">
        <i class="fas fa-plus"></i> Создать новую функцию
      </button>
    </div>

    <!-- Состояние загрузки -->
    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>Загрузка функций...</p>
    </div>

    <!-- Список функций -->
    <div v-else-if="functions.length > 0" class="functions-list">
      <table>
        <thead>
          <tr>
            <th><i class="fas fa-hashtag"></i> ID</th>
            <th><i class="fas fa-font"></i> Название</th>
            <th><i class="fas fa-tag"></i> Тип</th>
            <th><i class="fas fa-calendar"></i> Дата создания</th>
            <th><i class="fas fa-cogs"></i> Действия</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="func in functions" :key="func.id">
            <td class="id">{{ func.id }}</td>
            <td class="name">{{ func.name }}</td>
            <td class="type-cell">
              <span class="type-badge" :class="func.type">
                <i :class="func.type === 'analytic' ? 'fas fa-calculator' : 'fas fa-table'"></i>
                {{ func.type === 'analytic' ? 'Аналитическая' : 'Табличная' }}
              </span>
            </td>
            <td class="date">{{ formatDate(func.createdAt) }}</td>
            <td class="actions">
              <div class="action-buttons">
                <button
                  @click="visualizeFunction(func.id)"
                  class="btn-visualize"
                  title="Построить график"
                >
                  <i class="fas fa-chart-line"></i>
                  <span class="btn-text">График</span>
                </button>
                <button
                  @click="showDetailsUnavailable()"
                  class="btn-details"
                  title="Подробнее"
                >
                  <i class="fas fa-eye"></i>
                </button>
                <button
                  @click="editFunction(func.id)"
                  class="btn-edit"
                  title="Редактировать"
                >
                  <i class="fas fa-edit"></i>
                </button>
                <button
                  @click="confirmDelete(func)"
                  class="btn-delete"
                  title="Удалить"
                >
                  <i class="fas fa-trash-alt"></i>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Пустое состояние -->
    <div v-else class="empty-state">
      <div class="empty-icon"><i class="fas fa-chart-line fa-4x"></i></div>
      <h3>Функций пока нет</h3>
      <p>Создайте свою первую математическую функцию</p>
      <button @click="createFunction" class="btn-create">
        <i class="fas fa-plus"></i> Создать функцию
      </button>
    </div>

    <!-- Модальное окно: Детали недоступны -->
    <div v-if="showDetailsModal" class="modal-overlay">
      <div class="modal">
        <div class="modal-header info-header">
          <i class="fas fa-info-circle"></i>
          <h3>Информация</h3>
          <button @click="closeDetailsModal" class="modal-close">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <p>Детальный просмотр функций пока недоступен.</p>
          <p>Эта функция находится в разработке и будет доступна в следующем обновлении.</p>
        </div>
        <div class="modal-footer">
          <button @click="closeDetailsModal" class="btn-ok">
            Понятно
          </button>
        </div>
      </div>
    </div>

    <!-- Модальное окно: Удаление -->
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
          <p>Вы действительно хотите удалить функцию <strong>{{ functionToDelete?.name }}</strong> (ID: {{ functionToDelete?.id }})?</p>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import apiClient from '@/api/client'

const router = useRouter()
const functions = ref([])
const loading = ref(true)

// Модальные окна
const showDetailsModal = ref(false)
const showDeleteModal = ref(false)
const functionToDelete = ref(null)

// Загрузка функций
const loadFunctions = async () => {
  try {
    const response = await apiClient.get('/api/functions')
    functions.value = response.data
  } catch (error) {
    console.error('Ошибка загрузки функций:', error)
  } finally {
    loading.value = false
  }
}

// Форматирование даты
const formatDate = (dateString) => {
  if (!dateString) return 'Не указано'
  const date = new Date(dateString)
  return date.toLocaleDateString('ru-RU')
}

// Навигация
const visualizeFunction = (id) => {
  router.push(`/functions/${id}/visualize`)
}

const showDetailsUnavailable = () => {
  showDetailsModal.value = true
}

const closeDetailsModal = () => {
  showDetailsModal.value = false
}

const editFunction = (id) => {
  router.push(`/functions/${id}/edit`)
}

const createFunction = () => {
  router.push('/functions/create')
}

// Удаление функции
const confirmDelete = (func) => {
  functionToDelete.value = func
  showDeleteModal.value = true
}

const closeDeleteModal = () => {
  showDeleteModal.value = false
  functionToDelete.value = null
}

const performDelete = async () => {
  if (!functionToDelete.value) return

  try {
    await apiClient.delete(`/api/functions/${functionToDelete.value.id}`)
    loadFunctions()
    closeDeleteModal()
  } catch (error) {
    console.error('Ошибка удаления:', error)
    alert('Не удалось удалить функцию')
  }
}

onMounted(() => {
  loadFunctions()
})
</script>

<style scoped>
.functions-view {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  margin-bottom: 30px;
}

.header h1 {
  margin: 0 0 10px 0;
  color: var(--color-text-primary);
  display: flex;
  align-items: center;
  gap: 10px;
}

.header p {
  color: var(--color-text-secondary);
  margin: 0 0 20px 0;
}

.btn-create {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background-color: var(--color-primary);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s;
}

.btn-create:hover {
  background-color: #45a049;
}

.loading {
  text-align: center;
  padding: 40px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid var(--color-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.functions-list {
  overflow-x: auto;
  border-radius: 8px;
  box-shadow: var(--box-shadow);
  background: var(--color-bg-card);
}

table {
  width: 100%;
  border-collapse: collapse;
  min-width: 800px;
}

th {
  padding: 16px 15px;
  text-align: left;
  background-color: rgba(0, 0, 0, 0.02);
  font-weight: bold;
  color: var(--color-text-primary);
  border-bottom: 2px solid var(--color-border);
  white-space: nowrap;
}

th i {
  margin-right: 8px;
  font-size: 14px;
  opacity: 0.7;
}

td {
  padding: 12px 15px;
  border-bottom: 1px solid var(--color-border);
  vertical-align: middle;
}

tr:last-child td {
  border-bottom: none;
}

tr:hover {
  background-color: rgba(0, 0, 0, 0.02);
}

.id {
  font-family: 'Courier New', monospace;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.name {
  font-weight: 500;
  color: var(--color-text-primary);
}

.type-cell {
  white-space: nowrap;
}

.type-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 13px;
  font-weight: bold;
  white-space: nowrap;
}

.type-badge.analytic {
  background-color: rgba(52, 152, 219, 0.1);
  color: var(--color-primary);
}

.type-badge.tabulated {
  background-color: rgba(155, 89, 182, 0.1);
  color: #9b59b6;
}

.type-badge i {
  font-size: 12px;
}

.date {
  color: var(--color-text-secondary);
  font-size: 14px;
  white-space: nowrap;
}

.actions {
  width: 220px;
}

.action-buttons {
  display: flex;
  gap: 6px;
  align-items: center;
}

.action-buttons button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  padding: 6px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s;
  min-width: 36px;
  height: 36px;
}

.btn-visualize {
  background-color: #2196F3;
  color: white;
  flex: 1;
}

.btn-details {
  background-color: #673AB7;
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

.btn-text {
  display: inline;
}

@media (max-width: 768px) {
  .btn-text {
    display: none;
  }

  .btn-visualize {
    min-width: 36px;
    max-width: 36px;
  }
}

.action-buttons button:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: var(--color-bg-card);
  border-radius: 15px;
  box-shadow: var(--box-shadow);
  margin-top: 30px;
}

.empty-icon {
  margin-bottom: 20px;
  color: var(--color-text-muted);
}

.empty-state h3 {
  color: var(--color-text-primary);
  margin-bottom: 10px;
}

.empty-state p {
  color: var(--color-text-secondary);
  margin-bottom: 30px;
}

/* Стили для модальных окон */
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

.info-header {
  background: var(--color-bg-footer);
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
  justify-content: flex-end;
  gap: 15px;
  padding: 20px 25px;
  border-top: 1px solid var(--color-border);
}

.btn-ok {
  padding: 10px 25px;
  background-color: var(--color-bg-footer);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-ok:hover {
  background-color: #1976D2;
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
</style>