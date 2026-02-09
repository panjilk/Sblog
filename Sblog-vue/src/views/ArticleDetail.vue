<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/utils/axios'
import { ElMessage } from 'element-plus'
import { MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/preview.css'
import { useTheme } from '@/composables/useTheme'

const route = useRoute()
const router = useRouter()
const { isDark } = useTheme()

// 计算属性来提供正确的主题值给 md-editor-v3
const mdTheme = computed(() => isDark.value ? 'dark' : 'light')

const loading = ref(false)
const article = ref(null)
const comments = ref([])
const isVisible = ref(false)
const contentLoaded = ref(false)
const activeHeading = ref('')

// 目录相关
const toc = ref([])
const tocVisible = ref(true)
const contentRef = ref(null)

const commentForm = ref({
  content: '',
  author: '',
  email: ''
})

// 生成目录
const generateToc = () => {
  nextTick(() => {
    // 尝试多种选择器来找到md-editor-v3的内容元素
    let contentElement = null

    // 方法1: 通过ref获取（md-editor-v3的ref指向组件实例）
    if (contentRef.value) {
      // md-editor-v3的预览组件有一个内部元素
      contentElement = contentRef.value.$el?.querySelector('.md-preview') ||
                       contentRef.value.$el ||
                       document.querySelector('.md-preview')
    }

    // 方法2: 直接使用querySelector
    if (!contentElement) {
      contentElement = document.querySelector('.md-preview')
    }

    // 方法3: 尝试通过class查找article-content
    if (!contentElement) {
      contentElement = document.querySelector('.article-content')
    }

    console.log('Content element:', contentElement)

    if (!contentElement) {
      console.log('No content element found, retrying...')
      // 重试一次，等待更长时间让md-editor-v3完成渲染
      setTimeout(generateToc, 500)
      return
    }

    // 在整个内容区域内查找标题
    const headings = contentElement.querySelectorAll('h1, h2, h3, h4, h5, h6')
    console.log('Found headings:', headings.length)

    if (headings.length === 0) {
      console.log('No headings found, retrying...')
      setTimeout(generateToc, 500)
      return
    }

    const tocList = []

    headings.forEach((heading, index) => {
      const id = `heading-${index}`
      heading.id = id

      const level = parseInt(heading.tagName.substring(1))
      const text = heading.textContent.trim()

      tocList.push({
        id,
        text,
        level,
        element: heading
      })
    })

    toc.value = tocList
    tocVisible.value = true
    console.log('Generated TOC:', tocList)
    console.log('TOC visible:', tocVisible.value)
  })
}

// 滚动到指定标题
const scrollToHeading = (id) => {
  const element = document.getElementById(id)
  if (element) {
    const offset = 80 // 顶部导航栏高度
    const elementPosition = element.getBoundingClientRect().top + window.pageYOffset
    window.scrollTo({
      top: elementPosition - offset,
      behavior: 'smooth'
    })
  }
}

// 监听滚动，高亮当前目录项
const handleScroll = () => {
  const headings = toc.value.map(item => item.element)
  const scrollTop = window.pageYOffset + 100

  for (let i = headings.length - 1; i >= 0; i--) {
    const heading = headings[i]
    if (heading) {
      const offsetTop = heading.getBoundingClientRect().top + window.pageYOffset
      if (offsetTop <= scrollTop) {
        activeHeading.value = toc.value[i].id
        return
      }
    }
  }
  activeHeading.value = ''
}

// 获取文章详情
const fetchArticle = async () => {
  loading.value = true
  try {
    const res = await request.get(`/articles/${route.params.id}`)
    article.value = res.data || res

    // 等待渲染完成后生成目录
    await nextTick()
    // 使用更长的延迟确保 Markdown 渲染完成
    setTimeout(generateToc, 500)
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
    const res = await request.get(`/articles/${articleId}/comments`)

    let commentList = []
    if (res?.data?.list && Array.isArray(res.data.list)) {
      commentList = res.data.list
    } else if (res?.data && Array.isArray(res.data)) {
      commentList = res.data
    } else if (Array.isArray(res)) {
      commentList = res
    }

    comments.value = commentList
  } catch (error) {
    console.error('获取评论失败:', error)
    comments.value = []
  }
}

// 提交评论
const submitComment = async () => {
  // 检查文章是否允许评论
  if (article.value && article.value.allowComment === false) {
    ElMessage.warning('该文章已关闭评论功能')
    return
  }

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

// 复制代码功能
const copyCode = async (code, button) => {
  try {
    await navigator.clipboard.writeText(code)
    ElMessage.success('代码已复制到剪贴板')

    // 更新按钮状态
    const originalHTML = button.innerHTML
    button.innerHTML = '<i class="icon">✓</i>'
    button.classList.add('copied')

    setTimeout(() => {
      button.innerHTML = originalHTML
      button.classList.remove('copied')
    }, 2000)
  } catch (err) {
    console.error('复制失败:', err)
    ElMessage.error('复制失败，请手动复制')
  }
}

// 为所有代码块添加复制按钮
const addCopyButtons = (retryCount = 0) => {
  nextTick(() => {
    // 尝试多种选择器
    let codeBlocks = document.querySelectorAll('.md-preview pre')

    // 如果没找到，尝试其他选择器
    if (codeBlocks.length === 0) {
      codeBlocks = document.querySelectorAll('.article-content pre')
    }

    // 如果还是没找到，尝试找到所有包含 code 的 pre
    if (codeBlocks.length === 0) {
      const allPres = document.querySelectorAll('pre')
      codeBlocks = Array.from(allPres).filter(pre => pre.querySelector('code'))
    }

    console.log('Found code blocks:', codeBlocks.length, 'Retry:', retryCount)

    if (codeBlocks.length === 0 && retryCount < 10) {
      // 如果没有找到代码块，等待更长时间后重试
      setTimeout(() => addCopyButtons(retryCount + 1), 300)
      return
    }

    let buttonsAdded = 0
    codeBlocks.forEach((block) => {
      // 检查是否已经添加过复制按钮
      if (block.querySelector('.copy-button')) {
        buttonsAdded++
        return
      }

      const codeElement = block.querySelector('code')
      if (!codeElement) return

      const code = codeElement.textContent

      // 创建复制按钮
      const copyButton = document.createElement('button')
      copyButton.className = 'copy-button'
      copyButton.innerHTML = '<i class="icon">📋</i> 复制'
      copyButton.setAttribute('aria-label', '复制代码')
      copyButton.type = 'button'
      copyButton.style.cssText = `
        position: absolute;
        top: 10px;
        right: 10px;
        background: rgba(255, 255, 255, 0.1);
        border: 1px solid rgba(255, 255, 255, 0.2);
        color: #fff;
        padding: 6px 14px;
        border-radius: 8px;
        font-size: 13px;
        cursor: pointer;
        display: flex;
        align-items: center;
        gap: 5px;
        transition: all 0.3s ease;
        backdrop-filter: blur(10px);
        z-index: 10;
      `

      // 绑定点击事件
      copyButton.addEventListener('click', () => {
        copyCode(code, copyButton)
      })

      // 将按钮添加到代码块
      block.style.position = 'relative'
      block.appendChild(copyButton)
      buttonsAdded++
    })

    console.log('Copy buttons added:', buttonsAdded)
  })
}

onMounted(() => {
  fetchArticle()
  fetchComments()

  setTimeout(() => {
    isVisible.value = true
  }, 100)

  setTimeout(() => {
    contentLoaded.value = true
    // 为代码块添加复制按钮
    addCopyButtons()
  }, 400)

  window.addEventListener('scroll', handleScroll, { passive: true })
})

// 组件卸载时移除滚动监听
import { onUnmounted } from 'vue'
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<template>
  <div v-if="loading" class="loading">
    <div class="spinner"></div>
    <p>加载中...</p>
  </div>

  <div v-else-if="article" class="article-detail-wrapper">
    <div class="article-detail" :class="{ 'is-visible': isVisible, 'content-loaded': contentLoaded }">
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
      <div v-if="article.cover" class="article-cover">
        <img v-lazy="article.cover" :alt="article.title" class="article-cover-img" />
        <div class="cover-overlay"></div>
      </div>

      <!-- 文章内容 - 使用 md-editor-v3 的预览组件 -->
      <div class="article-content-wrapper">
        <MdPreview
          ref="contentRef"
          :modelValue="article.content || ''"
          language="zh-CN"
          :theme="mdTheme"
          class="article-content"
        />
      </div>

      <!-- 评论区 -->
      <div class="comments-section">
        <h2 class="comments-title">💬 评论 ({{ comments.length }})</h2>

        <!-- 文章已关闭评论提示 -->
        <div v-if="article.allowComment === false" class="comment-disabled">
          <div class="disabled-icon">🔒</div>
          <p>该文章已关闭评论功能</p>
        </div>

        <!-- 评论表单 -->
        <div v-else class="comment-form">
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

    <!-- 目录侧边栏 -->
    <div v-if="toc.length > 0" class="toc-sidebar" :class="{ 'toc-visible': tocVisible }">
      <div class="toc-header">
        <h3>📑 目录</h3>
        <button @click="tocVisible = !tocVisible" class="toc-toggle">
          {{ tocVisible ? '收起' : '展开' }}
        </button>
      </div>
      <div v-show="tocVisible" class="toc-list">
        <div
          v-for="item in toc"
          :key="item.id"
          :class="['toc-item', `toc-level-${item.level}`, { 'toc-active': activeHeading === item.id }]"
          @click="scrollToHeading(item.id)"
        >
          {{ item.text }}
        </div>
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
  background: #fff;
  min-height: 400px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  p {
    margin-top: 20px;
    font-size: 16px;
  }

  .spinner {
    width: 50px;
    height: 50px;
    margin: 0 auto;
    border: 4px solid #f3f3f3;
    border-top: 4px solid #667eea;
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }
}

.article-detail-wrapper {
  position: relative;
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  gap: 30px;
}

.article-detail {
  flex: 1;
  min-width: 0;
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

    .article-cover-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s ease;
    }

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

  .article-content-wrapper {
    margin-bottom: 60px;
    opacity: 0;
    animation: fadeIn 0.8s ease 0.4s forwards;
  }

  :deep(.md-preview) {
    font-size: 17px;
    line-height: 1.9;
    color: #333;

    h1, h2, h3, h4, h5, h6 {
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

    h1 {
      font-size: 32px;
      border-bottom: 3px solid #667eea;
      padding-bottom: 12px;
    }

    h2 {
      font-size: 28px;
    }

    h3 {
      font-size: 24px;
    }

    h4 {
      font-size: 20px;
    }

    h5 {
      font-size: 18px;
    }

    h6 {
      font-size: 16px;
    }

    p {
      margin-bottom: 18px;
    }

    code {
      background: #f5f5f5;
      padding: 3px 8px;
      border-radius: 6px;
      font-family: 'Courier New', monospace;
      color: #e83e8c;
    }

    pre {
      background: #2d2d2d;
      color: #f8f8f2;
      padding: 20px;
      border-radius: 12px;
      overflow-x: auto;
      margin: 25px 0;
      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
      position: relative;

      code {
        background: transparent;
        color: inherit;
        padding: 0;
      }

      // 复制按钮样式
      .copy-button {
        position: absolute;
        top: 10px;
        right: 10px;
        background: rgba(255, 255, 255, 0.1);
        border: 1px solid rgba(255, 255, 255, 0.2);
        color: #fff;
        padding: 6px 14px;
        border-radius: 8px;
        font-size: 13px;
        cursor: pointer;
        display: flex;
        align-items: center;
        gap: 5px;
        transition: all 0.3s ease;
        backdrop-filter: blur(10px);

        .icon {
          font-size: 14px;
        }

        &:hover {
          background: rgba(102, 126, 234, 0.8);
          border-color: rgba(102, 126, 234, 1);
          transform: translateY(-2px);
        }

        &:active {
          transform: translateY(0);
        }

        &.copied {
          background: rgba(16, 185, 129, 0.8);
          border-color: rgba(16, 185, 129, 1);
        }
      }
    }

    img {
      max-width: 100%;
      border-radius: 12px;
      margin: 25px 0;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
      transition: transform 0.3s ease;

      &:hover {
        transform: scale(1.02);
      }
    }

    blockquote {
      border-left: 4px solid #667eea;
      padding-left: 20px;
      margin: 25px 0;
      color: #666;
      font-style: italic;
      background: #f9f9ff;
      padding: 20px;
      border-radius: 8px;
    }

    ul, ol {
      padding-left: 25px;
      margin-bottom: 18px;

      li {
        margin-bottom: 8px;
      }
    }

    a {
      color: #667eea;
      text-decoration: none;
      border-bottom: 1px solid transparent;
      transition: border-color 0.3s ease;

      &:hover {
        border-bottom-color: #667eea;
      }
    }

    table {
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

    hr {
      border: none;
      height: 2px;
      background: linear-gradient(90deg, transparent, #667eea, transparent);
      margin: 40px 0;
    }

    strong {
      color: #333;
      font-weight: 600;
    }

    em {
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

    .comment-disabled {
      text-align: center;
      padding: 40px 20px;
      background: linear-gradient(135deg, #f9f9ff, #f5f5ff);
      border-radius: 16px;
      margin-bottom: 35px;
      box-shadow: 0 4px 15px rgba(102, 126, 234, 0.1);

      .disabled-icon {
        font-size: 48px;
        margin-bottom: 15px;
      }

      p {
        font-size: 16px;
        color: #999;
        margin: 0;
      }
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

// 目录侧边栏样式
.toc-sidebar {
  width: 250px;
  flex-shrink: 0;
  position: sticky;
  top: 100px;
  height: fit-content;
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;

  .toc-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
    padding-bottom: 12px;
    border-bottom: 2px solid #e0e0e0;

    h3 {
      font-size: 16px;
      font-weight: 600;
      margin: 0;
      color: #333;
    }

    .toc-toggle {
      background: none;
      border: none;
      color: #667eea;
      cursor: pointer;
      font-size: 13px;
      padding: 4px 8px;
      border-radius: 6px;
      transition: background 0.3s ease;

      &:hover {
        background: rgba(102, 126, 234, 0.1);
      }
    }
  }

  .toc-list {
    max-height: 500px;
    overflow-y: auto;

    .toc-item {
      padding: 8px 12px;
      margin-bottom: 4px;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s ease;
      font-size: 14px;
      color: #666;
      line-height: 1.5;

      &:hover {
        background: #f5f5f5;
        color: #667eea;
      }

      &.toc-active {
        background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
        color: #667eea;
        font-weight: 500;
      }

      &.toc-level-1 {
        padding-left: 12px;
        font-weight: 600;
      }

      &.toc-level-2 {
        padding-left: 24px;
      }

      &.toc-level-3 {
        padding-left: 36px;
        font-size: 13px;
      }

      &.toc-level-4 {
        padding-left: 48px;
        font-size: 13px;
      }

      &.toc-level-5 {
        padding-left: 60px;
        font-size: 12px;
      }

      &.toc-level-6 {
        padding-left: 72px;
        font-size: 12px;
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

@media (max-width: 1024px) {
  .article-detail-wrapper {
    flex-direction: column;

    .toc-sidebar {
      display: none;
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

    :deep(.md-preview) {
      font-size: 16px;

      h2 {
        font-size: 24px;
      }

      h3 {
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

// 深色模式样式
[data-theme='dark'] .loading {
  background: #1a1a1a;
  color: #b0b0b0;
}

[data-theme='dark'] .error {
  background: #1a1a1a;

  p {
    color: #b0b0b0;
  }
}

[data-theme='dark'] .article-detail-wrapper {
  background: #1a1a1a;
}

[data-theme='dark'] .article-detail {
  .article-title {
    background: linear-gradient(135deg, #a78bfa, #818cf8);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .article-meta {
    color: #999;
  }

  .tag {
    background: #2a2a3a;
    color: #e0e0e0;
  }
}

[data-theme='dark'] .md-preview {
  color: #e0e0e0;

  h1, h2, h3, h4, h5, h6 {
    color: #e0e0e0;
  }

  p {
    color: #b0b0b0;
  }

  code {
    background: #3a3a4a;
    color: #e83e8c;
  }

  pre {
    background: #1a1a2e;
    color: #f8f8f2;
    position: relative;

    .copy-button {
      background: rgba(255, 255, 255, 0.08);
      border-color: rgba(255, 255, 255, 0.15);

      &:hover {
        background: rgba(167, 139, 250, 0.7);
        border-color: rgba(167, 139, 250, 1);
      }

      &.copied {
        background: rgba(16, 185, 129, 0.7);
        border-color: rgba(16, 185, 129, 1);
      }
    }
  }

  blockquote {
    background: #2a2a3a;
    border-left-color: #a78bfa;
    color: #b0b0b0;
  }

  a {
    color: #a78bfa;
  }

  table thead {
    background: linear-gradient(135deg, #a78bfa, #818cf8);
  }

  table tbody tr {
    border-bottom-color: #3a3a4a;

    &:hover {
      background: #2a2a3a;
    }
  }

  strong {
    color: #e0e0e0;
  }

  em {
    color: #b0b0b0;
  }

  ul li, ol li {
    color: #b0b0b0;
  }
}

[data-theme='dark'] .comments-section {
  border-top-color: #3a3a4a;

  .comments-title {
    color: #e0e0e0;
  }

  .comment-disabled {
    background: linear-gradient(135deg, #2a2a3a, #252530);

    p {
      color: #999;
    }
  }

  .comment-form {
    background: linear-gradient(135deg, #2a2a3a, #252530);

    h3 {
      color: #e0e0e0;
    }

    input,
    textarea {
      background: #1a1a2e;
      border-color: #3a3a4a;
      color: #e0e0e0;

      &::placeholder {
        color: #999;
      }
    }
  }

  .comment-item {
    border-bottom-color: #3a3a4a;

    &:hover {
      background: #2a2a3a;
    }

    .comment-avatar {
      background: linear-gradient(135deg, #a78bfa, #818cf8);
    }

    .comment-author {
      color: #e0e0e0;
    }

    .comment-date {
      color: #999;
    }

    .comment-text {
      color: #b0b0b0;
    }
  }

  .no-comments {
    color: #999;
  }
}

[data-theme='dark'] .toc-sidebar {
  background: #252530;

  .toc-header {
    border-bottom-color: #3a3a4a;

    h3 {
      color: #e0e0e0;
    }
  }

  .toc-item {
    color: #b0b0b0;

    &:hover {
      background: #2a2a3a;
      color: #a78bfa;
    }

    &.toc-active {
      background: linear-gradient(135deg, rgba(167, 139, 250, 0.15), rgba(129, 140, 248, 0.15));
      color: #a78bfa;
    }
  }
}
</style>
