<template>
  <div id="app">
    <!-- Навигационная панель (только для авторизованных) -->
    <header v-if="showNavigation" class="app-header">
      <nav class="navbar">
        <!-- Логотип и навигация слева -->
        <div class="nav-section nav-left">
          <router-link to="/functions" class="nav-logo">
            <i class="fas fa-calculator"></i>
            <span class="logo-text">MathFunctions</span>
          </router-link>

          <div class="nav-links">
            <router-link to="/functions" class="nav-link" active-class="active">
              <i class="fas fa-list"></i>
              <span>Мои функции</span>
            </router-link>

            <router-link to="/functions/create" class="nav-link" active-class="active">
              <i class="fas fa-plus-circle"></i>
              <span>Создать</span>
            </router-link>

            <router-link v-if="authStore.isAdmin" to="/users" class="nav-link" active-class="active">
              <i class="fas fa-users-cog"></i>
              <span>Пользователи</span>
            </router-link>
          </div>
        </div>

        <!-- Профиль и выход справа -->
        <div class="nav-section nav-right">
          <div class="user-profile">
            <div class="user-avatar">
              <i class="fas fa-user"></i>
            </div>
            <div class="user-info">
              <div class="user-name">{{ authStore.user?.name }}</div>
              <div class="user-role">
                <span v-if="authStore.isAdmin" class="role-badge admin">
                  <i class="fas fa-crown"></i> Администратор
                </span>
                <span v-else class="role-badge user">
                  <i class="fas fa-user"></i> Пользователь
                </span>
              </div>
            </div>
            <div class="user-actions">
              <router-link to="/profile" class="profile-link" title="Профиль">
                <i class="fas fa-user-circle"></i>
              </router-link>
              <button @click="logout" class="logout-btn" title="Выйти">
                <i class="fas fa-sign-out-alt"></i>
              </button>
            </div>
          </div>
        </div>
      </nav>
    </header>

    <!-- Основное содержимое -->
    <main class="main-content" :class="{ 'with-nav': showNavigation }">
      <div class="container">
        <!-- Хлебные крошки (опционально) -->
        <nav v-if="showBreadcrumb" class="breadcrumb">
          <router-link to="/functions" class="breadcrumb-item">
            <i class="fas fa-home"></i>
          </router-link>
          <span class="breadcrumb-separator">/</span>
          <span class="breadcrumb-current">{{ currentRouteName }}</span>
        </nav>

        <!-- Заголовок страницы -->
        <div v-if="showPageTitle" class="page-header">
          <h1 class="page-title">{{ pageTitle }}</h1>
          <div class="page-subtitle" v-if="pageSubtitle">{{ pageSubtitle }}</div>
        </div>

        <!-- Контент страницы (с анимацией) -->
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>

    <!-- Футер -->
    <footer v-if="showNavigation" class="app-footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-left">
            <div class="footer-logo">
              <i class="fas fa-calculator"></i>
              <span>MathFunctions</span>
            </div>
            <p class="footer-description">
              Система для работы с математическими функциями
            </p>
          </div>
          <div class="footer-right">
            <div class="footer-info">
              <div class="footer-version">Версия 1.0.0</div>
              <div class="footer-copyright">
                &copy; 2025 PKDevs. Все права защищены.
              </div>
            </div>
          </div>
        </div>
      </div>
    </footer>

    <!-- Глобальные уведомления (можно добавить позже) -->
    <!-- <notification-center /> -->
  </div>
</template>

<script setup lang="ts">
import { computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// Показать навигацию только для авторизованных пользователей и не на страницах auth
const showNavigation = computed(() => {
  return authStore.isAuthenticated && route.meta.layout !== 'auth'
})

// Показать хлебные крошки
const showBreadcrumb = computed(() => {
  return showNavigation.value && route.meta.breadcrumb
})

// Текущее название маршрута для хлебных крошек
const currentRouteName = computed(() => {
  return route.meta.breadcrumb || route.name?.toString() || ''
})

// Заголовок страницы
const pageTitle = computed(() => {
  return route.meta.title || 'MathFunctions'
})

// Подзаголовок страницы (опционально)
const pageSubtitle = computed(() => {
  return route.meta.subtitle || ''
})

// Показывать заголовок страницы
const showPageTitle = computed(() => {
  return route.meta.title && route.meta.layout !== 'auth'
})

// Выход из системы
const logout = () => {
  if (confirm('Вы уверены, что хотите выйти?')) {
    authStore.logout()
    router.push('/login')
  }
}

// Следим за изменениями маршрута для обновления заголовка
watch(
  () => route.meta.title,
  (newTitle) => {
    if (newTitle) {
      document.title = `${newTitle} | MathFunctions`
    }
  },
  { immediate: true }
)
</script>

<style>
/* Глобальные стили */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  line-height: 1.6;
  color: #333;
  background-color: #f8f9fa;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* Шапка */
.app-header {
  background: linear-gradient(135deg, #2c3e50 0%, #3498db 100%);
  color: white;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 70px;
}

.nav-section {
  display: flex;
  align-items: center;
}

/* Логотип */
.nav-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  color: white;
  text-decoration: none;
  font-size: 1.5rem;
  font-weight: 700;
  margin-right: 40px;
  transition: transform 0.3s;
}

.nav-logo:hover {
  transform: translateY(-2px);
}

.nav-logo i {
  font-size: 1.8rem;
  color: #3498db;
}

.logo-text {
  background: linear-gradient(135deg, #3498db 0%, #2ecc71 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* Навигационные ссылки */
.nav-links {
  display: flex;
  gap: 5px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  padding: 10px 16px;
  border-radius: 8px;
  transition: all 0.3s;
  font-weight: 500;
}

.nav-link:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.nav-link.active {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.nav-link i {
  font-size: 1.1rem;
}

/* Профиль пользователя */
.user-profile {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  transition: background 0.3s;
}

.user-profile:hover {
  background: rgba(255, 255, 255, 0.15);
}

.user-avatar {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #3498db 0%, #2ecc71 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.2rem;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-weight: 600;
  font-size: 0.95rem;
}

.user-role {
  font-size: 0.8rem;
  margin-top: 2px;
}

.role-badge {
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 0.75rem;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.role-badge.admin {
  background: rgba(231, 76, 60, 0.2);
  color: #e74c3c;
}

.role-badge.user {
  background: rgba(52, 152, 219, 0.2);
  color: #3498db;
}

.user-actions {
  display: flex;
  gap: 10px;
  margin-left: 10px;
}

.profile-link, .logout-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
  font-size: 1rem;
}

.profile-link {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  text-decoration: none;
}

.profile-link:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: scale(1.1);
}

.logout-btn {
  background: rgba(231, 76, 60, 0.2);
  color: #e74c3c;
}

.logout-btn:hover {
  background: rgba(231, 76, 60, 0.3);
  transform: scale(1.1);
}

/* Основной контент */
.main-content {
  flex: 1;
  padding: 30px 0;
}

.main-content.with-nav {
  padding-top: 30px;
}

/* Хлебные крошки */
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 25px;
  padding: 12px 20px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.breadcrumb-item {
  color: #3498db;
  text-decoration: none;
  display: flex;
  align-items: center;
}

.breadcrumb-item:hover {
  text-decoration: underline;
}

.breadcrumb-separator {
  color: #95a5a6;
}

.breadcrumb-current {
  color: #2c3e50;
  font-weight: 600;
}

/* Заголовок страницы */
.page-header {
  margin-bottom: 30px;
}

.page-title {
  font-size: 2.2rem;
  color: #2c3e50;
  margin-bottom: 10px;
  font-weight: 700;
}

.page-subtitle {
  color: #7f8c8d;
  font-size: 1.1rem;
}

/* Футер */
.app-footer {
  background: #2c3e50;
  color: #ecf0f1;
  padding: 30px 0;
  margin-top: 50px;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 1.5rem;
  font-weight: 700;
  color: #3498db;
}

.footer-description {
  color: #bdc3c7;
  font-size: 0.9rem;
  max-width: 400px;
}

.footer-right {
  text-align: right;
}

.footer-version {
  color: #95a5a6;
  font-size: 0.9rem;
  margin-bottom: 5px;
}

.footer-copyright {
  color: #7f8c8d;
  font-size: 0.85rem;
}

/* Анимации */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* Адаптивность */
@media (max-width: 768px) {
  .navbar {
    padding: 0 15px;
    height: 60px;
  }

  .nav-links {
    display: none; /* Скрываем ссылки на мобильных */
  }

  .user-profile {
    padding: 6px 12px;
  }

  .user-info {
    display: none; /* Скрываем информацию о пользователе на мобильных */
  }

  .footer-content {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }

  .footer-left, .footer-right {
    text-align: center;
  }
}
</style>