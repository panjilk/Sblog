<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/axios'

const loading = ref(false)
const tags = ref([])
const dialogVisible = ref(false)
const dialogMode = ref('create')
const formRef = ref()

const tagForm = ref({
  id: null,
  name: ''
})

const rules = {
  name: [{ required: true, message: '请输入标签名称', trigger: 'blur' }]
}

const fetchTags = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/tags')
    tags.value = res.data || res
  } catch (error) {
    console.error('获取标签列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleCreate = () => {
  dialogMode.value = 'create'
  tagForm.value = { id: null, name: '' }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogMode.value = 'edit'
  tagForm.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个标签吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.delete(`/admin/tags/${id}`)
    ElMessage.success('删除成功')
    fetchTags()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogMode.value === 'create') {
          await request.post('/admin/tags', tagForm.value)
          ElMessage.success('创建成功')
        } else {
          await request.put(`/admin/tags/${tagForm.value.id}`, tagForm.value)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        fetchTags()
      } catch (error) {
        console.error('保存失败:', error)
      }
    }
  })
}

onMounted(() => {
  fetchTags()
})
</script>

<template>
  <div class="tag">
    <div class="page-header">
      <h2>标签管理</h2>
      <el-button type="primary" @click="handleCreate">
        <el-icon><Plus /></el-icon>
        新建标签
      </el-button>
    </div>

    <el-card class="table-card">
      <el-table :data="tags" v-loading="loading" style="width: 100%">
        <el-table-column prop="name" label="标签名称" width="200" />
        <el-table-column prop="articleCount" label="文章数量" width="100" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'create' ? '新建标签' : '编辑标签'"
      width="400px"
    >
      <el-form ref="formRef" :model="tagForm" :rules="rules" label-width="100px">
        <el-form-item label="标签名称" prop="name">
          <el-input v-model="tagForm.name" placeholder="请输入标签名称" />
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
.tag {
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
}
</style>
