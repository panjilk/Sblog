import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从 localStorage 读取 token
    const token = localStorage.getItem('token')
    if (token) {
      // 自动添加到请求头
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  res => {
    // 返回 res.data
    return res.data
  },
  error => {
    const { response } = error
    let message = '请求失败'

    if (response) {
      switch (response.status) {
        case 400:
          message = response.data?.message || '请求参数错误'
          break
        case 401:
          message = '未授权，请重新登录'
          // 401 自动跳转登录页
          localStorage.removeItem('token')
          window.location.href = '/login'
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求资源不存在'
          break
        case 500:
          message = '服务器错误'
          break
        default:
          message = response.data?.message || `请求失败 (${response.status})`
      }
    } else if (error.code === 'ECONNABORTED') {
      message = '请求超时'
    } else {
      message = '网络连接失败'
    }

    // 统一错误提示
    ElMessage.error(message)

    return Promise.reject(error)
  }
)

export default request
