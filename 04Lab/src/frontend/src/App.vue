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

        <!-- Профиль, тема и выход справа -->
        <div class="nav-section nav-right">
          <!-- Переключатель тем -->
          <div class="theme-switcher" v-if="showNavigation">
            <button
              @click="toggleThemeDropdown"
              class="theme-toggle-btn"
              :title="`Тема: ${themeStore.currentThemeName}`"
            >
              <span v-html="themeStore.currentThemeIconHtml"></span>
            </button>

            <!-- Выпадающее меню с выбором темы -->
            <div v-if="showThemeDropdown" class="theme-dropdown" ref="themeDropdown">
              <div class="theme-dropdown-header">
                <strong>Выбор темы</strong>
              </div>
              <div
                v-for="theme in themes"
                :key="theme.value"
                class="theme-option"
                :class="{ active: themeStore.currentTheme === theme.value }"
                @click="selectTheme(theme.value)"
              >
                <span class="theme-icon" v-html="theme.iconHtml"></span>
                <div class="theme-info">
                  <div class="theme-name">{{ theme.name }}</div>
                  <div class="theme-desc">{{ theme.description }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- Профиль пользователя -->
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

    <!-- Оверлей для закрытия выпадающего меню темы -->
    <div
      v-if="showThemeDropdown"
      class="dropdown-overlay"
      @click="closeThemeDropdown"
    ></div>
  </div>
</template>

<script setup lang="ts">
import { computed, watch, ref, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useThemeStore } from '@/stores/theme.store'
import { ThemeName } from '@/stores/theme.store'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const themeStore = useThemeStore()

// Состояние для выпадающего меню темы
const showThemeDropdown = ref(false)
const themeDropdown = ref<HTMLElement | null>(null)

// Список тем для меню - ДОБАВЛЕНО поле iconHtml
const themes = [
  {
    value: 'light',
    name: 'Светлая',
    iconHtml: '<i class="fas fa-sun"></i>',
    description: 'BASED theme'
  },
  {
    value: 'dark',
    name: 'Темная',
    iconHtml: '<i class="fas fa-moon"></i>',
    description: 'Luna the Best'
  },
  {
    value: 'ultra-dark',
    name: 'Ультра-темная',
    iconHtml: '<i class="fa-brands fa-squarespace"></i>', // ИСПРАВЛЕНО
    description: 'Темная и мрачная пустота'
  },
  {
    value: 'eco',
    name: 'Эко',
    iconHtml: '<i class="fas fa-leaf"></i>',
    description: 'WEED'
  }
]

// Показать навигацию только для авторизованных пользователей
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

// Подзаголовок страницы
const pageSubtitle = computed(() => {
  return route.meta.subtitle || ''
})

// Показывать заголовок страницы
const showPageTitle = computed(() => {
  return route.meta.title && route.meta.layout !== 'auth'
})



// Название текущей темы для кнопки
const currentThemeName = computed(() => {
  const theme = themes.find(t => t.value === themeStore.currentTheme)
  return theme?.name || 'Светлая'
})

// Иконка текущей темы для кнопки
const currentThemeIcon = computed(() => {
  const theme = themes.find(t => t.value === themeStore.currentTheme)
  return theme?.icon || '☀️'
})

// Текущая тема для сравнения в меню
const currentTheme = computed(() => themeStore.currentTheme || 'light')

// ============================================

// Переключение выпадающего меню
const toggleThemeDropdown = () => {
  console.log('🔄 toggleThemeDropdown вызван')
  showThemeDropdown.value = !showThemeDropdown.value
}

// Закрытие выпадающего меню
const closeThemeDropdown = () => {
  showThemeDropdown.value = false
}

// ВЫБОР ТЕМЫ
const selectTheme = (theme: string) => {
  console.log('🎯 Выбираем тему:', theme)

  // Удаляем все классы тем
  const allThemes = ['light', 'dark', 'ultra-dark', 'eco']
  const html = document.documentElement

  allThemes.forEach(t => html.classList.remove(`theme-${t}`))
  html.classList.add(`theme-${theme}`)
  html.setAttribute('data-theme', theme)

  // Сохраняем в localStorage
  localStorage.setItem('app-theme', theme)

  // Обновляем store
  themeStore.currentTheme = theme as ThemeName

  closeThemeDropdown()
  console.log('✅ Тема применена:', theme)
}

// Выход из системы
const logout = () => {
  if (confirm('Вы уверены, что хотите выйти?')) {
    authStore.logout()
    router.push('/login')
  }
}

// Следим за изменениями маршрута
watch(
  () => route.meta.title,
  (newTitle) => {
    if (newTitle) {
      document.title = `${newTitle} | MathFunctions`
    }
  },
  { immediate: true }
)

// При монтировании проверяем тему
onMounted(() => {
  // Проверяем, что тема применена
  const savedTheme = localStorage.getItem('app-theme') || 'light'
  const html = document.documentElement

  // Принудительно применяем класс темы
  const allThemes = ['light', 'dark', 'ultra-dark', 'eco']
  allThemes.forEach(t => html.classList.remove(`theme-${t}`))
  html.classList.add(`theme-${savedTheme}`)
  html.setAttribute('data-theme', savedTheme)

  console.log('🚀 App mounted, тема:', savedTheme)
  console.log('themeStore.currentTheme:', themeStore.currentTheme)
})

// ИЛИ еще проще - использовать напрямую из store
// В шаблоне можно так:
// :title="`Тема: ${themeStore.currentThemeName || 'Светлая'}`"
// {{ themeStore.currentThemeIcon || '☀️' }}
</script>

<style>

/* Глобальные стили */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  transition: background-color var(--transition-speed, 0.3s) ease,
              border-color var(--transition-speed, 0.3s) ease,
              color var(--transition-speed, 0.3s) ease,
              box-shadow var(--transition-speed, 0.3s) ease,
              transform var(--transition-speed, 0.3s) ease;
}

/* Установка переменных для корневого элемента */
html {
  /* Базовые переменные по умолчанию (светлая тема) */
  --color-bg-main: #f8f9fa;
  --color-text-primary: #2c3e50;
  --color-text-secondary: #7f8c8d;
  --color-text-light: #ffffff;
  --color-text-muted: #95a5a6;
  --color-primary: #3498db;
  --color-bg-card: #ffffff;
  --color-bg-header: linear-gradient(135deg, #2c3e50 0%, #3498db 100%);
  --color-bg-footer: #2c3e50;
  --color-border: #e1e8ed;
  --color-bg-hover: rgba(52, 152, 219, 0.1);
  --color-bg-active: rgba(52, 152, 219, 0.2);
  --gradient-primary: linear-gradient(135deg, #3498db 0%, #2ecc71 100%);
  --box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  --box-shadow-lg: 0 8px 25px rgba(0, 0, 0, 0.2);
  --border-radius: 8px;
  --transition-speed: 0.3s;
}

/* Применяем переменные к body */
body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  line-height: 1.6;
  color: var(--color-text-primary);
  background-color: var(--color-bg-main);
  margin: 0;
  padding: 0;
  min-height: 100vh;
  transition: background-color var(--transition-speed) ease,
              color var(--transition-speed) ease;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--color-bg-main);
  color: var(--color-text-primary);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* Шапка */
.app-header {
  background: var(--color-bg-header);
  color: var(--color-text-light);
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
  color: var(--color-text-light);
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
  color: var(--color-primary);
}

.logo-text {
  background: var(--gradient-primary);
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
  color: var(--color-text-light);
  text-decoration: none;
  padding: 10px 16px;
  border-radius: 8px;
  transition: all 0.3s;
  font-weight: 500;
  opacity: 0.9;
}

.nav-link:hover {
  background: var(--color-bg-hover);
  color: var(--color-text-light);
}

.nav-link.active {
  background: var(--color-bg-active);
  color: var(--color-text-light);
}

.nav-link i {
  font-size: 1.1rem;
}

/* Переключатель тем */
.theme-switcher {
  position: relative;
  margin-right: 15px;
}

.theme-toggle-btn {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: var(--color-bg-hover);
  color: var(--color-text-light);
  border: 2px solid var(--color-border, rgba(255, 255, 255, 0.2));
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 1.3rem;
  transition: all 0.3s;
}

.theme-toggle-btn:hover {
  background: var(--color-bg-active);
  transform: scale(1.05);
  border-color: var(--color-primary);
}

/* Выпадающее меню тем */
.theme-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 10px;
  width: 280px;
  background: var(--color-bg-card);
  border-radius: var(--border-radius, 8px);
  box-shadow: var(--box-shadow-lg);
  border: 1px solid var(--color-border);
  z-index: 1100;
  overflow: hidden;
}

.theme-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 10px;
  width: 280px;
  background: var(--color-bg-card);
  border-radius: var(--border-radius, 8px);
  box-shadow: var(--box-shadow-lg);
  border: 1px solid var(--color-border);
  z-index: 1100;
  overflow: hidden;
}

.theme-dropdown-header {
  padding: 15px;
  background: var(--color-bg-hover);
  border-bottom: 1px solid var(--color-border);
  font-size: 0.9rem;
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: color 0.3s, background-color 0.3s;
}

.theme-dropdown-header:hover {
  color: white;
  background: var(--color-primary);
}

.theme-option {
  padding: 15px;
  display: flex;
  align-items: center;
  gap: 15px;
  cursor: pointer;
  transition: background-color 0.3s;
  border-bottom: 1px solid var(--color-border-light, #f1f1f1);
}

.theme-option:last-child {
  border-bottom: none;
}

.theme-option:hover {
  background: var(--color-primary);
}
.theme-option:hover .theme-icon,
.theme-option:hover .theme-name,
.theme-option:hover .theme-desc {
  color: var(--color-border-light);
}

.theme-option.active {
  background: var(--color-bg-active);
  position: relative; /* Добавляем для псевдоэлемента */
}

/* Граница на 80% высоты с помощью псевдоэлемента */
.theme-option.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 5%; /* Отступ сверху 10% */
  height: 90%; /* Высота 80% от родителя */
  width: 3px;
  background: var(--color-primary);
  border-radius: 0 3px 3px 0;
}

.theme-icon {
  font-size: 1.5rem;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-primary);
  transition: color 0.3s; /* Плавный переход для иконки */
}

.theme-info {
  flex: 1;
}

.theme-name {
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 3px;
  transition: color 0.3s; /* Плавный переход для названия */
}

.theme-desc {
  font-size: 0.85rem;
  color: var(--color-text-muted);
  transition: color 0.3s;
}


.user-profile {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 8px 16px;
  background: var(--color-bg-hover);
  border-radius: 12px;
  transition: background 0.3s;
}

.user-profile:hover {
  background: var(--color-bg-active);
}

.user-avatar {
  width: 40px;
  height: 40px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-light);
  font-size: 1.2rem;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-weight: 600;
  font-size: 0.95rem;
  color: var(--color-text-light);
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
  background: var(--color-bg-hover);
  color: var(--color-error, #e74c3c);
}

.role-badge.user {
  background: var(--color-bg-hover);
  color: var(--color-text-light, #5dade2);
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
  background: var(--color-bg-hover);
  color: var(--color-text-light);
  text-decoration: none;
}

.profile-link:hover {
  background: var(--color-bg-active);
  transform: scale(1.1);
}

.logout-btn {
  background: var(--color-bg-hover);
  color: var(--color-error, --color-text-light);
}

.logout-btn:hover {
  background: var(--color-bg-active);
  transform: scale(1.1);
}

/* Основной контент */
.main-content {
  flex: 1;
  padding: 30px 0;
  background-color: var(--color-bg-main);
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
  background: var(--color-bg-card);
  border-radius: 10px;
  box-shadow: var(--box-shadow);
}

.breadcrumb-item {
  color: var(--color-primary);
  text-decoration: none;
  display: flex;
  align-items: center;
}

.breadcrumb-item:hover {
  text-decoration: underline;
}

.breadcrumb-separator {
  color: var(--color-text-muted);
}

.breadcrumb-current {
  color: var(--color-text-primary);
  font-weight: 600;
}

/* Заголовок страницы */
.page-header {
  margin-bottom: 30px;
}

.page-title {
  font-size: 2.2rem;
  color: var(--color-text-primary);
  margin-bottom: 10px;
  font-weight: 700;
}

.page-subtitle {
  color: var(--color-text-secondary);
  font-size: 1.1rem;
}

/* Футер */
.app-footer {
  background: var(--color-bg-footer);
  color: var(--color-text-light);
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
  color: var(--color-primary);
}

.footer-description {
  color: var(--color-text-muted);
  font-size: 0.9rem;
  max-width: 400px;
}

.footer-right {
  text-align: right;
}

.footer-version {
  color: var(--color-text-muted);
  font-size: 0.9rem;
  margin-bottom: 5px;
}

.footer-copyright {
  color: var(--color-text-secondary);
  font-size: 0.85rem;
}

/* Оверлей для закрытия выпадающего меню */
.dropdown-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 500;
  background: transparent;
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

  .theme-switcher {
    margin-right: 10px;
  }

  .theme-toggle-btn {
    width: 40px;
    height: 40px;
    font-size: 1.2rem;
  }

  .theme-dropdown {
    width: 250px;
    right: -10px;
  }

  .user-profile {
    padding: 6px 12px;
  }

  .user-info {
    display: none; /* Скрываем информацию о пользователе на мобильных */
  }

  .user-actions {
    gap: 8px;
  }

  .profile-link, .logout-btn {
    width: 32px;
    height: 32px;
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

/* === ВАЖНОЕ ДОПОЛНЕНИЕ: Применение классов тем === */
/* Когда html получает класс темы, переопределяем переменные */

html.theme-light {
  --color-bg-main: #f8f9fa;
  --color-text-primary: #2c3e50;
  --color-text-secondary: #7f8c8d;
  --color-text-light: #ffffff;
  --color-text-muted: #95a5a6;
  --color-primary: #3498db;
  --color-bg-card: #ffffff;
  --color-bg-header: linear-gradient(135deg, #2c3e50 0%, #3498db 100%);
  --color-bg-footer: #2c3e50;
  --color-border: #e1e8ed;
  --color-bg-hover: rgba(52, 152, 219, 0.1);
  --color-bg-active: rgba(52, 152, 219, 0.2);
  --gradient-primary: linear-gradient(135deg, #3498db 0%, #7fbbe3 100%);
}

html.theme-dark {
  --color-bg-main: #1a1a2e;
  --color-bg-card: #16213e;
  --color-bg-header: linear-gradient(135deg, #0f3460 0%, #1a1a2e 100%);
  --color-bg-footer: #0f3460;
  --color-text-primary: #e6e6e6;
  --color-text-secondary: #b0b0b0;
  --color-text-light: #ffffff;
  --color-text-muted: #888888;
  --color-primary: #3498db;
  --color-border: #2a2a3e;
  --color-bg-hover: rgba(52, 152, 219, 0.15);
  --color-bg-active: rgba(52, 152, 219, 0.25);
  --gradient-primary: linear-gradient(120deg, #3498db 0%, #2ecc71 100%);
}

html.theme-ultra-dark {
  --color-bg-main: #0d0d0d;
  --color-bg-card: #1a1a1a;
  --color-bg-header: linear-gradient(135deg, #2e0f7d 0%, #0d0d0d 100%);
  --color-bg-footer: #2e0f7d;
  --color-text-primary: #f5f5f5;
  --color-text-secondary: #cccccc;
  --color-text-light: #ffffff;
  --color-text-muted: #888888;
  --color-primary: #9b59b6;
  --color-border: #333333;
  --color-bg-hover: rgba(155, 89, 182, 0.15);
  --color-bg-active: rgba(155, 89, 182, 0.25);
  --gradient-primary: linear-gradient(0deg, #9b59b6 0%, #2e0f7d 100%);
}

html.theme-eco {
  --color-bg-main: #f0f8ff;
  --color-bg-card: #ffffff;
  --color-bg-header: linear-gradient(135deg, #27ae60 0%, #16a085 100%);
  --color-bg-footer: #16a085;
  --color-text-primary: #1a535c;
  --color-text-secondary: #4a4a4a;
  --color-text-light: #ffffff;
  --color-text-muted: #1a535c;
  --color-primary: #62d17e;
  --color-border: #d1ecf1;
  --color-bg-hover: rgba(39, 174, 96, 0.1);
  --color-bg-active: rgba(39, 174, 96, 0.2);
  --gradient-primary: linear-gradient(180deg, #4ea655  5%, #84d18a 50%);
}

/* Гарантируем, что body наследует фон от html */
html[class*="theme-"] {
  background-color: var(--color-bg-main);
}

html[class*="theme-"] body {
  background-color: var(--color-bg-main);
  color: var(--color-text-primary);
}

html[class*="theme-"] #app {
  background-color: var(--color-bg-main);
  color: var(--color-text-primary);
}
</style>

