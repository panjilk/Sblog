<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/user'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const rememberMe = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, message: '用户名不能少于3个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 3, message: '密码不能少于3个字符', trigger: 'blur' }
  ]
}

// 页面加载时检查是否有保存的用户名
onMounted(() => {
  const savedUsername = localStorage.getItem('rememberedUsername')
  if (savedUsername) {
    loginForm.username = savedUsername
    loginForm.password = localStorage.getItem('rememberedpwd')
    rememberMe.value = true
  }
})

const handleLogin = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login({
          username: loginForm.username,
          password: loginForm.password
        })
        console.log("登录响应:", res)
        console.log("响应类型:", typeof res)

        // 处理不同的响应码
        if (res.code === 200) {
          localStorage.setItem('token', res.data.token)

          // 如果勾选了记住密码，保存用户名
          if (rememberMe.value) {
            localStorage.setItem('rememberedUsername', loginForm.username)
             localStorage.setItem('rememberedpwd', loginForm.password)
          } else {
            localStorage.removeItem('rememberedUsername')
          }

          ElMessage.success('登录成功')
          router.push('/admin')
        } else {
          // 非 200 状态码，显示错误消息
          ElMessage.error(res.message || '登录失败')
        }
      } catch (error) {
        console.error('登录失败:', error)
        ElMessage.error(error.response?.data?.message || '登录失败，请检查网络连接')
      } finally {
        loading.value = false
      }
    } else {
      ElMessage.error('请正确填写表单')
    }
  })
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2>用户登录</h2>
      </template>

      <el-form ref="formRef" :model="loginForm" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="rememberMe">记住密码</el-checkbox>
        </el-form-item>

        <el-form-item>
          <el-row :gutter="50" style="width: 100%">
            <el-col :span="12">
              <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">登录</el-button>
            </el-col>
            <el-col :span="12">
              <el-button type="success" @click="goToRegister" style="width: 100%">注册</el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

}

.login-card {
  width: 400px;
  border-radius: 5%;
  h2 {
    text-align: center;
    margin: 0;
    color: #333;
  }
}

:deep(.el-card__header) {
  padding: 20px;
}
</style>
