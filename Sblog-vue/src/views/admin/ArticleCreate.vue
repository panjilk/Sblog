<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import request from '@/utils/axios'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const saveLoading = ref(false)
const isEdit = computed(() => !!route.params.id)

const articleForm = ref({
  title: '',
  content: '',
  summary: '',
  cover: '',
  categoryId: null,
  tagIds: [],
  status: 'draft',
  allowComment: true,
  views: 0
})

const categories = ref([])
const allTags = ref([])
const formRef = ref()
const originalViews = ref(0)

// 配置编辑器主题
const editorTheme = ref('light')

const rules = {
  title: [{ required: true, message: '请输入文章标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入文章内容', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

// 图片上传处理
const handleUploadImg = async (files, callback) => {
  const res = await Promise.all(
    files.map((file) => {
      return new Promise((rev, rej) => {
        const formData = new FormData()
        formData.append('file', file)

        request.post('/admin/upload/image', formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        }).then((res) => {
          const data = res.data || res
          rev({
            url: data.url,
            alt: file.name,
            title: file.name
          })
        }).catch((error) => {
          rej(error)
        })
      })
    })
  )

  callback(res)
}

// 编辑器工具栏扩展
const toolbars = [
  'bold',
  'underline',
  'italic',
  'strikeThrough',
  '-',
  'title',
  'sub',
  'sup',
  'quote',
  'unorderedList',
  'orderedList',
  'task',
  '-',
  'codeRow',
  'code',
  'link',
  'image',
  'table',
  '-',
  'revoke',
  'next',
  'save',
  '=',
  'pageFullscreen',
  'fullscreen',
  'preview',
  'htmlPreview',
  'catalog'
]

const fetchCategories = async () => {
  try {
    const res = await request.get('/admin/categories')
    categories.value = res.data || res
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const fetchTags = async () => {
  try {
    const res = await request.get('/admin/tags')
    allTags.value = res.data || res
  } catch (error) {
    console.error('获取标签失败:', error)
  }
}

const handleSaveDraft = async () => {
  articleForm.value.status = 'draft'
  await handleSubmit()
}

const handlePublish = async () => {
  articleForm.value.status = 'published'
  await handleSubmit()
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      saveLoading.value = true
      try {
        if (isEdit.value) {
          await request.put(`/admin/articles/${route.params.id}`, articleForm.value)
          // 如果浏览量有变化，更新浏览量
          if (articleForm.value.views !== originalViews.value) {
            await request.put(`/admin/articles/${route.params.id}/views`, {
              views: articleForm.value.views
            })
          }
          ElMessage.success(articleForm.value.status === 'draft' ? '保存草稿成功' : '更新成功')
        } else {
          await request.post('/admin/articles', articleForm.value)
          ElMessage.success(articleForm.value.status === 'draft' ? '保存草稿成功' : '发布成功')
        }
        router.push('/admin/article-list')
      } catch (error) {
        console.error('保存失败:', error)
        ElMessage.error('保存失败: ' + (error.message || '未知错误'))
      } finally {
        saveLoading.value = false
      }
    }
  })
}

const fetchArticle = async () => {
  if (!route.params.id) return
  loading.value = true
  try {
    const res = await request.get(`/admin/articles/${route.params.id}`)
    const data = res.data || res
    articleForm.value = {
      title: data.title || '',
      content: data.content || '',
      summary: data.summary || '',
      cover: data.cover || '',
      categoryId: data.categoryId || null,
      tagIds: data.tagIds || [],
      status: data.status || 'draft',
      allowComment: data.allowComment ?? true,
      views: data.views || 0
    }
    originalViews.value = data.views || 0
  } catch (error) {
    console.error('获取文章详情失败:', error)
    ElMessage.error('获取文章详情失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchCategories()
  fetchTags()
  if (isEdit.value) {
    fetchArticle()
  }
})
</script>

<template>
  <div class="article-create">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑文章' : '写文章' }}</h2>
      <div class="header-actions">
        <el-button @click="router.push('/admin/article-list')">取消</el-button>
        <el-button type="info" :loading="saveLoading" @click="handleSaveDraft">
          保存草稿
        </el-button>
        <el-button type="primary" :loading="saveLoading" @click="handlePublish">
          发布
        </el-button>
      </div>
    </div>

    <el-card class="form-card" v-loading="loading">
      <el-form ref="formRef" :model="articleForm" :rules="rules" label-width="100px">
        <el-form-item label="文章标题" prop="title">
          <el-input
            v-model="articleForm.title"
            placeholder="请输入文章标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="文章分类" prop="categoryId">
          <el-select v-model="articleForm.categoryId" placeholder="请选择分类">
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="文章标签">
          <el-select v-model="articleForm.tagIds" multiple placeholder="请选择标签">
            <el-option
              v-for="tag in allTags"
              :key="tag.id"
              :label="tag.name"
              :value="tag.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="文章摘要">
          <el-input
            v-model="articleForm.summary"
            type="textarea"
            :rows="3"
            placeholder="请输入文章摘要"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="封面图片">
          <el-input v-model="articleForm.cover" placeholder="请输入封面图片URL" />
        </el-form-item>

        <el-form-item label="文章内容" prop="content">
          <MdEditor
            v-model="articleForm.content"
            :theme="editorTheme"
            :toolbars="toolbars"
            @onUploadImg="handleUploadImg"
            placeholder="请输入文章内容，支持 Markdown..."
            language="zh-CN"
          />
        </el-form-item>

        <el-form-item label="允许评论">
          <el-switch v-model="articleForm.allowComment" />
        </el-form-item>

        <el-form-item label="浏览量" v-if="isEdit">
          <el-input-number
            v-model="articleForm.views"
            :min="0"
            :step="1"
            placeholder="请输入浏览量"
            style="width: 200px"
          />
          <span style="margin-left: 10px; color: #999; font-size: 12px">
            修改浏览量会影响文章的统计数据
          </span>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.article-create {
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

    .header-actions {
      display: flex;
      gap: 10px;
    }
  }

  .form-card {
    max-width: 1000px;
  }

  :deep(.md-editor) {
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }
}
</style>
