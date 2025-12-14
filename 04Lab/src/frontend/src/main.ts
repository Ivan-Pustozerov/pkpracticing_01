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