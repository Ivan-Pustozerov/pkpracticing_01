/**
 * Сервис для работы с пользователями (только для администраторов)
 * Соответствует UserController в Spring
 */
import apiClient from '@/api/client'
import type { User, UserRequest } from '@/types/api'

class UserService {
  /**
   * Получить всех пользователей (только для ADMIN)
   * @returns Список пользователей
   */
  async getAllUsers(): Promise<User[]> {
    const response = await apiClient.get<User[]>('/api/users')
    return response.data
  }

  /**
   * Получить пользователя по ID
   * @param id - ID пользователя
   * @returns Данные пользователя
   */
  async getUser(id: number): Promise<User> {
    const response = await apiClient.get<User>(`/api/users/${id}`)
    return response.data
  }

  /**
   * Создать пользователя (только для ADMIN)
   * @param data - данные пользователя
   * @param isAdmin - создать как администратора
   * @returns Созданный пользователь
   */
  async createUser(data: UserRequest, isAdmin: boolean = false): Promise<User> {
    const endpoint = isAdmin ? '/api/users/admin' : '/api/users'
    const response = await apiClient.post<User>(endpoint, data)
    return response.data
  }

  /**
   * Обновить пользователя
   * @param id - ID пользователя
   * @param data - новые данные
   * @returns Обновленный пользователь
   */
  async updateUser(id: number, data: Partial<UserRequest>): Promise<User> {
    const response = await apiClient.put<User>(`/api/users/${id}`, data)
    return response.data
  }

  /**
   * Изменить роль пользователя (только для ADMIN)
   * @param id - ID пользователя
   * @param isAdmin - назначить/снять права администратора
   * @returns Обновленный пользователь
   */
  async changeUserRole(id: number, isAdmin: boolean): Promise<User> {
    const response = await apiClient.put<User>(`/api/users/${id}/role`, { isAdmin })
    return response.data
  }

  /**
   * Удалить пользователя (только для ADMIN)
   * @param id - ID пользователя
   */
  async deleteUser(id: number): Promise<void> {
    await apiClient.delete(`/api/users/${id}`)
  }

  /**
   * Получить текущего пользователя (по токену)
   * @returns Данные текущего пользователя
   */
  async getCurrentUser(): Promise<User | null> {
    try {
      // Получаем свой профиль через /api/users/{id}
      // Для этого нужен ID текущего пользователя
      // Альтернативно: создать endpoint /api/users/me в Spring
      return null
    } catch {
      return null
    }
  }
}

export default new UserService()