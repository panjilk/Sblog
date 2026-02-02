import request from '@/utils/axios'

// 登录
export const login = (data) => {
  return request.post('/login', data)
}

// 注册
export const register = (data) => {
  return request.post('/register', data)
}

// 获取用户信息
export const getUserInfo = () => {
  return request.get('/user/info')
}

// 退出登录
export const logout = () => {
  return request.post('/logout')
}
