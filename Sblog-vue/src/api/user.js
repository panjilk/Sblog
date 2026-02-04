import request from '@/utils/axios'

// 登录
export const login = (data) => {
  return request.post('/admin/users/login', data)
}

// 注册
export const register = (data) => {
  return request.post('/admin/users/register', data)
}

// 获取用户信息
export const getUserInfo = () => {
  return request.get('/admin/users/info')
}

// 退出登录
export const logout = () => {
  return request.post('/admin/users/logout')
}

// 检查用户名是否存在
export const checkUsername = (username) => {
  return request.get('/admin/users/check-username', { params: { username } })
}
