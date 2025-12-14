import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

/**
 * Конфигурация Vite для разработки и сборки
 * Для разработки: проксирует API запросы к Spring (localhost:8080)
 * Для продакшена: собирает в папку static Spring проекта
 */
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 3000,  // Порт разработки фронтенда
    open: true,   // Автоматически открывать браузер
    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // Ваш Spring бэкенд
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path // Проксируем как есть
      }
    }
  },
  build: {
    outDir: 'dist',  // По умолчанию для разработки
    // Для продакшена раскомментируйте:
    // outDir: '../src/main/resources/static',
    // emptyOutDir: true
  }
})
