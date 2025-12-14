<template>
  <div class="create-function-view">
    <div class="page-header">
      <h1>Создать функцию</h1>
      <p class="page-subtitle">Добавьте новую математическую функцию</p>
    </div>

    <div class="function-types">
      <div class="type-card" @click="selectType('analytic')">
        <i class="fas fa-square-root-alt"></i>
        <h3>Аналитическая</h3>
        <p>Функция заданная формулой</p>
      </div>

      <div class="type-card" @click="selectType('tabulated')">
        <i class="fas fa-table"></i>
        <h3>Табличная</h3>
        <p>Функция заданная таблицей значений</p>
      </div>
    </div>

    <div v-if="selectedType" class="function-form">
      <h3>Создание {{ selectedType === 'analytic' ? 'аналитической' : 'табличной' }} функции</h3>
      <p>Форма будет реализована позже</p>
      <button @click="selectedType = null" class="btn btn-secondary">
        <i class="fas fa-arrow-left"></i> Назад
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const selectedType = ref<'analytic' | 'tabulated' | null>(null)

const selectType = (type: 'analytic' | 'tabulated') => {
  selectedType.value = type
}
</script>

<style scoped>
.create-function-view {
  padding: 20px;
}

.function-types {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 30px;
}

.type-card {
  background: white;
  border-radius: 15px;
  padding: 30px;
  text-align: center;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.type-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0,0,0,0.1);
  border-color: #3498db;
}

.type-card i {
  font-size: 48px;
  color: #3498db;
  margin-bottom: 20px;
}

.type-card h3 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.type-card p {
  color: #7f8c8d;
  margin: 0;
}

.function-form {
  background: white;
  border-radius: 15px;
  padding: 30px;
  margin-top: 30px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 8px;
  border: none;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-secondary {
  background: #95a5a6;
  color: white;
}

.btn-secondary:hover {
  background: #7f8c8d;
}
</style>