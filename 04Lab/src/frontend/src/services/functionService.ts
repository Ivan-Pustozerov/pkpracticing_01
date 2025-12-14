/**
 * Сервис для работы с математическими функциями
 * Соответствует FunctionController в Spring бэкенде
 */

import apiClient from '@/api/client'
import type {
  MathFunctionBase,
  AnalyticFunctionRequest,
  AnalyticFunctionResponse,
  TabulatedFunctionRequest,
  TabulatedFunctionResponse
} from '@/types/api'

class FunctionService {
  /**
   * Получить все функции пользователя
   * @param type - опциональный фильтр по типу
   */
  async getAllFunctions(type?: 'analytic' | 'tabulated'): Promise<MathFunctionBase[]> {
    try {
      const params = type ? { type } : {}
      const response = await apiClient.get<MathFunctionBase[]>('/api/functions', { params })
      return response.data
    } catch (error) {
      console.error('Error fetching functions:', error)
      throw error
    }
  }

  /**
   * Получить конкретную функцию по ID
   */
  async getFunction(id: number): Promise<AnalyticFunctionResponse | TabulatedFunctionResponse> {
    try {
      const response = await apiClient.get(`/api/functions/${id}`)
      return response.data
    } catch (error) {
      console.error(`Error fetching function ${id}:`, error)
      throw error
    }
  }

  /**
   * Создать аналитическую функцию
   */
  async createAnalyticFunction(data: AnalyticFunctionRequest): Promise<AnalyticFunctionResponse> {
    try {
      const response = await apiClient.post<AnalyticFunctionResponse>('/api/functions/analytic', data)
      return response.data
    } catch (error) {
      console.error('Error creating analytic function:', error)
      throw error
    }
  }

  /**
   * Создать табличную функцию
   */
  async createTabulatedFunction(data: TabulatedFunctionRequest): Promise<TabulatedFunctionResponse> {
    try {
      const response = await apiClient.post<TabulatedFunctionResponse>('/api/functions/tabulated', data)
      return response.data
    } catch (error) {
      console.error('Error creating tabulated function:', error)
      throw error
    }
  }

  /**
   * Удалить функцию
   */
  async deleteFunction(id: number): Promise<void> {
    try {
      await apiClient.delete(`/api/functions/${id}`)
    } catch (error) {
      console.error(`Error deleting function ${id}:`, error)
      throw error
    }
  }

  /**
   * Получить только аналитические функции (с деталями)
   */
  async getAnalyticFunctions(): Promise<AnalyticFunctionResponse[]> {
    try {
      const baseFunctions = await this.getAllFunctions('analytic')
      const detailedPromises = baseFunctions.map(func => this.getFunction(func.id))
      const detailedFunctions = await Promise.all(detailedPromises)

      return detailedFunctions.filter((func): func is AnalyticFunctionResponse =>
        'functionExpression' in func
      )
    } catch (error) {
      console.error('Error fetching analytic functions:', error)
      throw error
    }
  }

  /**
   * Получить только табличные функции (с деталями)
   */
  async getTabulatedFunctions(): Promise<TabulatedFunctionResponse[]> {
    try {
      const baseFunctions = await this.getAllFunctions('tabulated')
      const detailedPromises = baseFunctions.map(func => this.getFunction(func.id))
      const detailedFunctions = await Promise.all(detailedPromises)

      return detailedFunctions.filter((func): func is TabulatedFunctionResponse =>
        'xVals' in func && 'yVals' in func
      )
    } catch (error) {
      console.error('Error fetching tabulated functions:', error)
      throw error
    }
  }
}

// Экспортируем singleton экземпляр
const functionService = new FunctionService()
export default functionService