<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <i class="fas fa-user-plus register-icon"></i>
        <h1 class="register-title">Регистрация</h1>
        <p class="register-subtitle">Создайте новый аккаунт</p>
      </div>

      <form @submit.prevent="handleRegister" class="register-form">
        <div class="form-group">
          <label for="name" class="form-label">
            <i class="fas fa-user"></i> Имя пользователя
          </label>
          <input
            id="name"
            v-model="form.name"
            type="text"
            required
            placeholder="Придумайте имя"
            :disabled="authStore.loading"
            class="form-input"
            :class="{ 'error': errors.name }"
            @input="clearError('name')"
          />
          <div v-if="errors.name" class="error-text">{{ errors.name }}</div>
        </div>

        <div class="form-group">
          <label for="email" class="form-label">
            <i class="fas fa-envelope"></i> Email
          </label>
          <input
            id="email"
            v-model="form.email"
            type="email"
            required
            placeholder="Ваш email"
            :disabled="authStore.loading"
            class="form-input"
            :class="{ 'error': errors.email }"
            @input="clearError('email')"
          />
          <div v-if="errors.email" class="error-text">{{ errors.email }}</div>
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
            placeholder="Придумайте пароль"
            :disabled="authStore.loading"
            class="form-input"
            :class="{ 'error': errors.password }"
            @input="clearError('password')"
          />
          <div v-if="errors.password" class="error-text">{{ errors.password }}</div>
        </div>

        <div class="form-group">
          <label for="confirmPassword" class="form-label">
            <i class="fas fa-lock"></i> Подтверждение пароля
          </label>
          <input
            id="confirmPassword"
            v-model="form.confirmPassword"
            type="password"
            required
            placeholder="Повторите пароль"
            :disabled="authStore.loading"
            class="form-input"
            :class="{ 'error': errors.confirmPassword }"
            @input="clearError('confirmPassword')"
          />
          <div v-if="errors.confirmPassword" class="error-text">{{ errors.confirmPassword }}</div>
        </div>

        <div v-if="authStore.error" class="alert alert-error">
          <i class="fas fa-exclamation-circle"></i>
          {{ authStore.error }}
        </div>

        <div v-if="successMessage" class="alert alert-success">
          <i class="fas fa-check-circle"></i>
          {{ successMessage }}
        </div>

        <button
          type="submit"
          class="register-button"
          :disabled="authStore.loading || !isFormValid"
        >
          <template v-if="authStore.loading">
            <span class="spinner"></span>
            <span>Регистрация...</span>
          </template>
          <template v-else>
            <i class="fas fa-user-plus"></i>
            <span>Зарегистрироваться</span>
          </template>
        </button>

        <div class="register-footer">
          <p>Уже есть аккаунт? <router-link to="/login" class="link">Войдите</router-link></p>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import type { UserRequest } from '@/types/api'

const router = useRouter()
const authStore = useAuthStore()

// Форма регистрации
const form = reactive({
  name: '',
  email: '',
  password: '',
  confirmPassword: ''
})

// Ошибки валидации
const errors = reactive({
  name: '',
  email: '',
  password: '',
  confirmPassword: ''
})

// Сообщение об успехе
const successMessage = ref('')

// Проверка валидности формы
const isFormValid = computed(() => {
  return form.name.trim().length > 0 &&
         form.email.trim().length > 0 &&
         form.password.length > 0 &&
         form.confirmPassword.length > 0 &&
         form.password === form.confirmPassword
})

// Очистка ошибки
const clearError = (field: keyof typeof errors) => {
  errors[field] = ''
  authStore.clearError()
}

// Валидация формы
const validateForm = (): boolean => {
  let isValid = true

  // Сброс ошибок
  Object.keys(errors).forEach(key => {
    errors[key as keyof typeof errors] = ''
  })

  // Валидация имени
  if (!form.name.trim()) {
    errors.name = 'Введите имя пользователя'
    isValid = false
  }

  // Валидация email
  if (!form.email.trim()) {
    errors.email = 'Введите email'
    isValid = false
  } else if (!/\S+@\S+\.\S+/.test(form.email)) {
    errors.email = 'Введите корректный email'
    isValid = false
  }

  // Валидация пароля
  if (!form.password) {
    errors.password = 'Введите пароль'
    isValid = false
  } else if (form.password.length < 6) {
    errors.password = 'Пароль должен быть не менее 6 символов'
    isValid = false
  }

  // Подтверждение пароля
  if (!form.confirmPassword) {
    errors.confirmPassword = 'Подтвердите пароль'
    isValid = false
  } else if (form.password !== form.confirmPassword) {
    errors.confirmPassword = 'Пароли не совпадают'
    isValid = false
  }

  return isValid
}

// Обработчик регистрации
const handleRegister = async () => {
  if (!validateForm()) {
    return
  }

  try {
    const userData: UserRequest = {
      name: form.name.trim(),
      email: form.email.trim(),
      password: form.password
    }

    await authStore.register(userData)

    // Успешная регистрация
    successMessage.value = 'Регистрация успешна! Вы будете перенаправлены...'

    // Перенаправление через 2 секунды
    setTimeout(() => {
      router.push('/functions')
    }, 2000)

  } catch (error) {
    // Ошибка уже обработана в store
    console.error('Ошибка регистрации:', error)
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #3498db 0%, #2ecc71 100%);
  padding: 20px;
}

.register-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  padding: 40px;
  width: 100%;
  max-width: 500px;
}

.register-header {
  text-align: center;
  margin-bottom: 40px;
}

.register-icon {
  font-size: 48px;
  color: #3498db;
  margin-bottom: 15px;
}

.register-title {
  font-size: 28px;
  color: #333;
  margin-bottom: 8px;
  font-weight: 700;
}

.register-subtitle {
  color: #666;
  font-size: 16px;
  margin: 0;
}

.register-form {
  margin-top: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #555;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 10px;
  font-size: 16px;
  transition: all 0.3s;
  background: #f8f9fa;
}

.form-input:focus {
  outline: none;
  border-color: #3498db;
  background: white;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
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

.register-button {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #3498db 0%, #2ecc71 100%);
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
  margin-top: 20px;
}

.register-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(52, 152, 219, 0.4);
}

.register-button:disabled {
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

.register-footer {
  margin-top: 30px;
  text-align: center;
  color: #666;
  font-size: 14px;
}

.register-footer p {
  margin: 0;
}

.link {
  color: #3498db;
  text-decoration: none;
  font-weight: 600;
}

.link:hover {
  text-decoration: underline;
}

@media (max-width: 480px) {
  .register-card {
    padding: 30px 20px;
  }

  .register-title {
    font-size: 24px;
  }

  .register-icon {
    font-size: 36px;
  }
}
</style>