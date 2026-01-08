<template>
  <div class="users-view">
    <div class="page-header">
      <h1><i class="fas fa-users-cog"></i> Управление пользователями</h1>
      <p class="page-subtitle">Панель администратора для управления учетными записями</p>
    </div>

    <div v-if="!users.length && !loading" class="empty-state">
      <i class="fas fa-user-shield fa-4x"></i>
      <h3>Список пользователей пуст</h3>
      <p>Нажмите кнопку ниже, чтобы загрузить данные</p>
      <button @click="loadUsers" class="btn btn-primary">
        <i class="fas fa-sync-alt"></i> Загрузить пользователей
      </button>
    </div>

    <div v-else class="users-content">
      <div class="toolbar">
        <div class="toolbar-info">
          <p><i class="fas fa-info-circle"></i> Всего: {{ users.length }} пользователей</p>
        </div>
        <div class="toolbar-actions">
          <button @click="showCreateModal = true" class="btn btn-primary">
            <i class="fas fa-user-plus"></i> Создать пользователя
          </button>
          <button @click="loadUsers" class="btn btn-secondary">
            <i class="fas fa-sync-alt"></i> Обновить
          </button>
        </div>
      </div>

      <div v-if="loading" class="loading">
        <i class="fas fa-spinner fa-spin fa-3x"></i>
        <p>Загрузка пользователей...</p>
      </div>

      <div v-else class="users-table">
        <div class="table-header">
          <div class="column name">Имя</div>
          <div class="column email">Email</div>
          <div class="column role">Роль</div>
          <div class="column status">Состояние</div>
          <div class="column actions">Действия</div>
        </div>

        <div v-for="user in users" :key="user.id" class="user-row">
          <div class="column name">
            <div class="user-avatar">
              <i class="fas fa-user-circle"></i>
            </div>
            <div class="user-info">
              <strong>{{ user.name || 'Без имени' }}</strong>
              <small>ID: {{ user.id }}</small>
            </div>
          </div>
          <div class="column email">
            {{ user.email }}
          </div>
          <div class="column role">
            <span :class="['role-badge', user.isAdmin ? 'admin' : 'user']">
              <i :class="user.isAdmin ? 'fas fa-shield-alt' : 'fas fa-user'"></i>
              {{ user.isAdmin ? 'Администратор' : 'Пользователь' }}
            </span>
          </div>
          <div class="column status">
            <span class="status-badge">
              <i class="fas fa-tools"></i> В разработке
            </span>
          </div>
          <div class="column actions">
            <div class="action-buttons">
              <button @click="editUser(user)" class="btn-action edit" title="Редактировать">
                <i class="fas fa-edit"></i>
              </button>
              <button
                @click="toggleUserRole(user)"
                :class="['btn-action', user.isAdmin ? 'demote' : 'promote']"
                :title="user.isAdmin ? 'Понизить до пользователя' : 'Повысить до администратора'"
              >
                <i :class="user.isAdmin ? 'fas fa-user' : 'fas fa-shield-alt'"></i>
              </button>
              <button @click="confirmDeleteUser(user)" class="btn-action delete" title="Удалить">
                <i class="fas fa-trash"></i>
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="stats-card">
        <div class="stat">
          <i class="fas fa-users"></i>
          <div class="stat-info">
            <h3>{{ users.length }}</h3>
            <p>Всего пользователей</p>
          </div>
        </div>
        <div class="stat">
          <i class="fas fa-shield-alt"></i>
          <div class="stat-info">
            <h3>{{ adminCount }}</h3>
            <p>Администраторов</p>
          </div>
        </div>
        <div class="stat">
          <i class="fas fa-user"></i>
          <div class="stat-info">
            <h3>{{ userCount }}</h3>
            <p>Обычных пользователей</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Модальное окно создания/редактирования пользователя -->
    <div v-if="showCreateModal" class="modal-overlay">
      <div class="modal">
        <div class="modal-header">
          <h3>
            <i class="fas" :class="editingUser ? 'fa-user-edit' : 'fa-user-plus'"></i>
            {{ editingUser ? 'Редактировать пользователя' : 'Новый пользователь' }}
          </h3>
          <button @click="closeModal" class="btn-close">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <div class="modal-body">
          <div class="form-group">
            <label for="name">
              <i class="fas fa-user"></i> Имя пользователя *
            </label>
            <input
              id="name"
              v-model="userForm.name"
              type="text"
              placeholder="Введите имя пользователя"
              required
            >
            <p class="help-text">Логин</p>
          </div>

          <div class="form-group">
            <label for="email">
              <i class="fas fa-envelope"></i> Email адрес *
            </label>
            <input
              id="email"
              v-model="userForm.email"
              type="email"
              placeholder="user@example.com"
              required
            >
            <p class="help-text">Почта</p>
          </div>

          <div v-if="!editingUser" class="form-group">
            <label for="password">
              <i class="fas fa-lock"></i> Пароль *
            </label>
            <input
              id="password"
              v-model="userForm.password"
              type="password"
              placeholder="Придумайте пароль"
              required
            >
            <p class="help-text">Минимум 6 символов</p>
          </div>

          <div class="form-group">
            <label>
              <i class="fas fa-user-tag"></i> Права доступа
            </label>
            <div class="role-selector">
              <button
                @click="userForm.isAdmin = false"
                :class="['role-option', !userForm.isAdmin ? 'selected' : '']"
                type="button"
              >
                <i class="fas fa-user"></i>
                <span>Обычный пользователь</span>
                <p>Доступ к основным функциям</p>
              </button>
              <button
                @click="userForm.isAdmin = true"
                :class="['role-option', userForm.isAdmin ? 'selected' : '']"
                type="button"
              >
                <i class="fas fa-shield-alt"></i>
                <span>Администратор</span>
                <p>BOSS OF this GYM</p>
              </button>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button @click="closeModal" class="btn btn-secondary">
            <i class="fas fa-times"></i> Отмена
          </button>
          <button
            @click="saveUser"
            :disabled="saving"
            class="btn btn-primary"
          >
            <i class="fas" :class="saving ? 'fa-spinner fa-spin' : 'fa-save'"></i>
            {{ saving ? 'Сохранение...' : (editingUser ? 'Сохранить' : 'Создать') }}
          </button>
        </div>
      </div>
    </div>

    <!-- Модальное окно подтверждения удаления -->
    <div v-if="showDeleteModal" class="modal-overlay">
      <div class="modal confirm-modal">
        <div class="modal-header">
          <h3><i class="fas fa-exclamation-triangle"></i> Подтверждение удаления</h3>
          <button @click="closeDeleteModal" class="btn-close">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <div class="modal-body">
          <div class="warning-message">
            <i class="fas fa-trash-alt fa-3x"></i>
            <h4>Удалить пользователя?</h4>
            <p>Вы действительно хотите удалить <strong>{{ userToDelete?.name || 'пользователя без имени' }}</strong> ({{ userToDelete?.email }})?</p>
            <p class="text-warning"><i class="fas fa-exclamation-circle"></i> Это действие нельзя отменить!</p>
          </div>
        </div>

        <div class="modal-footer">
          <button @click="closeDeleteModal" class="btn btn-secondary">
            <i class="fas fa-times"></i> Отмена
          </button>
          <button
            @click="deleteUser"
            :disabled="deleting"
            class="btn btn-danger"
          >
            <i class="fas" :class="deleting ? 'fa-spinner fa-spin' : 'fa-trash'"></i>
            {{ deleting ? 'Удаление...' : 'Удалить' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import UserService from '@/services/UserService'
import type { User, UserRequest } from '@/types/api'

// Состояние
const users = ref<User[]>([])
const loading = ref(false)
const saving = ref(false)
const deleting = ref(false)
const showCreateModal = ref(false)
const showDeleteModal = ref(false)
const editingUser = ref<User | null>(null)
const userToDelete = ref<User | null>(null)

// Форма пользователя
const userForm = reactive({
  name: '',
  email: '',
  password: '',
  isAdmin: false
})

// Вычисляемые свойства
const adminCount = computed(() =>
  users.value.filter(user => user.isAdmin).length
)

const userCount = computed(() =>
  users.value.filter(user => !user.isAdmin).length
)

// Методы
const loadUsers = async () => {
  loading.value = true
  try {
    users.value = await UserService.getAllUsers()
  } catch (error: any) {
    console.error('Ошибка загрузки пользователей:', error)
    alert(error.response?.data?.message || 'Ошибка при загрузке пользователей')
  } finally {
    loading.value = false
  }
}

const openCreateModal = () => {
  resetUserForm()
  editingUser.value = null
  showCreateModal.value = true
}

const editUser = (user: User) => {
  resetUserForm()
  editingUser.value = user
  Object.assign(userForm, {
    name: user.name || '',
    email: user.email,
    isAdmin: user.isAdmin
  })
  showCreateModal.value = true
}

const saveUser = async () => {
  // Валидация
  if (!userForm.name.trim()) {
    alert('Введите имя пользователя')
    return
  }

  if (!userForm.email.trim()) {
    alert('Введите email адрес')
    return
  }

  if (!editingUser.value && !userForm.password.trim()) {
    alert('Введите пароль для нового пользователя')
    return
  }

  saving.value = true
  try {
    if (editingUser.value) {
      // Обновление пользователя
      await UserService.updateUser(editingUser.value.id, userForm)
      await loadUsers()
      alert('Пользователь успешно обновлен')
    } else {
      // Создание пользователя
      await UserService.createUser(userForm as UserRequest, userForm.isAdmin)
      await loadUsers()
      alert('Пользователь успешно создан')
    }
    closeModal()
  } catch (error: any) {
    console.error('Ошибка сохранения пользователя:', error)
    alert(error.response?.data?.message || 'Ошибка при сохранении пользователя')
  } finally {
    saving.value = false
  }
}

const toggleUserRole = async (user: User) => {
  const action = user.isAdmin ? 'понизить до обычного пользователя' : 'повысить до администратора'
  if (!confirm(`Вы уверены, что хотите ${action} "${user.name || user.email}"?`)) {
    return
  }

  try {
    await UserService.changeUserRole(user.id, !user.isAdmin)
    await loadUsers()
    alert(`Роль пользователя успешно изменена`)
  } catch (error: any) {
    console.error('Ошибка изменения роли:', error)
    alert(error.response?.data?.message || 'Ошибка при изменении роли пользователя')
  }
}

const confirmDeleteUser = (user: User) => {
  userToDelete.value = user
  showDeleteModal.value = true
}

const deleteUser = async () => {
  if (!userToDelete.value) return

  deleting.value = true
  try {
    await UserService.deleteUser(userToDelete.value.id)
    await loadUsers()
    alert('Пользователь удален')
    closeDeleteModal()
  } catch (error: any) {
    console.error('Ошибка удаления пользователя:', error)
    alert(error.response?.data?.message || 'Ошибка при удалении пользователя')
  } finally {
    deleting.value = false
  }
}

const resetUserForm = () => {
  Object.assign(userForm, {
    name: '',
    email: '',
    password: '',
    isAdmin: false
  })
}

const closeModal = () => {
  showCreateModal.value = false
  editingUser.value = null
  resetUserForm()
}

const closeDeleteModal = () => {
  showDeleteModal.value = false
  userToDelete.value = null
}

// Хуки жизненного цикла
onMounted(() => {
  // Можно включить автозагрузку при необходимости:
  // loadUsers()
})
</script>

<style scoped>
.users-view {
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

.empty-state {
  background: var(--color-bg-card);
  border-radius: 15px;
  padding: 60px 40px;
  text-align: center;
  margin-top: 30px;
  box-shadow: var(--box-shadow);
}

.empty-state i {
  font-size: 48px;
  color: var(--color-primary);
  margin-bottom: 20px;
  opacity: 0.8;
}

.empty-state h3 {
  color: var(--color-text-primary);
  margin-bottom: 10px;
}

.empty-state p {
  color: var(--color-text-secondary);
  margin-bottom: 30px;
  max-width: 500px;
  margin-left: auto;
  margin-right: auto;
}

.users-content {
  margin-top: 30px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  flex-wrap: wrap;
  gap: 15px;
  background: var(--color-bg-card);
  padding: 15px 20px;
  border-radius: 10px;
  box-shadow: var(--box-shadow);
}

.toolbar-info {
  color: var(--color-text-secondary);
  font-size: 14px;
}

.toolbar-info i {
  color: var(--color-primary);
  margin-right: 8px;
}

.toolbar-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
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

.users-table {
  background: var(--color-bg-card);
  border-radius: 15px;
  overflow: hidden;
  box-shadow: var(--box-shadow);
  margin-bottom: 30px;
}

.table-header {
  display: grid;
  grid-template-columns: 2fr 2fr 1fr 1fr 0.8fr;
  background: var(--color-bg-hover);
  padding: 15px 20px;
  font-weight: 600;
  color: var(--color-text-primary);
  border-bottom: 1px solid var(--color-border);
}

.user-row {
  display: grid;
  grid-template-columns: 2fr 2fr 1fr 1fr 0.8fr;
  padding: 15px 20px;
  align-items: center;
  border-bottom: 1px solid var(--color-border);
  transition: background-color var(--transition-speed);
}

.user-row:hover {
  background-color: var(--color-bg-hover);
}

.user-row:last-child {
  border-bottom: none;
}

.column {
  padding: 0 10px;
}

.column.name {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-avatar i {
  font-size: 40px;
  color: var(--color-primary);
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-info strong {
  color: var(--color-text-primary);
  margin-bottom: 4px;
  font-size: 16px;
}

.user-info small {
  color: var(--color-text-muted);
  font-size: 12px;
  opacity: 0.8;
}

.role-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 15px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  width: fit-content;
}

.role-badge.admin {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.role-badge.user {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 15px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  background: linear-gradient(135deg, #f6d365 0%, #fda085 100%);
  color: white;
  width: fit-content;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.btn-action {
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all var(--transition-speed);
  color: white;
}

.btn-action.edit {
  background: var(--color-primary);
}

.btn-action.promote {
  background: #f39c12;
}

.btn-action.demote {
  background: #3498db;
}

.btn-action.delete {
  background: #e74c3c;
}

.btn-action:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.btn-action:active {
  transform: translateY(0);
}

.stats-card {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 30px;
}

.stat {
  background: var(--color-bg-card);
  border-radius: 15px;
  padding: 25px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: var(--box-shadow);
  transition: transform var(--transition-speed);
}

.stat:hover {
  transform: translateY(-5px);
}

.stat i {
  font-size: 40px;
  color: var(--color-primary);
}

.stat-info h3 {
  font-size: 32px;
  margin: 0;
  color: var(--color-text-primary);
  font-weight: 700;
}

.stat-info p {
  margin: 5px 0 0 0;
  color: var(--color-text-secondary);
  font-size: 14px;
}

/* Модальные окна */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
  backdrop-filter: blur(3px);
}

.modal {
  background: var(--color-bg-card);
  border-radius: 15px;
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  animation: modalFadeIn 0.3s ease-out;
}

@keyframes modalFadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid var(--color-border);
}

.modal-header h3 {
  margin: 0;
  color: var(--color-text-primary);
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
}

.btn-close {
  background: none;
  border: none;
  font-size: 20px;
  color: var(--color-text-muted);
  cursor: pointer;
  padding: 5px;
  transition: color var(--transition-speed);
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-close:hover {
  background: var(--color-bg-hover);
  color: var(--color-text-primary);
}

.modal-body {
  padding: 20px;
}

.modal-footer {
  padding: 20px;
  border-top: 1px solid var(--color-border);
  display: flex;
  justify-content: flex-end;
  gap: 15px;
}

.confirm-modal .modal-body {
  text-align: center;
  padding: 40px 20px;
}

.warning-message i {
  color: #e74c3c;
  margin-bottom: 20px;
  opacity: 0.8;
}

.warning-message h4 {
  color: var(--color-text-primary);
  margin-bottom: 15px;
  font-size: 18px;
}

.warning-message p {
  color: var(--color-text-secondary);
  margin-bottom: 10px;
  line-height: 1.5;
}

.text-warning {
  color: #f39c12 !important;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
  margin-top: 15px;
  padding: 10px;
  background: rgba(243, 156, 18, 0.1);
  border-radius: 8px;
}

/* Форма в модальном окне */
.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: var(--color-text-primary);
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
}

.form-group input {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  font-size: 15px;
  background-color: var(--color-bg-card);
  color: var(--color-text-primary);
  transition: all var(--transition-speed);
}

.form-group input:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
}

.help-text {
  margin: 8px 0 0 0;
  font-size: 13px;
  color: var(--color-text-muted);
  line-height: 1.4;
}

.role-selector {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  margin-top: 10px;
}

.role-option {
  background: var(--color-bg-hover);
  border: 2px solid var(--color-border);
  border-radius: 10px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all var(--transition-speed);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.role-option:hover {
  border-color: var(--color-primary);
  transform: translateY(-2px);
}

.role-option.selected {
  border-color: var(--color-primary);
  background: rgba(52, 152, 219, 0.1);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.15);
}

.role-option i {
  font-size: 32px;
  color: var(--color-primary);
  margin-bottom: 10px;
}

.role-option span {
  display: block;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 5px;
  font-size: 14px;
}

.role-option p {
  margin: 0;
  font-size: 12px;
  color: var(--color-text-muted);
  line-height: 1.4;
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
  font-size: 15px;
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
  transform: translateY(-2px);
}

.btn-danger {
  background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
  color: white;
}

.btn-danger:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(231, 76, 60, 0.3);
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none !important;
  box-shadow: none !important;
}

/* Адаптивность */
@media (max-width: 1024px) {
  .table-header,
  .user-row {
    grid-template-columns: 1.5fr 1.5fr 1fr 1fr 0.8fr;
  }
}

@media (max-width: 768px) {
  .users-view {
    padding: 15px;
  }

  .toolbar {
    flex-direction: column;
    align-items: stretch;
    text-align: center;
  }

  .toolbar-info {
    order: 2;
    margin-top: 10px;
  }

  .toolbar-actions {
    order: 1;
    justify-content: center;
  }

  .table-header {
    display: none;
  }

  .user-row {
    grid-template-columns: 1fr;
    gap: 15px;
    padding: 20px;
    border-bottom: 2px solid var(--color-border);
  }

  .column {
    padding: 0;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .column::before {
    content: attr(data-label);
    font-weight: 600;
    color: var(--color-text-primary);
    margin-right: 10px;
  }

  .column.name::before { content: "Имя:"; }
  .column.email::before { content: "Email:"; }
  .column.role::before { content: "Роль:"; }
  .column.status::before { content: "Состояние:"; }
  .column.actions::before { content: "Действия:"; }

  .column.name {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .column.actions {
    border-top: 1px solid var(--color-border);
    padding-top: 15px;
    justify-content: center;
  }

  .action-buttons {
    justify-content: center;
  }

  .stats-card {
    grid-template-columns: 1fr;
  }

  .role-selector {
    grid-template-columns: 1fr;
  }

  .modal {
    margin: 20px;
  }

  .modal-footer {
    flex-direction: column;
  }

  .modal-footer .btn {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .page-header h1 {
    font-size: 1.5rem;
    flex-direction: column;
    gap: 5px;
    text-align: center;
  }

  .empty-state {
    padding: 40px 20px;
  }
}

/* Анимации */
.user-row {
  animation: fadeInUp 0.3s ease-out;
}

@keyframes fadeInUp {
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
.modal::-webkit-scrollbar {
  width: 8px;
}

.modal::-webkit-scrollbar-track {
  background: var(--color-bg-hover);
  border-radius: 4px;
}

.modal::-webkit-scrollbar-thumb {
  background: var(--color-primary);
  border-radius: 4px;
}

.modal::-webkit-scrollbar-thumb:hover {
  background: var(--color-primary-dark, #2980b9);
}

/* Подсказки для обязательных полей */
.form-group input:required {
  border-left: 3px solid var(--color-primary);
}

/* Стили для состояний загрузки */
.fa-spinner {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>