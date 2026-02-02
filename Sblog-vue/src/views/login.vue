<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/user'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

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

const handleLogin = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login({
          username: loginForm.username,
          password: loginForm.password
        })

        // 假设后端返回格式: { code: 200, data: { token: 'xxx' }, message: 'success' }
        if (res.code === 200) {
          localStorage.setItem('token', res.data.token)
          ElMessage.success('登录成功')
          router.push('/index')
        }
      } catch (error) {
        console.error('登录失败:', error)
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
