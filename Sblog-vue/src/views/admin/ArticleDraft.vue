<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const drafts = ref([])
const total = ref(0)

const queryParams = ref({
  page: 1,
  pageSize: 10
})

const fetchDrafts = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/articles', {
      params: { ...queryParams.value, status: 'draft' }
    })
    const data = res.data || res
    drafts.value = data.list || []
    total.value = data.total || 0
  } catch (error) {
    console.error('获取草稿列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleEdit = (id) => {
  router.push(`/admin/article-edit/${id}`)
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这篇草稿吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.delete(`/admin/articles/${id}`)
    ElMessage.success('删除成功')
    fetchDrafts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

onMounted(() => {
  fetchDrafts()
})

watch(() => [queryParams.value.page, queryParams.value.pageSize], () => {
  fetchDrafts()
})
</script>

<template>
  <div class="article-draft">
    <div class="page-header">
      <h2>草稿箱</h2>
      <el-button type="primary" @click="router.push('/admin/article-create')">
        <el-icon><Plus /></el-icon>
        新建草稿
      </el-button>
    </div>

    <el-card class="table-card">
      <el-table :data="drafts" v-loading="loading" style="width: 100%">
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="createdAt" label="创建时间" width="110">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" width="110">
          <template #default="{ row }">
            {{ formatDate(row.updatedAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row.id)">
              编辑
            </el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && drafts.length === 0" description="暂无草稿" />

      <el-pagination
        v-if="drafts.length > 0"
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        class="center-pagination"
      />
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.article-draft {
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
    min-height: 400px;

    .center-pagination {
      display: flex;
      justify-content: center;
      margin-top: 20px;
    }
  }
}
</style>
