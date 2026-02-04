<script setup>
import { reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register, checkUsername } from '@/api/user'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const checkingUsername = ref(false)
const usernameStatus = ref('') // 'available', 'taken', ''

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validateUsername = async (rule, value, callback) => {
  if (!value) {
    callback()
    return
  }
  if (value.length < 3) {
    callback()
    return
  }
  // 检查用户名是否已存在
  try {
    checkingUsername.value = true
    const res = await checkUsername(value)
    if (res.code === 200 && !res.data) {
      callback(new Error('用户名已存在'))
    } else {
      callback()
    }
  } catch (error) {
    callback()
  } finally {
    checkingUsername.value = false
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' },
    { validator: validateUsername, trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 3, max: 20, message: '密码长度为3-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 实时检查用户名状态
watch(() => registerForm.username, async (newVal) => {
  if (newVal && newVal.length >= 3) {
    try {
      checkingUsername.value = true
      const res = await checkUsername(newVal)
      if (res.code === 200) {
        usernameStatus.value = res.data ? 'available' : 'taken'
      }
    } catch (error) {
      usernameStatus.value = ''
    } finally {
      checkingUsername.value = false
    }
  } else {
    usernameStatus.value = ''
  }
})

const handleRegister = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await register({
          username: registerForm.username,
          password: registerForm.password
        })

        if (res.code === 200) {
          ElMessage.success('注册成功，请登录')
          router.push('/login')
        } else {
          ElMessage.error(res.message || '注册失败')
        }
      } catch (error) {
        if (error.response?.data?.message) {
          ElMessage.error(error.response.data.message)
        } else {
          ElMessage.error('注册失败，请稍后重试')
        }
      } finally {
        loading.value = false
      }
    } else {
      ElMessage.error('请正确填写表单')
    }
  })
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <h2>用户注册</h2>
      </template>

      <el-form ref="formRef" :model="registerForm" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
          >
            <template #suffix>
              <span v-if="checkingUsername" class="checking-text">检查中...</span>
              <span v-else-if="usernameStatus === 'available'" class="available-text">可用</span>
              <span v-else-if="usernameStatus === 'taken'" class="taken-text">已存在</span>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码（6-20个字符）" show-password />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" show-password />
        </el-form-item>

        <el-form-item>
          <el-row :gutter="50" style="width: 100%">
            <el-col :span="12">
              <el-button type="info" @click="goToLogin" style="width: 100%">返回登录</el-button>
            </el-col>
            <el-col :span="12">
              <el-button type="success" @click="handleRegister" :loading="loading" :disabled="usernameStatus === 'taken'" style="width: 100%">注册</el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-card {
  width: 420px;
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

.checking-text {
  color: #909399;
  font-size: 12px;
}

.available-text {
  color: #67c23a;
  font-size: 12px;
}

.taken-text {
  color: #f56c6c;
  font-size: 12px;
}
</style>
