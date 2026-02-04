<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/axios'

const loading = ref(false)
const formRef = ref()

const settingsForm = ref({
  siteName: '',
  siteUrl: '',
  logo: '',
  favicon: '',
  description: '',
  keywords: '',
  footer: ''
})

const rules = {
  siteName: [{ required: true, message: '请输入网站名称', trigger: 'blur' }],
  siteUrl: [{ required: true, message: '请输入网站地址', trigger: 'blur' }]
}

const fetchSettings = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/settings')
    settingsForm.value = res
  } catch (error) {
    console.error('获取设置失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await request.put('/admin/settings', settingsForm.value)
        ElMessage.success('保存成功')
      } catch (error) {
        console.error('保存失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  fetchSettings()
})
</script>

<template>
  <div class="settings">
    <div class="page-header">
      <h2>基本设置</h2>
    </div>

    <el-card class="form-card" v-loading="loading">
      <el-form ref="formRef" :model="settingsForm" :rules="rules" label-width="120px">
        <el-form-item label="网站名称" prop="siteName">
          <el-input v-model="settingsForm.siteName" placeholder="请输入网站名称" />
        </el-form-item>

        <el-form-item label="网站地址" prop="siteUrl">
          <el-input v-model="settingsForm.siteUrl" placeholder="请输入网站地址，如：https://example.com" />
        </el-form-item>

        <el-form-item label="Logo URL">
          <el-input v-model="settingsForm.logo" placeholder="请输入 Logo 图片地址" />
        </el-form-item>

        <el-form-item label="Favicon URL">
          <el-input v-model="settingsForm.favicon" placeholder="请输入 Favicon 地址" />
        </el-form-item>

        <el-form-item label="网站描述">
          <el-input
            v-model="settingsForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入网站描述"
          />
        </el-form-item>

        <el-form-item label="关键词">
          <el-input v-model="settingsForm.keywords" placeholder="请输入网站关键词，多个关键词用逗号分隔" />
        </el-form-item>

        <el-form-item label="页脚信息">
          <el-input
            v-model="settingsForm.footer"
            type="textarea"
            :rows="2"
            placeholder="请输入页脚版权信息"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSave">
            保存设置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.settings {
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
    max-width: 700px;
  }
}
</style>
