// src/stores/theme.store.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export type ThemeName = 'light' | 'dark' | 'ultra-dark' | 'eco'

export const useThemeStore = defineStore('theme', () => {
  // Текущая тема
  const currentTheme = ref<ThemeName>('light')

  // Названия тем для отображения
  const themeNames = {
    'light': 'Светлая',
    'dark': 'Темная', 
    'ultra-dark': 'Ультра-темная',
    'eco': 'Эко'
  } as const

  // Иконки тем (ИЗМЕНЕНО: эмодзи → Font Awesome иконки)
  const themeIcons = {
    'light': '<i class="fas fa-sun"></i>',          // ☀️ → fa-sun
    'dark': '<i class="fas fa-moon"></i>',          // 🌙 → fa-moon
    'ultra-dark': '<i class="fa-brands fa-squarespace"></i>', // 🌌 → fa-moon-stars
    'eco': '<i class="fas fa-leaf"></i>'           // 🌿 → fa-leaf
  } as const

  // Иконки тем (альтернативный вариант - только классы)
  const themeIconClasses = {
    'light': 'fas fa-sun',
    'dark': 'fas fa-moon',
    'ultra-dark': 'fas fa-moon-stars',
    'eco': 'fas fa-leaf'
  } as const

  // Описание тем
  const themeDescriptions = {
    'light': 'Классическая светлая тема',
    'dark': 'Темная тема с синими акцентами',
    'ultra-dark': 'Черная тема с фиолетовыми акцентами',
    'eco': 'Экологичная тема с зелеными тонами'
  } as const

  // Проверяем, является ли строка названием темы
  const isThemeName = (theme: string): theme is ThemeName => {
    return ['light', 'dark', 'ultra-dark', 'eco'].includes(theme)
  }

  // Загружаем тему из localStorage при инициализации
  const loadTheme = () => {
    try {
      const savedTheme = localStorage.getItem('app-theme')
      if (savedTheme && isThemeName(savedTheme)) {
        currentTheme.value = savedTheme
        applyTheme(savedTheme)
      } else {
        applyTheme('light')
      }
    } catch (error) {
      console.warn('Ошибка при загрузке темы:', error)
      applyTheme('light')
    }
  }

  // Применяем тему
  const applyTheme = (theme: ThemeName) => {
    // Удаляем предыдущие классы тем
    const htmlElement = document.documentElement
    const themeClasses = [
      'theme-light',
      'theme-dark', 
      'theme-ultra-dark',
      'theme-eco'
    ]

    themeClasses.forEach(className => htmlElement.classList.remove(className))

    // Добавляем класс текущей темы
    htmlElement.classList.add(`theme-${theme}`)

    // Добавляем data-атрибут для CSS
    htmlElement.setAttribute('data-theme', theme)

    // Сохраняем в localStorage
    try {
      localStorage.setItem('app-theme', theme)
    } catch (error) {
      console.warn('Не удалось сохранить тему в localStorage:', error)
    }

    currentTheme.value = theme

    // Генерируем событие для отслеживания
    window.dispatchEvent(new CustomEvent('theme-changed', { detail: theme }))
  }

  // Устанавливаем тему
  const setTheme = (theme: ThemeName) => {
    if (!isThemeName(theme)) {
      console.warn(`Неизвестная тема: ${theme}`)
      return
    }
    applyTheme(theme)
  }

  // Переключаем на следующую тему
  const nextTheme = () => {
    const themes: ThemeName[] = ['light', 'dark', 'ultra-dark', 'eco']
    const currentIndex = themes.indexOf(currentTheme.value)
    const nextIndex = (currentIndex + 1) % themes.length
    setTheme(themes[nextIndex])
  }

  // Получаем текущую иконку темы (ИЗМЕНЕНО: возвращаем HTML или классы)
  const currentThemeIcon = computed(() => themeIcons[currentTheme.value])
  
  // ИЛИ для использования v-html:
  const currentThemeIconHtml = computed(() => themeIcons[currentTheme.value])
  
  // ИЛИ для использования классов:
  const currentThemeIconClass = computed(() => themeIconClasses[currentTheme.value])

  // Получаем текущее название темы
  const currentThemeName = computed(() => themeNames[currentTheme.value])

  // Получаем описание текущей темы
  const currentThemeDescription = computed(() => themeDescriptions[currentTheme.value])

  // Инициализируем тему при создании store
  loadTheme()

  return {
    // State
    currentTheme,

    // Getters
    currentThemeIcon,
    currentThemeIconHtml,      // НОВОЕ: для v-html
    currentThemeIconClass,     // НОВОЕ: для классов
    currentThemeName,
    currentThemeDescription,
    themeNames,
    themeIcons,
    themeIconClasses,          // НОВОЕ
    themeDescriptions,

    // Actions
    setTheme,
    nextTheme,
    loadTheme,
    applyTheme,
    isThemeName
  }
})