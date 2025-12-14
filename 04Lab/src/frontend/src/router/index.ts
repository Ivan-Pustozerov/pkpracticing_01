/**
 * Конфигурация маршрутизатора Vue Router
 * Защищает маршруты на основе авторизации и ролей
 */
import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// Определение маршрутов
const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/functions',
    meta: { title: 'Главная' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/LoginView.vue'),
    meta: {
      title: 'Вход в систему',
      requiresGuest: true,
      layout: 'auth'
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/RegisterView.vue'),
    meta: {
      title: 'Регистрация',
      requiresGuest: true,
      layout: 'auth'
    }
  },
  {
    path: '/functions',
    name: 'Functions',
    component: () => import('@/views/functions/FunctionsView.vue'),
    meta: {
      title: 'Мои функции',
      requiresAuth: true,
      breadcrumb: 'Функции'
    }
  },
  {
    path: '/functions/create',
    name: 'CreateFunction',
    component: () => import('@/views/functions/CreateFunctionView.vue'),
    meta: {
      title: 'Создать функцию',
      requiresAuth: true,
      breadcrumb: 'Создать'
    }
  },
  {
    path: '/functions/:id',
    name: 'FunctionDetail',
    component: () => import('@/views/functions/FunctionDetailView.vue'),
    meta: {
      title: 'Детали функции',
      requiresAuth: true,
      breadcrumb: 'Детали'
    }
  },
  {
    path: '/users',
    name: 'Users',
    component: () => import('@/views/admin/UsersView.vue'),
    meta: {
      title: 'Управление пользователями',
      requiresAuth: true,
      requiresAdmin: true,
      breadcrumb: 'Пользователи'
    }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/ProfileView.vue'),
    meta: {
      title: 'Мой профиль',
      requiresAuth: true,
      breadcrumb: 'Профиль'
    }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFoundView.vue'),
    meta: {
      title: 'Страница не найдена'
    }
  }
]

// Создание роутера
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// Навигационный гард (защита маршрутов)
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  // Проверяем авторизацию при каждом переходе
  authStore.checkAuth()

  const isAuthenticated = authStore.isAuthenticated
  const isAdmin = authStore.isAdmin

  // Устанавливаем заголовок страницы
  if (to.meta.title) {
    document.title = `${to.meta.title} | MathFunctions`
  }

  // Проверка: требуется авторизация, но пользователь не авторизован
  if (to.meta.requiresAuth && !isAuthenticated) {
    console.log('Требуется авторизация, перенаправление на /login')
    next('/login')
    return
  }

  // Проверка: требуется быть гостем, но пользователь авторизован
  if (to.meta.requiresGuest && isAuthenticated) {
    console.log('Авторизованный пользователь не может посещать эту страницу, перенаправление на /functions')
    next('/functions')
    return
  }

  // Проверка: требуется роль ADMIN, но пользователь не администратор
  if (to.meta.requiresAdmin && !isAdmin) {
    console.log('Требуется роль ADMIN, перенаправление на /functions')
    next('/functions')
    return
  }

  // Все проверки пройдены
  next()
})

export default router