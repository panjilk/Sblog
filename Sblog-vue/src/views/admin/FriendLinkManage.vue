<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/axios'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('添加友情链接')
const friendLinks = ref([])

const linkForm = ref({
  id: null,
  name: '',
  url: '',
  logo: '',
  description: '',
  sortOrder: 0,
  isActive: true
})

const rules = {
  name: [{ required: true, message: '请输入链接名称', trigger: 'blur' }],
  url: [{ required: true, message: '请输入链接地址', trigger: 'blur' }]
}

const formRef = ref()

const fetchFriendLinks = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/friend-links')
    friendLinks.value = res.data || res
  } catch (error) {
    console.error('获取友情链接失败:', error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '添加友情链接'
  linkForm.value = {
    id: null,
    name: '',
    url: '',
    logo: '',
    description: '',
    sortOrder: 0,
    isActive: true
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑友情链接'
  linkForm.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这个友情链接吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/admin/friend-links/${row.id}`)
      ElMessage.success('删除成功')
      fetchFriendLinks()
    } catch (error) {
      console.error('删除失败:', error)
    }
  })
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (linkForm.value.id) {
          await request.put(`/admin/friend-links/${linkForm.value.id}`, linkForm.value)
          ElMessage.success('更新成功')
        } else {
          await request.post('/admin/friend-links', linkForm.value)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        fetchFriendLinks()
      } catch (error) {
        console.error('保存失败:', error)
      }
    }
  })
}

const handleSortOrderChange = async (row) => {
  try {
    await request.put(`/admin/friend-links/${row.id}`, row)
    ElMessage.success('排序更新成功')
    fetchFriendLinks()
  } catch (error) {
    console.error('更新排序失败:', error)
  }
}

onMounted(() => {
  fetchFriendLinks()
})
</script>

<template>
  <div class="friend-link-manage">
    <div class="page-header">
      <h2>友情链接管理</h2>
      <el-button type="primary" @click="handleAdd">添加链接</el-button>
    </div>

    <el-card class="table-card">
      <el-table :data="friendLinks" v-loading="loading" stripe>
        <el-table-column prop="name" label="名称" width="200" />
        <el-table-column prop="url" label="地址" min-width="250" show-overflow-tooltip />
        <el-table-column label="Logo" width="80">
          <template #default="{ row }">
            <el-image
              v-if="row.logo"
              :src="row.logo"
              style="width: 40px; height: 40px; border-radius: 4px"
              fit="cover"
            />
            <span v-else class="text-placeholder">无</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="100">
          <template #default="{ row }">
            <el-input-number
              v-model="row.sortOrder"
              :min="0"
              size="small"
              @change="handleSortOrderChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="isActive" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isActive ? 'success' : 'info'">
              {{ row.isActive ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="formRef?.resetFields()"
    >
      <el-form
        ref="formRef"
        :model="linkForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="链接名称" prop="name">
          <el-input v-model="linkForm.name" placeholder="请输入链接名称" />
        </el-form-item>
        <el-form-item label="链接地址" prop="url">
          <el-input v-model="linkForm.url" placeholder="请输入链接地址，如：https://example.com" />
        </el-form-item>
        <el-form-item label="Logo URL">
          <el-input v-model="linkForm.logo" placeholder="请输入Logo图片URL（可选）" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="linkForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入链接描述（可选）"
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="linkForm.sortOrder" :min="0" />
          <span class="form-tip">数字越小越靠前</span>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="linkForm.isActive" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.friend-link-manage {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h2 {
      font-size: 24px;
      font-weight: 600;
      margin: 0;
      color: #333;
    }
  }

  .table-card {
    .text-placeholder {
      color: #999;
      font-size: 14px;
    }

    .form-tip {
      margin-left: 10px;
      font-size: 12px;
      color: #999;
    }
  }
}
</style>
