<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/axios'

const loading = ref(false)
const comments = ref([])
const total = ref(0)
const replyDialogVisible = ref(false)
const replyForm = ref({
  id: null,
  content: ''
})

const queryParams = ref({
  page: 1,
  pageSize: 10,
  status: ''
})

const fetchComments = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/comments', { params: queryParams.value })
    const data = res.data || res
    comments.value = data.list || []
    total.value = data.total || 0
  } catch (error) {
    console.error('获取评论列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleReply = (row) => {
  replyForm.value = { id: row.id, content: '' }
  replyDialogVisible.value = true
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.delete(`/admin/comments/${id}`)
    ElMessage.success('删除成功')
    fetchComments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const handleSubmitReply = async () => {
  if (!replyForm.value.content.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  try {
    await request.post(`/admin/comments/${replyForm.value.id}/reply`, {
      content: replyForm.value.content
    })
    ElMessage.success('回复成功')
    replyDialogVisible.value = false
    fetchComments()
  } catch (error) {
    console.error('回复失败:', error)
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  if (isNaN(date.getTime())) return '-'
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  fetchComments()
})

watch(() => [queryParams.value.page, queryParams.value.pageSize], () => {
  fetchComments()
})
</script>

<template>
  <div class="comment">
    <div class="page-header">
      <h2>评论管理</h2>
    </div>

    <el-card class="table-card">
      <el-table :data="comments" v-loading="loading" style="width: 100%">
        <el-table-column prop="content" label="评论内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="author" label="评论者" width="120" />
        <el-table-column prop="articleTitle" label="文章" width="150" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleReply(row)">
              回复
            </el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        class="center-pagination"
      />
    </el-card>

    <el-dialog v-model="replyDialogVisible" title="回复评论" width="500px">
      <el-input
        v-model="replyForm.content"
        type="textarea"
        :rows="4"
        placeholder="请输入回复内容"
      />
      <template #footer>
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitReply">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.comment {
  .page-header {
    margin-bottom: 20px;

    h2 {
      font-size: 24px;
      font-weight: 600;
      margin: 0;
      color: #333;
    }
  }

  .table-card {
    .center-pagination {
      display: flex;
      justify-content: center;
      margin-top: 20px;
    }
  }
}
</style>
