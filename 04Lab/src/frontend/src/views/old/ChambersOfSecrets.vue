<template>
  <div class="chambers-view">
    <div class="chambers-container">
      <div class="chambers-header">
        <h1>Chambers of Secrets</h1>
        <p class="subtitle">Введите секретный код...</p>
      </div>

      <div class="code-input-section">
        <div class="input-container">
          <input
            v-model="currentCode"
            @keyup.enter="submitCode"
            placeholder="Введите код..."
            class="code-input"
          />
          <button @click="submitCode" class="submit-btn">
            Отправить
          </button>
        </div>
      </div>

      <div class="response-section" v-if="currentResponse">
        <div class="response-card">
          <h4>Ответ:</h4>
          <p>{{ currentResponse }}</p>
        </div>
      </div>

      <div class="back-section">
        <button @click="goBack" class="back-btn">
          Вернуться в профиль
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const currentCode = ref('')
const currentResponse = ref('')

const secretCodes: Record<string, string> = {
  'test': 'MEH-MEH',
  'hello': 'Приветствую, искатель секретов!',
  'help': 'Попробуйте коды: test, hello',
  'secret': 'Вы нашли настоящий секрет! 🗝️',
  '42': 'Ответ на главный вопрос жизни, вселенной и всего такого!'
}

const submitCode = () => {
  if (!currentCode.value.trim()) return

  const code = currentCode.value.trim().toLowerCase()

  if (secretCodes[code]) {
    currentResponse.value = secretCodes[code]
  } else {
    const responses = [
      'Ничего не происходит...',
      'Код не распознан.',
      'Это не сработало.'
    ]
    currentResponse.value = responses[Math.floor(Math.random() * responses.length)]
  }

  currentCode.value = ''
}

const goBack = () => {
  router.push('/profile')
}
</script>

<style scoped>
.chambers-view {
  min-height: 100vh;
  background: linear-gradient(135deg, #0c0c1d 0%, #1a1a2e 100%);
  color: #e0e0ff;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chambers-container {
  max-width: 600px;
  width: 100%;
  background: rgba(26, 26, 46, 0.9);
  border-radius: 15px;
  padding: 30px;
  border: 1px solid rgba(52, 152, 219, 0.2);
}

.chambers-header {
  text-align: center;
  margin-bottom: 30px;
}

.chambers-header h1 {
  font-size: 2.5rem;
  color: #f1c40f;
  margin-bottom: 10px;
}

.subtitle {
  color: #95a5a6;
  font-style: italic;
}

.code-input-section {
  margin-bottom: 20px;
}

.input-container {
  display: flex;
  gap: 10px;
}

.code-input {
  flex: 1;
  padding: 12px 15px;
  background: rgba(44, 62, 80, 0.7);
  border: 2px solid #3498db;
  border-radius: 8px;
  color: white;
  font-size: 1rem;
}

.code-input:focus {
  outline: none;
  border-color: #f1c40f;
}

.submit-btn {
  padding: 12px 24px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
}

.submit-btn:hover {
  background: #2980b9;
}

.response-card {
  background: rgba(44, 62, 80, 0.7);
  border-radius: 10px;
  padding: 20px;
  border-left: 4px solid #2ecc71;
  margin-top: 20px;
}

.response-card h4 {
  color: #2ecc71;
  margin-bottom: 10px;
}

.back-section {
  margin-top: 30px;
  text-align: center;
}

.back-btn {
  padding: 12px 24px;
  background: #7f8c8d;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.back-btn:hover {
  background: #95a5a6;
}
</style>