<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/axios'

const loading = ref(false)
const formRef = ref()

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }]
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await request.put('/admin/password', {
          oldPassword: passwordForm.value.oldPassword,
          newPassword: passwordForm.value.newPassword
        })
        ElMessage.success('密码修改成功，请重新登录')
        // 清空表单
        passwordForm.value = {
          oldPassword: '',
          newPassword: '',
          confirmPassword: ''
        }
      } catch (error) {
        console.error('密码修改失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

const handleReset = () => {
  formRef.value.resetFields()
}
</script>

<template>
  <div class="security-password">
    <div class="page-header">
      <h2>修改密码</h2>
    </div>

    <el-card class="form-card">
      <el-form ref="formRef" :model="passwordForm" :rules="rules" label-width="120px" style="max-width: 500px">
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入当前密码" show-password />
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码（至少6位）" show-password />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">
            修改密码
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-alert
        title="安全提示"
        type="warning"
        :closable="false"
        show-icon
        style="margin-top: 30px"
      >
        <ul style="margin: 5px 0 0 20px; padding-left: 20px;">
          <li>密码长度至少为 6 位</li>
          <li>建议使用字母、数字和特殊字符的组合</li>
          <li>修改密码后需要重新登录</li>
        </ul>
      </el-alert>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.security-password {
  .page-header {
    margin-bottom: 20px;

    h2 {
      font-size: 24px;
      font-weight: 600;
      margin: 0;
      color: #333;
    }
  }

  .form-card {
    max-width: 600px;
  }
}
</style>
