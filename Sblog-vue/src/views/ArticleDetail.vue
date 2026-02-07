<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/utils/axios'
import { ElMessage } from 'element-plus'
import { marked } from 'marked'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const article = ref(null)
const comments = ref([])
const isVisible = ref(false)
const contentLoaded = ref(false)

const commentForm = ref({
  content: '',
  author: '',
  email: ''
})

// 配置 marked 选项
marked.setOptions({
  breaks: true,  // 支持 GitHub 风格的换行（单个换行符视为 <br>）
  gfm: true,     // 启用 GitHub 风格的 Markdown
  headerIds: true,
  mangle: false
})

// 将 Markdown 转换为 HTML
const renderedContent = computed(() => {
  if (!article.value?.content) return ''
  try {
    return marked(article.value.content)
  } catch (error) {
    console.error('Markdown 解析失败:', error)
    return article.value.content
  }
})

// 获取文章详情
const fetchArticle = async () => {
  loading.value = true
  try {
    const res = await request.get(`/articles/${route.params.id}`)
    article.value = res.data || res
  } catch (error) {
    console.error('获取文章详情失败:', error)
    ElMessage.error('获取文章详情失败')
  } finally {
    loading.value = false
  }
}

// 获取文章评论
const fetchComments = async () => {
  try {
    const articleId = route.params.id
    console.log('正在获取文章评论，文章ID:', articleId)
    const res = await request.get(`/articles/${articleId}/comments`)
    console.log('评论接口完整响应:', JSON.stringify(res))

    // 处理响应数据：{ code: 200, data: { list: [], total: 0 } }
    // axios拦截器已返回res.data，所以res就是 { code: 200, data: {...} }
    let commentList = []
    if (res?.data?.list && Array.isArray(res.data.list)) {
      commentList = res.data.list
      console.log(`成功获取 ${commentList.length} 条评论`)
    } else if (res?.data && Array.isArray(res.data)) {
      commentList = res.data
      console.log(`成功获取 ${commentList.length} 条评论（直接数组）`)
    } else if (Array.isArray(res)) {
      commentList = res
      console.log(`成功获取 ${commentList.length} 条评论（res直接是数组）`)
    } else {
      console.warn('未找到评论列表，响应格式:', res)
    }

    comments.value = commentList
  } catch (error) {
    console.error('获取评论失败:', error)
    comments.value = []
  }
}

// 提交评论
const submitComment = async () => {
  if (!commentForm.value.content.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  if (!commentForm.value.author.trim()) {
    ElMessage.warning('请输入您的昵称')
    return
  }

  try {
    await request.post(`/articles/${route.params.id}/comments`, {
      author: commentForm.value.author,
      content: commentForm.value.content,
      email: commentForm.value.email || ''
    })
    // 创建新评论对象，直接添加到评论列表顶部
    const newComment = {
      id: Date.now(),
      author: commentForm.value.author,
      content: commentForm.value.content,
      email: commentForm.value.email || '',
      createdAt: new Date().toISOString()
    }
    comments.value.unshift(newComment)
    ElMessage.success('评论发表成功！')
    commentForm.value = { content: '', author: '', email: '' }
  } catch (error) {
    console.error('提交评论失败:', error)
    ElMessage.error('提交评论失败')
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  fetchArticle()
  fetchComments()

  // 触发进入动画
  setTimeout(() => {
    isVisible.value = true
  }, 100)

  // 内容延迟动画
  setTimeout(() => {
    contentLoaded.value = true
  }, 400)
})
</script>

<template>
  <div v-if="loading" class="loading">
    <div class="spinner"></div>
    <p>加载中...</p>
  </div>

  <div v-else-if="article" class="article-detail" :class="{ 'is-visible': isVisible, 'content-loaded': contentLoaded }">
    <!-- 文章头部 -->
    <header class="article-header">
      <h1 class="article-title">{{ article.title }}</h1>
      <div class="article-meta">
        <span v-if="article.categoryName" class="category">📁 {{ article.categoryName }}</span>
        <span class="date">📅 {{ formatDate(article.createdAt) }}</span>
        <span class="views">👁️ {{ article.views || 0 }} 次阅读</span>
      </div>
      <div v-if="article.tags && article.tags.length > 0" class="article-tags">
        <span v-for="tag in article.tags" :key="tag" class="tag">{{ tag }}</span>
      </div>
    </header>

    <!-- 文章封面 -->
    <div v-if="article.cover" class="article-cover" :style="{ backgroundImage: `url(${article.cover})` }">
      <div class="cover-overlay"></div>
    </div>

    <!-- 文章内容 -->
    <div class="article-content markdown-body" v-html="renderedContent"></div>

    <!-- 评论区 -->
    <div class="comments-section">
      <h2 class="comments-title">💬 评论 ({{ comments.length }})</h2>

      <!-- 评论表单 -->
      <div class="comment-form">
        <h3>✍️ 发表评论</h3>
        <div class="form-group">
          <input
            v-model="commentForm.author"
            type="text"
            placeholder="您的昵称 *"
          />
          <input
            v-model="commentForm.email"
            type="email"
            placeholder="您的邮箱 (选填)"
          />
        </div>
        <textarea
          v-model="commentForm.content"
          placeholder="说点什么吧..."
          rows="4"
        ></textarea>
        <button @click="submitComment" class="submit-btn">🚀 提交评论</button>
      </div>

      <!-- 评论列表 -->
      <div v-if="comments.length > 0" class="comment-list">
        <div v-for="(comment, index) in comments" :key="comment.id" class="comment-item" :style="{ animationDelay: `${index * 0.1}s` }">
          <div class="comment-avatar">
            {{ comment.author ? comment.author.charAt(0).toUpperCase() : '?' }}
          </div>
          <div class="comment-content">
            <div class="comment-header">
              <span class="comment-author">{{ comment.author || '匿名' }}</span>
              <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
            </div>
            <p class="comment-text">{{ comment.content }}</p>
          </div>
        </div>
      </div>
      <div v-else class="no-comments">
        <div class="empty-icon">💭</div>
        <p>暂无评论，快来抢沙发吧~</p>
      </div>
    </div>
  </div>

  <div v-else class="error">
    <div class="error-icon">😕</div>
    <p>文章不存在</p>
    <button @click="router.push('/index')" class="back-btn">🏠 返回首页</button>
  </div>
</template>

<style scoped lang="scss">
.loading {
  text-align: center;
  padding: 80px 20px;
  color: #999;

  .spinner {
    width: 50px;
    height: 50px;
    margin: 0 auto 20px;
    border: 4px solid #f3f3f3;
    border-top: 4px solid #667eea;
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }
}

.article-detail {
  max-width: 800px;
  margin: 0 auto;
  opacity: 0;
  transform: translateY(30px);
  transition: opacity 0.6s ease, transform 0.6s ease;

  &.is-visible {
    opacity: 1;
    transform: translateY(0);
  }

  .article-header {
    margin-bottom: 35px;
    opacity: 0;
    animation: fadeInDown 0.8s ease forwards;

    .article-title {
      font-size: 36px;
      font-weight: 700;
      margin: 0 0 20px 0;
      color: #333;
      line-height: 1.4;
      background: linear-gradient(135deg, #667eea, #764ba2);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }

    .article-meta {
      display: flex;
      align-items: center;
      gap: 15px;
      font-size: 14px;
      color: #999;
      margin-bottom: 20px;
      flex-wrap: wrap;

      .category {
        padding: 6px 16px;
        background: linear-gradient(135deg, #667eea, #764ba2);
        color: #fff;
        border-radius: 20px;
        font-weight: 500;
      }

      .date,
      .views {
        display: flex;
        align-items: center;
        gap: 5px;
      }
    }

    .article-tags {
      display: flex;
      gap: 10px;
      flex-wrap: wrap;

      .tag {
        padding: 6px 16px;
        background: #f5f5f5;
        border-radius: 20px;
        font-size: 14px;
        color: #666;
        transition: all 0.3s ease;

        &:hover {
          background: linear-gradient(135deg, #667eea, #764ba2);
          color: #fff;
          transform: translateY(-2px);
        }
      }
    }
  }

  .article-cover {
    height: 400px;
    border-radius: 16px;
    margin-bottom: 40px;
    background-size: cover;
    background-position: center;
    position: relative;
    overflow: hidden;
    opacity: 0;
    animation: fadeInScale 0.8s ease 0.2s forwards;
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);

    .cover-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
      opacity: 0;
      transition: opacity 0.5s ease;
    }

    &:hover .cover-overlay {
      opacity: 1;
    }
  }

  .article-content {
    font-size: 17px;
    line-height: 1.9;
    color: #333;
    margin-bottom: 60px;
    opacity: 0;
    animation: fadeIn 0.8s ease 0.4s forwards;

    :deep(h2),
    :deep(h3) {
      margin-top: 40px;
      margin-bottom: 20px;
      color: #333;
      position: relative;
      padding-bottom: 10px;

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 0;
        width: 60px;
        height: 3px;
        background: linear-gradient(135deg, #667eea, #764ba2);
        border-radius: 2px;
      }
    }

    :deep(h2) {
      font-size: 28px;
    }

    :deep(h3) {
      font-size: 24px;
    }

    :deep(p) {
      margin-bottom: 18px;
    }

    :deep(code) {
      background: #f5f5f5;
      padding: 3px 8px;
      border-radius: 6px;
      font-family: 'Courier New', monospace;
      color: #e83e8c;
    }

    :deep(pre) {
      background: #2d2d2d;
      color: #f8f8f2;
      padding: 20px;
      border-radius: 12px;
      overflow-x: auto;
      margin: 25px 0;
      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);

      code {
        background: transparent;
        color: inherit;
        padding: 0;
      }
    }

    :deep(img) {
      max-width: 100%;
      border-radius: 12px;
      margin: 25px 0;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
      transition: transform 0.3s ease;

      &:hover {
        transform: scale(1.02);
      }
    }

    :deep(blockquote) {
      border-left: 4px solid #667eea;
      padding-left: 20px;
      margin: 25px 0;
      color: #666;
      font-style: italic;
      background: #f9f9ff;
      padding: 20px;
      border-radius: 8px;
    }

    :deep(ul),
    :deep(ol) {
      padding-left: 25px;
      margin-bottom: 18px;

      li {
        margin-bottom: 8px;
      }
    }

    :deep(a) {
      color: #667eea;
      text-decoration: none;
      border-bottom: 1px solid transparent;
      transition: border-color 0.3s ease;

      &:hover {
        border-bottom-color: #667eea;
      }
    }

    // Markdown 标题样式
    :deep(h1) {
      font-size: 32px;
      font-weight: 700;
      margin: 40px 0 20px 0;
      padding-bottom: 12px;
      border-bottom: 3px solid #667eea;
      color: #333;
    }

    :deep(h4) {
      font-size: 20px;
      font-weight: 600;
      margin-top: 30px;
      margin-bottom: 15px;
      color: #333;
    }

    :deep(h5) {
      font-size: 18px;
      font-weight: 600;
      margin-top: 25px;
      margin-bottom: 12px;
      color: #333;
    }

    :deep(h6) {
      font-size: 16px;
      font-weight: 600;
      margin-top: 20px;
      margin-bottom: 10px;
      color: #666;
    }

    // Markdown 表格样式
    :deep(table) {
      width: 100%;
      border-collapse: collapse;
      margin: 25px 0;
      font-size: 15px;
      overflow: hidden;
      border-radius: 8px;
      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);

      thead {
        background: linear-gradient(135deg, #667eea, #764ba2);
        color: #fff;

        th {
          padding: 15px;
          text-align: left;
          font-weight: 600;
        }
      }

      tbody {
        tr {
          border-bottom: 1px solid #e0e0e0;
          transition: background 0.3s ease;

          &:last-child {
            border-bottom: none;
          }

          &:hover {
            background: #f8f9ff;
          }

          td {
            padding: 15px;
          }
        }
      }
    }

    // Markdown 水平线
    :deep(hr) {
      border: none;
      height: 2px;
      background: linear-gradient(90deg, transparent, #667eea, transparent);
      margin: 40px 0;
    }

    // Markdown 强调和斜体
    :deep(strong) {
      color: #333;
      font-weight: 600;
    }

    :deep(em) {
      color: #555;
      font-style: italic;
    }
  }

  .comments-section {
    border-top: 2px solid #e0e0e0;
    padding-top: 50px;
    opacity: 0;
    animation: fadeIn 0.8s ease 0.6s forwards;

    .comments-title {
      font-size: 26px;
      font-weight: 600;
      margin-bottom: 35px;
      color: #333;
    }

    .comment-form {
      background: linear-gradient(135deg, #f9f9ff, #f5f5ff);
      border-radius: 16px;
      padding: 25px;
      margin-bottom: 35px;
      box-shadow: 0 4px 15px rgba(102, 126, 234, 0.1);
      transition: transform 0.3s ease, box-shadow 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.15);
      }

      h3 {
        font-size: 18px;
        margin: 0 0 20px 0;
        color: #333;
      }

      .form-group {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 15px;
        margin-bottom: 15px;

        input {
          padding: 12px 18px;
          border: 2px solid #e0e0e0;
          border-radius: 10px;
          font-size: 14px;
          outline: none;
          transition: border-color 0.3s ease, box-shadow 0.3s ease;

          &:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
          }
        }
      }

      textarea {
        width: 100%;
        padding: 12px 18px;
        border: 2px solid #e0e0e0;
        border-radius: 10px;
        font-size: 14px;
        resize: vertical;
        outline: none;
        font-family: inherit;
        margin-bottom: 18px;
        transition: border-color 0.3s ease, box-shadow 0.3s ease;

        &:focus {
          border-color: #667eea;
          box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
        }
      }

      .submit-btn {
        padding: 12px 35px;
        background: linear-gradient(135deg, #667eea, #764ba2);
        color: #fff;
        border: none;
        border-radius: 10px;
        font-size: 15px;
        cursor: pointer;
        transition: transform 0.3s ease, box-shadow 0.3s ease;
        font-weight: 500;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
        }

        &:active {
          transform: translateY(0);
        }
      }
    }

    .comment-list {
      .comment-item {
        display: flex;
        gap: 18px;
        padding: 25px 0;
        border-bottom: 1px solid #e0e0e0;
        opacity: 0;
        animation: slideInLeft 0.5s ease forwards;

        &:last-child {
          border-bottom: none;
        }

        &:hover {
          background: #fafafa;
          margin: 0 -15px;
          padding-left: 15px;
          padding-right: 15px;
          border-radius: 10px;
        }

        .comment-avatar {
          width: 45px;
          height: 45px;
          border-radius: 50%;
          background: linear-gradient(135deg, #667eea, #764ba2);
          color: #fff;
          display: flex;
          align-items: center;
          justify-content: center;
          font-weight: 600;
          flex-shrink: 0;
          box-shadow: 0 4px 10px rgba(102, 126, 234, 0.3);
        }

        .comment-content {
          flex: 1;

          .comment-header {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;

            .comment-author {
              font-weight: 600;
              color: #333;
            }

            .comment-date {
              font-size: 13px;
              color: #999;
            }
          }

          .comment-text {
            margin: 0;
            color: #666;
            line-height: 1.7;
          }
        }
      }
    }

    .no-comments {
      text-align: center;
      padding: 50px 20px;
      color: #999;

      .empty-icon {
        font-size: 48px;
        margin-bottom: 15px;
      }
    }
  }
}

.error {
  text-align: center;
  padding: 80px 20px;

  .error-icon {
    font-size: 64px;
    margin-bottom: 20px;
  }

  p {
    color: #999;
    margin-bottom: 25px;
    font-size: 18px;
  }

  .back-btn {
    padding: 12px 35px;
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: #fff;
    border: none;
    border-radius: 10px;
    cursor: pointer;
    font-size: 15px;
    transition: transform 0.3s ease, box-shadow 0.3s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
    }
  }
}

@media (max-width: 768px) {
  .article-detail {
    .article-header {
      .article-title {
        font-size: 26px;
      }

      .article-meta {
        flex-wrap: wrap;
      }
    }

    .article-cover {
      height: 220px;
    }

    .article-content {
      font-size: 16px;

      :deep(h2) {
        font-size: 24px;
      }

      :deep(h3) {
        font-size: 20px;
      }
    }

    .comments-section {
      .comment-form {
        .form-group {
          grid-template-columns: 1fr;
        }
      }
    }
  }
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInScale {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}
</style>
