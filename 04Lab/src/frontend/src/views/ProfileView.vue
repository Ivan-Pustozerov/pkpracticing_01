<template>
  <div class="profile-view">
    <div class="page-header">
      <h1>Мой профиль</h1>
      <p class="page-subtitle">Управление вашим аккаунтом</p>
    </div>

    <div class="profile-card">
      <div class="profile-header">
        <div class="profile-avatar">
          <i class="fas fa-user"></i>
        </div>
        <div class="profile-info">
          <h2>{{ authStore.user?.name }}</h2>
          <p class="profile-email">{{ authStore.user?.email }}</p>
          <div class="profile-role">
            <span :class="authStore.isAdmin ? 'role-admin' : 'role-user'">
              {{ authStore.isAdmin ? 'Администратор' : 'Пользователь' }}
            </span>
          </div>
        </div>
      </div>

      <div class="profile-content">
        <p>Функционал профиля будет реализован позже</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
</script>

<style scoped>
.profile-view {
  padding: 20px;
}

.profile-card {
  background: white;
  border-radius: 15px;
  padding: 30px;
  margin-top: 30px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 30px;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ecf0f1;
}

.profile-avatar {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #3498db 0%, #2ecc71 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 2rem;
}

.profile-info h2 {
  margin: 0 0 5px 0;
  color: #2c3e50;
}

.profile-email {
  color: #7f8c8d;
  margin: 0 0 10px 0;
}

.profile-role span {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
}

.role-admin {
  background: rgba(231, 76, 60, 0.1);
  color: #e74c3c;
}

.role-user {
  background: rgba(52, 152, 219, 0.1);
  color: #3498db;
}

.profile-content {
  color: #7f8c8d;
  text-align: center;
  padding: 20px;
}
</style>