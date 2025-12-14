<template>
  <div class="function-detail-view">
    <div class="page-header">
      <h1>Детали функции</h1>
      <p class="page-subtitle">Просмотр и управление функцией</p>
    </div>

    <div class="function-detail">
      <p>Компонент деталей функции будет реализован позже</p>
      <router-link to="/functions" class="btn btn-primary">
        <i class="fas fa-arrow-left"></i> Назад к списку
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
// Логика будет добавлена позже
</script>

<style scoped>
.function-detail-view {
  padding: 20px;
}

.function-detail {
  background: white;
  border-radius: 15px;
  padding: 30px;
  margin-top: 30px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
  text-align: center;
}
</style>