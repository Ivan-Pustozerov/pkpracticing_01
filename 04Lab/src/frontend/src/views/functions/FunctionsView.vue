<template>
  <div class="functions-view">
    <div class="page-header">
      <h1>Мои функции</h1>
      <p class="page-subtitle">Список ваших математических функций</p>
    </div>

    <div class="empty-state">
      <i class="fas fa-functions"></i>
      <h3>Функций пока нет</h3>
      <p>Создайте свою первую математическую функцию</p>
      <router-link to="/functions/create" class="btn btn-primary">
        <i class="fas fa-plus"></i> Создать функцию
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
// Позже добавим логику
</script>

<style scoped>
.functions-view {
  padding: 20px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
  margin-top: 30px;
}

.empty-state i {
  font-size: 64px;
  color: #3498db;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-state h3 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.empty-state p {
  color: #7f8c8d;
  margin-bottom: 30px;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
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

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(52, 152, 219, 0.3);
}
</style>