<template>
  <div class="not-found-view">
    <div class="error-container">
      <div class="error-code">404</div>
      <h1 class="error-title">Страница не найдена</h1>
      <p class="error-message">Запрашиваемая страница не существует или была перемещена</p>
      <div class="error-actions">
        <router-link to="/" class="btn btn-primary">
          <i class="fas fa-home"></i> На главную
        </router-link>
        <router-link to="/functions" class="btn btn-secondary">
          <i class="fas fa-list"></i> К функциям
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// Простая 404 страница
</script>

<style scoped>
.not-found-view {
  min-height: 70vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.error-container {
  text-align: center;
  max-width: 600px;
}

.error-code {
  font-size: 120px;
  font-weight: 900;
  color: #3498db;
  line-height: 1;
  margin-bottom: 20px;
  opacity: 0.2;
}

.error-title {
  font-size: 36px;
  color: #2c3e50;
  margin-bottom: 20px;
}

.error-message {
  color: #7f8c8d;
  font-size: 18px;
  margin-bottom: 40px;
}

.error-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
  flex-wrap: wrap;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 12px 24px;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s;
}

.btn-primary {
  background: linear-gradient(135deg, #3498db 0%, #2ecc71 100%);
  color: white;
}

.btn-secondary {
  background: #95a5a6;
  color: white;
}

.btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

@media (max-width: 480px) {
  .error-code {
    font-size: 80px;
  }

  .error-title {
    font-size: 28px;
  }
}
</style>