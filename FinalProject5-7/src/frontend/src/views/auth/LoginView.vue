<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <i class="fas fa-calculator login-icon"></i>
        <h1 class="login-title">MathFunctions</h1>
        <p class="login-subtitle">Система математических функций</p>
      </div>

      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="name" class="form-label">
            <i class="fas fa-user"></i> Имя пользователя
          </label>
          <input
            id="name"
            v-model="form.name"
            type="text"
            required
            placeholder="Введите ваше имя"
            :disabled="authStore.loading"
            class="form-input"
            :class="{ 'error': errors.name }"
            @input="clearError('name')"
          />
          <div v-if="errors.name" class="error-text">{{ errors.name }}</div>
        </div>

        <div class="form-group">
          <label for="password" class="form-label">
            <i class="fas fa-lock"></i> Пароль
          </label>
          <input
            id="password"
            v-model="form.password"
            type="password"
            required
            placeholder="Введите пароль"
            :disabled="authStore.loading"
            class="form-input"
            :class="{ 'error': errors.password }"
            @input="clearError('password')"
          />
          <div v-if="errors.password" class="error-text">{{ errors.password }}</div>
        </div>

        <!-- Общая ошибка формы -->
        <div v-if="authStore.error" class="alert alert-error">
          <i class="fas fa-exclamation-circle"></i>
          {{ authStore.error }}
        </div>

        <!-- Сообщение об успехе (например, после регистрации) -->
        <div v-if="successMessage" class="alert alert-success">
          <i class="fas fa-check-circle"></i>
          {{ successMessage }}
        </div>

        <button
          type="submit"
          class="login-button"
          :disabled="authStore.loading || !isFormValid"
        >
          <template v-if="authStore.loading">
            <span class="spinner"></span>
            <span>Вход...</span>
          </template>
          <template v-else>
            <i class="fas fa-sign-in-alt"></i>
            <span>Войти</span>
          </template>
        </button>

        <div class="login-footer">
          <p>Нет аккаунта? <router-link to="/register" class="link">Зарегистрируйтесь</router-link></p>
          <p class="demo-hint">
            <i class="fas fa-info-circle"></i>
            Все пока находится на этапе тестирования
          </p>
        </div>
      </form>
    </div>

    <div class="login-background">
      <div class="math-symbol">∫</div>
      <div class="math-symbol">∑</div>
      <div class="math-symbol">ƒ</div>
      <div class="math-symbol">∞</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import type { LoginRequest } from '@/types/api'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// Форма входа
const form = reactive<LoginRequest>({
  name: '',
  password: ''
})

// Ошибки валидации
const errors = reactive({
  name: '',
  password: ''
})

// Сообщение об успехе (например, после регистрации)
const successMessage = ref('')

// Проверка валидности формы
const isFormValid = computed(() => {
  return form.name.trim().length > 0 && form.password.length > 0
})

// Очистка ошибки для конкретного поля
const clearError = (field: keyof typeof errors) => {
  errors[field] = ''
  authStore.clearError()
}

// Обработчик отправки формы
const handleLogin = async () => {
  // Сброс ошибок
  errors.name = ''
  errors.password = ''
  authStore.clearError()

  // Валидация
  if (!form.name.trim()) {
    errors.name = 'Введите имя пользователя'
    return
  }

  if (!form.password) {
    errors.password = 'Введите пароль'
    return
  }

  try {
    // Вызов логина из store
    await authStore.login({
      name: form.name.trim(),
      password: form.password
    })

    // Перенаправление
    const redirectPath = route.query.redirect as string || '/functions'
    router.push(redirectPath)

  } catch (error) {
    // Ошибка уже обработана в store
    console.error('Ошибка входа:', error)
  }
}

// При монтировании компонента
onMounted(() => {
  // Очищаем сообщения
  authStore.clearError()

  // Если есть сообщение об успешной регистрации в query параметрах
  if (route.query.registered === 'true') {
    successMessage.value = 'Регистрация успешна! Теперь вы можете войти в систему.'
  }

   /*
  if (import.meta.env.DEV) {
    form.name = 'admin'
    form.password = 'admin123'
  }
  */
})
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.login-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  padding: 40px;
  width: 100%;
  max-width: 450px;
  z-index: 1;
  position: relative;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-icon {
  font-size: 48px;
  color: #667eea;
  margin-bottom: 15px;
}

.login-title {
  font-size: 28px;
  color: #333;
  margin-bottom: 8px;
  font-weight: 700;
}

.login-subtitle {
  color: #666;
  font-size: 16px;
  margin: 0;
}

.form-group {
  margin-bottom: 24px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #555;
  font-size: 14px;
}

.form-label i {
  margin-right: 8px;
  color: #667eea;
}

.form-input {
  width: 100%;
  padding: 14px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 10px;
  font-size: 16px;
  transition: all 0.3s;
  background: #f8f9fa;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-input.error {
  border-color: #e74c3c;
}

.form-input:disabled {
  background: #f5f5f5;
  cursor: not-allowed;
}

.error-text {
  color: #e74c3c;
  font-size: 14px;
  margin-top: 5px;
}

.alert {
  padding: 14px;
  border-radius: 10px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
}

.alert-error {
  background: #ffeaea;
  color: #e74c3c;
  border: 1px solid #f5c6cb;
}

.alert-success {
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.login-button {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
}

.login-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.login-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top: 3px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.login-footer {
  margin-top: 30px;
  text-align: center;
  color: #666;
  font-size: 14px;
}

.login-footer p {
  margin: 8px 0;
}

.link {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
}

.link:hover {
  text-decoration: underline;
}

.demo-hint {
  font-size: 12px;
  color: #888;
  background: #f8f9fa;
  padding: 8px;
  border-radius: 6px;
  margin-top: 15px !important;
}

.demo-hint i {
  margin-right: 5px;
  color: #667eea;
}

/* Математические символы на фоне */
.login-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  opacity: 0.1;
}

.math-symbol {
  position: absolute;
  font-size: 150px;
  color: white;
  font-weight: bold;
  opacity: 0.5;
}

.math-symbol:nth-child(1) { top: 10%; left: 10%; }
.math-symbol:nth-child(2) { top: 20%; right: 15%; }
.math-symbol:nth-child(3) { bottom: 30%; left: 20%; }
.math-symbol:nth-child(4) { bottom: 20%; right: 10%; }

/* Адаптивность */
@media (max-width: 480px) {
  .login-card {
    padding: 30px 20px;
  }

  .login-title {
    font-size: 24px;
  }

  .login-icon {
    font-size: 36px;
  }
}
</style>