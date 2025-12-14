<template>
  <div class="users-view">
    <div class="page-header">
      <h1>Управление пользователями</h1>
      <p class="page-subtitle">Административная панель</p>
    </div>

    <div class="admin-panel">
      <div class="admin-info">
        <i class="fas fa-shield-alt"></i>
        <h3>Только для администраторов</h3>
        <p>Этот раздел доступен только пользователям с правами администратора</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// Логика админ-панели будет добавлена позже
</script>

<style scoped>
.users-view {
  padding: 20px;
}

.admin-panel {
  background: white;
  border-radius: 15px;
  padding: 40px;
  margin-top: 30px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
  text-align: center;
}

.admin-info i {
  font-size: 64px;
  color: #e74c3c;
  margin-bottom: 20px;
  opacity: 0.7;
}

.admin-info h3 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.admin-info p {
  color: #7f8c8d;
  max-width: 500px;
  margin: 0 auto;
}
</style>