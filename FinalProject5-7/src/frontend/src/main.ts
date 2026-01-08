/**
 * Точка входа Vue приложения
 * Инициализирует Vue, Pinia и Router
 */
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

// Глобальные стили
import './assets/main.css'
import './assets/styles/themes.css' // Добавляем импорт тем

// Создаем экземпляр Vue приложения
const app = createApp(App)

// Подключаем Pinia для управления состоянием
const pinia = createPinia()
app.use(pinia)

// Подключаем роутер
app.use(router)

// Монтируем приложение в DOM элемент #app
app.mount('#app')

// Логирование для отладки
console.log('MathFunctions frontend запущен!')
console.log('Режим:', import.meta.env.MODE)
console.log('API URL:', import.meta.env.VITE_API_URL || '/api')

// Инициализируем тему после монтирования приложения
setTimeout(async () => {
  try {
    const { useThemeStore } = await import('@/stores/theme.store')
    const themeStore = useThemeStore()
    // Тема уже загрузится из localStorage в конструкторе store
    console.log('Тема инициализирована:', themeStore.currentTheme)
  } catch (error) {
    console.warn('Ошибка при инициализации темы:', error)
  }
}, 100)