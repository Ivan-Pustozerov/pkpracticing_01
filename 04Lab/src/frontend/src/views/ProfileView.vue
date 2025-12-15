<template>
  <div class="profile-view">
    <div class="page-header">
      <h1><i class="fas fa-user-circle"></i> Мой профиль</h1>
      <p class="page-subtitle">Управление вашим аккаунтом</p>
    </div>

    <div class="profile-card">
      <div class="profile-header">
        <div class="profile-avatar">
          <i class="fas fa-user"></i>
        </div>
        <div class="profile-info">
          <h2>{{ authStore.user?.name }}</h2>
          <p class="profile-email">{{ authStore.user?.email }}</p>
          <div class="profile-role">
            <span :class="authStore.isAdmin ? 'role-admin' : 'role-user'">
              <i :class="authStore.isAdmin ? 'fas fa-crown' : 'fas fa-user'"></i>
              {{ authStore.isAdmin ? 'Администратор' : 'Пользователь' }}
            </span>
          </div>
        </div>
        <button class="btn-edit-profile" @click="openEditProfile">
          <i class="fas fa-edit"></i> Редактировать
        </button>
      </div>

      <div class="profile-stats">
        <div class="stat-card">
          <div class="stat-icon">
            <i class="fas fa-chart-line"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ functionsCount }}</div>
            <div class="stat-label">Функций создано</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">
            <i class="fas fa-calendar-alt"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ daysSinceJoin }}</div>
            <div class="stat-label">Дней с нами</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">
            <i class="fas fa-chart-pie"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ analyticFunctions }}</div>
            <div class="stat-label">Аналитических</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">
            <i class="fas fa-table"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ tabulatedFunctions }}</div>
            <div class="stat-label">Табличных</div>
          </div>
        </div>
      </div>

      <div class="profile-sections">
        <div class="profile-section">
          <h3><i class="fas fa-cog"></i> Настройки аккаунта</h3>
          <div class="settings-list">
            <div class="setting-item">
              <i class="fas fa-palette"></i>
              <span>Настройки темы</span>
              <button class="btn-setting" @click="openThemeSettings">
                <i class="fas fa-chevron-right"></i>
              </button>
            </div>
            <div class="setting-item">
              <i class="fas fa-bell"></i>
              <span>Уведомления</span>
              <button class="btn-setting" @click="openNotifications">
                <i class="fas fa-chevron-right"></i>
              </button>
            </div>
            <div class="setting-item">
              <i class="fas fa-shield-alt"></i>
              <span>Безопасность</span>
              <button class="btn-setting" @click="openSecurity">
                <i class="fas fa-chevron-right"></i>
              </button>
            </div>
          </div>
        </div>

        <div class="profile-section">
          <h3><i class="fas fa-info-circle"></i> Информация о системе</h3>
          <div class="system-info">
            <div class="info-item">
              <span class="label"><i class="fas fa-code"></i> Версия:</span>
              <span class="value">1.0.0</span>
            </div>
            <div class="info-item">
              <span class="label"><i class="fas fa-calendar"></i> Регистрация:</span>
              <span class="value">{{ formatDate(authStore.user?.createdAt) }}</span>
            </div>
            <div class="info-item">
              <span class="label"><i class="fas fa-clock"></i> Последний вход:</span>
              <span class="value">{{ formatDate(authStore.user?.lastLogin) || 'Сегодня' }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="profile-actions">
        <button class="btn-action btn-secondary" @click="openHelpCenter">
          <i class="fas fa-question-circle"></i> Помощь
        </button>
        <button class="btn-action btn-danger" @click="logout" v-if="authStore.isAuthenticated">
          <i class="fas fa-sign-out-alt"></i> Выйти
        </button>
      </div>
    </div>

    <!-- Модальное окно: Редактирование профиля недоступно -->
    <div v-if="showEditProfileModal" class="modal-overlay" @click.self="closeEditProfileModal">
      <div class="modal edit-profile-modal">
        <div class="modal-header edit-profile-header">
          <i class="fas fa-user-edit"></i>
          <h3>Редактирование профиля</h3>
          <button @click="closeEditProfileModal" class="modal-close">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="edit-profile-message">
            <div class="edit-profile-icon">
              <i class="fas fa-exclamation-circle"></i>
            </div>
            <h4>Мы сожалеем</h4>
            <p>Пока вы не можете редактировать свой профиль.</p>
            <p>Мы работаем над этой функцией, чтобы сделать управление аккаунтом более удобным.</p>

            <div class="news-subscription">
              <i class="fas fa-newspaper"></i>
              <p class="news-text">Следите за новостями в следующих обновлениях!</p>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeEditProfileModal" class="btn-ok">
            <i class="fas fa-check"></i> Понятно
          </button>
        </div>
      </div>
    </div>

    <!-- Модальное окно: Уведомления в разработке -->
    <div v-if="showNotificationsModal" class="modal-overlay" @click.self="closeNotificationsModal">
      <div class="modal notifications-modal">
        <div class="modal-header notifications-header">
          <i class="fas fa-bell"></i>
          <h3>Уведомления</h3>
          <button @click="closeNotificationsModal" class="modal-close">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="apology-message">
            <i class="fas fa-users apology-icon"></i>
            <h4>Команда PKDevs приносит извинения</h4>
            <p>Функция уведомлений находится в разработке.</p>
            <p>Мы работаем над созданием удобной системы оповещений.</p>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeNotificationsModal" class="btn-ok">
            <i class="fas fa-check"></i> Ждем с нетерпением!
          </button>
        </div>
      </div>
    </div>

    <!-- Модальное окно: Безопасность в разработке -->
    <div v-if="showSecurityModal" class="modal-overlay" @click.self="closeSecurityModal">
      <div class="modal security-modal">
        <div class="modal-header security-header">
          <i class="fas fa-shield-alt"></i>
          <h3>Безопасность</h3>
          <button @click="closeSecurityModal" class="modal-close">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="apology-message">
            <i class="fas fa-users apology-icon"></i>
            <h4>Команда PKDevs приносит извинения</h4>
            <p>Пока мы работаем над обновлением системы безопасности.</p>

            <!-- Картинка безопасности -->
            <div class="security-image-container">
              <img
                src="/images/securitymeme.jpg"
                alt="Безопасность в разработке"
                class="security-image"
              />
            </div>

            <p class="security-note">Secure. Contain. Protect.</p>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeSecurityModal" class="btn-ok">
            <i class="fas fa-check"></i> Оки-доки
          </button>
        </div>
      </div>
    </div>

    <!-- Модальное окно: Помощь -->
    <div v-if="showHelpCenterModal" class="modal-overlay" @click.self="closeHelpCenterModal">
      <div class="modal help-center-modal">
        <div class="modal-header help-center-header">
          <i class="fas fa-question-circle"></i>
          <h3>Поддержка</h3>
          <button @click="closeHelpCenterModal" class="modal-close">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="help-center-message">
            <div class="help-center-icon">
              <i class="fas fa-hands-helping"></i>
            </div>
            <h4>Нужна помощь?</h4>
            <p>Для получения поддержки свяжитесь лично с нашей командой или с любым верифицированным администратором.</p>

            <div class="support-info">
              <div class="support-method">
                <i class="fas fa-users support-icon"></i>
                <div class="support-text">
                  <h5>Наша команда</h5>
                  <p>Обратитесь напрямую к разработчикам PKDevs</p>
                </div>
              </div>

              <div class="support-method">
                <i class="fas fa-user-shield support-icon"></i>
                <div class="support-text">
                  <h5>Администраторы</h5>
                  <p>Верифицированные админы всегда готовы помочь</p>
                </div>
              </div>

              <div class="support-method">
                <i class="fas fa-heart support-icon"></i>
                <div class="support-text">
                  <h5>Спасибо!</h5>
                  <p>Мы ценим ваше понимание и терпение</p>
                </div>
              </div>
            </div>

            <div class="thank-you-message">
              <i class="fas fa-star"></i>
              <p>Спасибо за использование нашего сервиса!</p>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeHelpCenterModal" class="btn-ok">
            <i class="fas fa-check"></i> Понятно, спасибо!
          </button>
        </div>
      </div>
    </div>

    <!-- Диалог темы -->
    <div v-if="showThemeDialog" class="theme-dialog-overlay" @click.self="closeThemeDialog">
      <div class="theme-dialog">
        <h3><i class="fas fa-palette"></i> Настройки темы</h3>
        <p>Выбор темы уже доступен в верхнем меню>:]</p>
        <button class="btn-close" @click="closeThemeDialog">
          <i class="fas fa-times"></i> Закрыть
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import apiClient from '@/api/client'

const router = useRouter()
const authStore = useAuthStore()

// Модальные окна
const showEditProfileModal = ref(false)
const showNotificationsModal = ref(false)
const showSecurityModal = ref(false)
const showHelpCenterModal = ref(false)
const showThemeDialog = ref(false)

// Статистика
const functionsCount = ref(0)
const analyticFunctions = ref(0)
const tabulatedFunctions = ref(0)
const userCreatedAt = ref<string | null>(null)

// Вычисляем дни с момента регистрации
const daysSinceJoin = computed(() => {
  if (!userCreatedAt.value) return 0
  const joinDate = new Date(userCreatedAt.value)
  const today = new Date()
  const diffTime = Math.abs(today.getTime() - joinDate.getTime())
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24))
})

// Загружаем статистику пользователя
const loadUserStats = async () => {
  try {
    const response = await apiClient.get('/api/users/me/stats')
    const data = response.data
    functionsCount.value = data.totalFunctions || 0
    analyticFunctions.value = data.analyticFunctions || 0
    tabulatedFunctions.value = data.tabulatedFunctions || 0
    userCreatedAt.value = data.createdAt
  } catch (error) {
    console.error('Ошибка загрузки статистики:', error)
  }
}

// Форматирование даты
const formatDate = (dateString?: string) => {
  if (!dateString) return 'Не указано'
  try {
    const date = new Date(dateString)
    return date.toLocaleDateString('ru-RU', {
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    })
  } catch {
    return 'Неизвестно'
  }
}

// Действия
const openEditProfile = () => {
  showEditProfileModal.value = true
}

const closeEditProfileModal = () => {
  showEditProfileModal.value = false
}

const openThemeSettings = () => {
  showThemeDialog.value = true
}

const closeThemeDialog = () => {
  showThemeDialog.value = false
}

const openNotifications = () => {
  showNotificationsModal.value = true
}

const closeNotificationsModal = () => {
  showNotificationsModal.value = false
}

const openSecurity = () => {
  showSecurityModal.value = true
}

const closeSecurityModal = () => {
  showSecurityModal.value = false
}

const openHelpCenter = () => {
  showHelpCenterModal.value = true
}

const closeHelpCenterModal = () => {
  showHelpCenterModal.value = false
}

const logout = () => {
  if (confirm('Вы уверены, что хотите выйти?')) {
    authStore.logout()
    router.push('/login')
  }
}

onMounted(() => {
  loadUserStats()
})
</script>

<style scoped>
.profile-view {
  padding: 20px;
  max-width: 1200px;
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

.profile-card {
  background: var(--color-bg-card);
  border-radius: 15px;
  padding: 30px;
  margin-top: 30px;
  box-shadow: var(--box-shadow);
}

.profile-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 30px;
  margin-bottom: 40px;
  padding-bottom: 30px;
  border-bottom: 2px solid var(--color-border);
}

.profile-avatar {
  width: 100px;
  height: 100px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-light);
  font-size: 2.5rem;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.profile-info {
  flex: 1;
}

.profile-info h2 {
  margin: 0 0 8px 0;
  color: var(--color-text-primary);
  font-size: 2rem;
}

.profile-email {
  color: var(--color-text-secondary);
  margin: 0 0 15px 0;
  font-size: 1.1rem;
}

.profile-role span {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 600;
  transition: all var(--transition-speed);
}

.role-admin {
  background: rgba(231, 76, 60, 0.1);
  color: #e74c3c;
  border: 2px solid rgba(231, 76, 60, 0.3);
}

.role-user {
  background: rgba(52, 152, 219, 0.1);
  color: var(--color-primary);
  border: 2px solid rgba(52, 152, 219, 0.3);
}

.role-admin i, .role-user i {
  font-size: 0.8rem;
}

.btn-edit-profile {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: var(--color-primary);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: all var(--transition-speed);
}

.btn-edit-profile:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.profile-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.stat-card {
  background: var(--color-bg-hover);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  transition: all var(--transition-speed);
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--box-shadow);
}

.stat-icon {
  width: 50px;
  height: 50px;
  background: var(--gradient-primary);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.3rem;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: bold;
  color: var(--color-text-primary);
  margin-bottom: 5px;
}

.stat-label {
  font-size: 0.9rem;
  color: var(--color-text-secondary);
}

.profile-sections {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
  margin-bottom: 40px;
}

.profile-section {
  background: var(--color-bg-hover);
  border-radius: 12px;
  padding: 25px;
}

.profile-section h3 {
  margin: 0 0 20px 0;
  color: var(--color-text-primary);
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 1.2rem;
}

.settings-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.setting-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 15px;
  background: var(--color-bg-card);
  border-radius: 8px;
  transition: all var(--transition-speed);
}

.setting-item:hover {
  background: var(--color-bg-active);
  transform: translateX(5px);
}

.setting-item i:first-child {
  color: var(--color-primary);
  margin-right: 10px;
  width: 20px;
  text-align: center;
}

.setting-item span {
  flex: 1;
  color: var(--color-text-primary);
}

.btn-setting {
  background: none;
  border: none;
  color: var(--color-text-secondary);
  cursor: pointer;
  padding: 5px;
  transition: color var(--transition-speed);
}

.btn-setting:hover {
  color: var(--color-primary);
}

.system-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid var(--color-border);
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  color: var(--color-text-secondary);
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.95rem;
}

.info-item .label i {
  width: 20px;
  text-align: center;
}

.info-item .value {
  color: var(--color-text-primary);
  font-weight: 500;
  font-family: 'Courier New', monospace;
  font-size: 0.9rem;
}

.profile-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
  padding-top: 30px;
  border-top: 2px solid var(--color-border);
}

.btn-action {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: all var(--transition-speed);
}

.btn-action:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.btn-secondary {
  background: var(--color-text-muted);
  color: white;
}

.btn-danger {
  background: #e74c3c;
  color: white;
}

/* Общие стили модальных окон */
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
  text-align: center;
}

.modal-footer {
  display: flex;
  justify-content: center;
  padding: 20px 25px;
  border-top: 1px solid var(--color-border);
}

.btn-ok {
  padding: 10px 25px;
  background: var(--color-primary);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-ok:hover {
  opacity: 0.9;
  transform: translateY(-2px);
}

/* Модальное окно: Редактирование профиля */
.edit-profile-modal .modal-header {
  background: var(--color-primary);
}

.edit-profile-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.edit-profile-icon {
  width: 80px;
  height: 80px;
  background: var(--color-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 2.5rem;
  margin-bottom: 10px;
}

.edit-profile-message h4 {
  margin: 0;
  color: var(--color-text-primary);
  font-size: 1.4rem;
  text-align: center;
}

.edit-profile-message p {
  margin: 0;
  color: var(--color-text-secondary);
  line-height: 1.5;
  text-align: center;
}

.news-subscription {
  margin-top: 20px;
  padding: 15px;
  background: rgba(52, 152, 219, 0.1);
  border-radius: 10px;
  border-left: 4px solid  var(--color-primary);
  display: flex;
  align-items: center;
  gap: 15px;
  width: 100%;
  max-width: 400px;
}

.news-subscription i {
  color: var(--color-primary);
  font-size: 1.5rem;
}

.news-text {
  font-weight: bold;
  color: var(--color-primary) !important;
  margin: 0 !important;
  text-align: left !important;
}

/* Модальное окно: Уведомления */
.notifications-modal .modal-header {
  background: var(--color-primary);
}

.apology-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.apology-icon {
  font-size: 3rem;
  color: #FF9800;
  margin-bottom: 10px;
}

.apology-message h4 {
  margin: 0;
  color: var(--color-text-primary);
  font-size: 1.3rem;
  text-align: center;
}

.apology-message p {
  margin: 0;
  color: var(--color-text-secondary);
  line-height: 1.5;
}

/* Модальное окно: Безопасность */
.security-modal .modal-header {
  background: var(--color-primary);
}

.security-image-container {
  margin: 20px 0;
  padding: 15px;
  background: var(--color-bg-hover);
  border-radius: 10px;
  border: 2px dashed var(--color-primary);
}

.security-image {
  width: 100%;
  max-width: 500px;
  height: auto;
  border-radius: 8px;
  display: block;
  margin: 0 auto;
}

.security-note {
  margin-top: 15px !important;
  font-weight: bold;
  color: var(--color-primary) !important;
  background: var(--color-bg-main);
  padding: 10px 20px;
  border-radius: 8px;
}

/* Модальное окно: Помощь */
.help-center-modal .modal-header {
  background: linear-gradient(135deg, #9b59b6 0%, #8e44ad 100%);
}

.help-center-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.help-center-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #9b59b6 0%, #8e44ad 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 2.5rem;
  margin-bottom: 10px;
}

.help-center-message h4 {
  margin: 0;
  color: var(--color-text-primary);
  font-size: 1.5rem;
  text-align: center;
}

.help-center-message p {
  margin: 0;
  color: var(--color-text-secondary);
  line-height: 1.5;
  text-align: center;
}

.support-info {
  display: flex;
  flex-direction: column;
  gap: 15px;
  width: 100%;
  max-width: 400px;
  margin: 20px 0;
}

.support-method {
  display: flex;
  align-items: flex-start;
  gap: 15px;
  padding: 15px;
  background: rgba(155, 89, 182, 0.1);
  border-radius: 10px;
  border-left: 4px solid #9b59b6;
  text-align: left;
}

.support-icon {
  color: #9b59b6;
  font-size: 1.5rem;
  margin-top: 2px;
}

.support-text {
  flex: 1;
}

.support-text h5 {
  margin: 0 0 5px 0;
  color: var(--color-text-primary);
  font-size: 1.1rem;
}

.support-text p {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 0.9rem;
  text-align: left !important;
}

.thank-you-message {
  margin-top: 20px;
  padding: 15px;
  background: rgba(155, 89, 182, 0.1);
  border-radius: 10px;
  border: 2px dashed #9b59b6;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.thank-you-message i {
  color: #f1c40f;
  font-size: 1.2rem;
}

.thank-you-message p {
  margin: 0;
  font-weight: bold;
  color: #9b59b6 !important;
  font-size: 1rem;
}

/* Диалог темы */
.theme-dialog-overlay {
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
  backdrop-filter: blur(2px);
}

.theme-dialog {
  background: var(--color-bg-card);
  border-radius: 15px;
  padding: 30px;
  max-width: 400px;
  width: 90%;
  box-shadow: var(--box-shadow-lg);
}

.theme-dialog h3 {
  margin: 0 0 15px 0;
  color: var(--color-text-primary);
  display: flex;
  align-items: center;
  gap: 10px;
}

.theme-dialog p {
  color: var(--color-text-secondary);
  margin: 0 0 20px 0;
}

.btn-close {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: var(--color-primary);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  margin-top: 20px;
  transition: all var(--transition-speed);
}

.btn-close:hover {
  opacity: 0.9;
}

/* Адаптивность */
@media (max-width: 768px) {
  .profile-header {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }

  .profile-avatar {
    width: 80px;
    height: 80px;
    font-size: 2rem;
  }

  .profile-info h2 {
    font-size: 1.5rem;
  }

  .profile-stats {
    grid-template-columns: repeat(2, 1fr);
  }

  .profile-sections {
    grid-template-columns: 1fr;
  }

  .profile-actions {
    flex-direction: column;
  }

  .btn-action {
    width: 100%;
    justify-content: center;
  }

  .news-subscription {
    flex-direction: column;
    text-align: center;
    gap: 10px;
  }

  .news-text {
    text-align: center !important;
  }

  .support-method {
    flex-direction: column;
    text-align: center;
    gap: 10px;
  }

  .support-text p {
    text-align: center !important;
  }
}

@media (max-width: 480px) {
  .profile-stats {
    grid-template-columns: 1fr;
  }

  .profile-card {
    padding: 20px;
  }

  .profile-section {
    padding: 20px;
  }

  .security-image {
    max-width: 100%;
  }
}

/* Анимации */
.profile-card {
  animation: fadeIn 0.6s ease-out;
}

.stat-card {
  animation: slideUp 0.5s ease-out;
  animation-fill-mode: both;
}

.stat-card:nth-child(1) { animation-delay: 0.1s; }
.stat-card:nth-child(2) { animation-delay: 0.2s; }
.stat-card:nth-child(3) { animation-delay: 0.3s; }
.stat-card:nth-child(4) { animation-delay: 0.4s; }

.modal {
  animation: modalSlideUp 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes modalSlideUp {
  from {
    opacity: 0;
    transform: translateY(50px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* Интерактивные элементы */
.setting-item, .btn-setting, .btn-action, .btn-edit-profile, .modal-close {
  cursor: pointer;
}

.setting-item:active, .btn-action:active, .btn-edit-profile:active {
  transform: scale(0.98);
}

/* Кастомный скролл для мобильных */
@media (max-width: 768px) {
  .profile-view {
    overflow-x: hidden;
  }

  .profile-stats, .profile-sections {
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
  }
}
</style>