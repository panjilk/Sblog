<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/axios'

const router = useRouter()

const loading = ref(false)
const articles = ref([])
const total = ref(0)
const categories = ref([])
const tags = ref([])

const queryParams = ref({
  page: 1,
  pageSize: 10,
  categoryId: null,
  tagId: null,
  keyword: ''
})

const featuredArticles = ref([])
const isVisible = ref(false)
const loadedArticles = ref(false)
const loadedSidebar = ref(false)

// 获取文章列表
const fetchArticles = async () => {
  loading.value = true
  try {
    const res = await request.get('/articles', { params: queryParams.value })
    const data = res.data || res
    articles.value = data.list || []
    total.value = data.total || 0
  } catch (error) {
    console.error('获取文章列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取推荐文章
const fetchFeaturedArticles = async () => {
  try {
    const res = await request.get('/articles', { params: { page: 1, pageSize: 3 } })
    const data = res.data || res
    featuredArticles.value = data.list || []
  } catch (error) {
    console.error('获取推荐文章失败:', error)
  }
}

// 获取分类列表
const fetchCategories = async () => {
  try {
    const res = await request.get('/categories')
    categories.value = res.data || res
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 获取标签列表
const fetchTags = async () => {
  try {
    const res = await request.get('/tags')
    tags.value = res.data || res
  } catch (error) {
    console.error('获取标签失败:', error)
  }
}

// 跳转到文章详情
const goToArticle = (id) => {
  router.push(`/article/${id}`)
}

// 按分类筛选
const filterByCategory = (categoryId) => {
  queryParams.value.categoryId = categoryId
  queryParams.value.tagId = null
  queryParams.value.page = 1
  fetchArticles()
}

// 按标签筛选
const filterByTag = (tagId) => {
  queryParams.value.tagId = tagId
  queryParams.value.categoryId = null
  queryParams.value.page = 1
  fetchArticles()
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

onMounted(() => {
  fetchArticles()
  fetchFeaturedArticles()
  fetchCategories()
  fetchTags()

  // 触发进入动画
  setTimeout(() => {
    isVisible.value = true
  }, 100)

  // 文章列表延迟动画
  setTimeout(() => {
    loadedArticles.value = true
  }, 300)

  // 侧边栏延迟动画
  setTimeout(() => {
    loadedSidebar.value = true
  }, 500)
})
</script>

<template>
  <div class="home" :class="{ 'is-visible': isVisible }">
    <!-- Hero Section with Animated Bubbles -->
    <section class="hero">
      <div class="hero-bubbles">
        <span class="bubble"></span>
        <span class="bubble"></span>
        <span class="bubble"></span>
        <span class="bubble"></span>
        <span class="bubble"></span>
      </div>
      <h1 class="hero-title">✨ 欢迎来到 Sblog</h1>
      <p class="hero-subtitle">💭 分享技术，记录生活，探索无限可能</p>
      <div class="hero-scroll">
        <span></span>
      </div>
    </section>

    <!-- 推荐文章 -->
    <section v-if="featuredArticles.length > 0" class="featured-section" :class="{ 'is-visible': isVisible }">
      <h2 class="section-title">🔥 推荐文章</h2>
      <div class="featured-grid">
        <div
          v-for="(article, index) in featuredArticles"
          :key="article.id"
          class="featured-card"
          :style="{ animationDelay: `${index * 0.1}s` }"
          @click="goToArticle(article.id)"
        >
          <div class="featured-image" :style="{ backgroundImage: article.cover ? `url(${article.cover})` : 'linear-gradient(135deg, #667eea, #764ba2)' }">
            <div class="featured-overlay">
              <h3>{{ article.title }}</h3>
              <p>{{ article.summary }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 主内容区 -->
    <div class="content-wrapper">
      <!-- 文章列表 -->
      <div class="main-section" :class="{ 'is-loaded': loadedArticles }">
        <div class="section-header">
          <h2 class="section-title">📝 最新文章</h2>
          <div class="filters">
            <button
              :class="['filter-btn', { active: !queryParams.categoryId }]"
              @click="filterByCategory(null)"
            >
              🏠 全部
            </button>
            <button
              v-for="category in categories"
              :key="category.id"
              :class="['filter-btn', { active: queryParams.categoryId === category.id }]"
              @click="filterByCategory(category.id)"
            >
              {{ category.name }}
            </button>
          </div>
        </div>

        <div v-if="loading" class="loading">
          <div class="spinner"></div>
          <p>加载中...</p>
        </div>

        <div v-else-if="articles.length === 0" class="empty">
          <div class="empty-icon">📭</div>
          <p>暂无文章</p>
        </div>

        <div v-else class="article-list">
          <article
            v-for="(article, index) in articles"
            :key="article.id"
            class="article-card"
            :style="{ animationDelay: `${index * 0.08}s` }"
            @click="goToArticle(article.id)"
          >
            <div class="article-cover" :style="{ backgroundImage: article.cover ? `url(${article.cover})` : 'linear-gradient(135deg, #667eea, #764ba2)' }">
              <div class="cover-overlay"></div>
            </div>
            <div class="article-content">
              <h3 class="article-title">{{ article.title }}</h3>
              <p class="article-summary">{{ article.summary }}</p>
              <div class="article-meta">
                <span class="meta-item">
                  <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                    <line x1="16" y1="2" x2="16" y2="6"></line>
                    <line x1="8" y1="2" x2="8" y2="6"></line>
                    <line x1="3" y1="10" x2="21" y2="10"></line>
                  </svg>
                  {{ formatDate(article.createdAt) }}
                </span>
                <span class="meta-item">
                  <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                    <circle cx="12" cy="12" r="3"></circle>
                  </svg>
                  {{ article.views || 0 }}
                </span>
                <span v-if="article.categoryName" class="category-tag">{{ article.categoryName }}</span>
              </div>
              <div v-if="article.tags && article.tags.length > 0" class="article-tags">
                <span v-for="tag in article.tags" :key="tag" class="tag">{{ tag }}</span>
              </div>
            </div>
          </article>
        </div>
      </div>

      <!-- 侧边栏 -->
      <aside class="sidebar" :class="{ 'is-loaded': loadedSidebar }">
        <!-- 分类 -->
        <div class="sidebar-section">
          <h3 class="sidebar-title">📚 分类</h3>
          <ul class="sidebar-list">
            <li v-for="(category, index) in categories" :key="category.id" :style="{ animationDelay: `${index * 0.05}s` }">
              <a @click="filterByCategory(category.id)">
                {{ category.name }}
                <span class="count">({{ category.articleCount || 0 }})</span>
              </a>
            </li>
          </ul>
        </div>

        <!-- 标签云 -->
        <div class="sidebar-section">
          <h3 class="sidebar-title">🏷️ 标签</h3>
          <div class="tag-cloud">
            <span
              v-for="(tag, index) in tags"
              :key="tag.id"
              class="tag-cloud-item"
              :style="{ animationDelay: `${index * 0.03}s` }"
              @click="filterByTag(tag.id)"
            >
              {{ tag.name }}
            </span>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<style scoped lang="scss">
.home {
  opacity: 0;
  transform: translateY(20px);
  transition: opacity 0.6s ease, transform 0.6s ease;

  &.is-visible {
    opacity: 1;
    transform: translateY(0);
  }

  .hero {
    text-align: center;
    padding: 80px 20px 60px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    border-radius: 16px;
    margin-bottom: 50px;
    position: relative;
    overflow: hidden;

    .hero-bubbles {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      overflow: hidden;
      pointer-events: none;

      .bubble {
        position: absolute;
        bottom: -100px;
        background: rgba(255, 255, 255, 0.1);
        border-radius: 50%;
        animation: rise 15s infinite ease-in;

        &:nth-child(1) {
          width: 80px;
          height: 80px;
          left: 10%;
          animation-delay: 0s;
          animation-duration: 8s;
        }

        &:nth-child(2) {
          width: 40px;
          height: 40px;
          left: 20%;
          animation-delay: 2s;
          animation-duration: 10s;
        }

        &:nth-child(3) {
          width: 60px;
          height: 60px;
          left: 35%;
          animation-delay: 4s;
          animation-duration: 12s;
        }

        &:nth-child(4) {
          width: 50px;
          height: 50px;
          left: 50%;
          animation-delay: 1s;
          animation-duration: 9s;
        }

        &:nth-child(5) {
          width: 70px;
          height: 70px;
          left: 65%;
          animation-delay: 3s;
          animation-duration: 11s;
        }
      }
    }

    .hero-title {
      font-size: 42px;
      font-weight: 700;
      margin-bottom: 15px;
      position: relative;
      z-index: 1;
      animation: fadeInDown 0.8s ease;
    }

    .hero-subtitle {
      font-size: 20px;
      opacity: 0.95;
      position: relative;
      z-index: 1;
      animation: fadeInUp 0.8s ease 0.2s both;
    }

    .hero-scroll {
      position: absolute;
      bottom: 20px;
      left: 50%;
      transform: translateX(-50%);
      z-index: 1;
      animation: bounce 2s infinite;

      span {
        display: block;
        width: 24px;
        height: 40px;
        border: 2px solid rgba(255, 255, 255, 0.5);
        border-radius: 12px;
        position: relative;

        &::before {
          content: '';
          position: absolute;
          top: 8px;
          left: 50%;
          transform: translateX(-50%);
          width: 4px;
          height: 8px;
          background: rgba(255, 255, 255, 0.8);
          border-radius: 2px;
          animation: scroll 2s infinite;
        }
      }
    }
  }

  .featured-section {
    margin-bottom: 50px;
    opacity: 0;
    transform: translateY(30px);
    transition: opacity 0.6s ease, transform 0.6s ease;

    &.is-visible {
      opacity: 1;
      transform: translateY(0);
    }

    .section-title {
      font-size: 26px;
      font-weight: 600;
      margin-bottom: 25px;
      color: #333;
    }

    .featured-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
      gap: 25px;

      .featured-card {
        cursor: pointer;
        border-radius: 16px;
        overflow: hidden;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
        transition: transform 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275), box-shadow 0.4s ease;
        opacity: 0;
        animation: fadeInUp 0.6s ease forwards;

        &:hover {
          transform: translateY(-8px) scale(1.02);
          box-shadow: 0 12px 30px rgba(102, 126, 234, 0.25);
        }

        .featured-image {
          height: 220px;
          background-size: cover;
          background-position: center;
          position: relative;

          .featured-overlay {
            position: absolute;
            bottom: 0;
            left: 0;
            right: 0;
            padding: 25px;
            background: linear-gradient(to top, rgba(0, 0, 0, 0.85), transparent);

            h3 {
              color: #fff;
              font-size: 18px;
              margin: 0 0 10px 0;
            }

            p {
              color: rgba(255, 255, 255, 0.85);
              font-size: 14px;
              margin: 0;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              -webkit-box-orient: vertical;
              overflow: hidden;
            }
          }
        }
      }
    }
  }

  .content-wrapper {
    display: grid;
    grid-template-columns: 1fr 300px;
    gap: 30px;
  }

  .main-section {
    opacity: 0;
    transform: translateX(-20px);
    transition: opacity 0.6s ease, transform 0.6s ease;

    &.is-loaded {
      opacity: 1;
      transform: translateX(0);
    }

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 25px;
      flex-wrap: wrap;
      gap: 15px;

      .section-title {
        font-size: 26px;
        font-weight: 600;
        margin: 0;
        color: #333;
      }

      .filters {
        display: flex;
        gap: 10px;
        flex-wrap: wrap;

        .filter-btn {
          padding: 8px 18px;
          border: 1px solid #e0e0e0;
          background: #fff;
          border-radius: 25px;
          cursor: pointer;
          font-size: 14px;
          transition: all 0.3s ease;
          position: relative;
          overflow: hidden;

          &::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: linear-gradient(135deg, #667eea, #764ba2);
            opacity: 0;
            transition: opacity 0.3s ease;
            z-index: -1;
          }

          &:hover {
            border-color: #667eea;
            color: #667eea;
            transform: translateY(-2px);
          }

          &.active {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: #fff;
            border-color: transparent;
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
          }
        }
      }
    }

    .loading,
    .empty {
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

      .empty-icon {
        font-size: 64px;
        margin-bottom: 15px;
      }
    }

    .article-list {
      display: flex;
      flex-direction: column;
      gap: 25px;

      .article-card {
        display: flex;
        background: #fff;
        border-radius: 16px;
        overflow: hidden;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
        cursor: pointer;
        transition: transform 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275), box-shadow 0.4s ease;
        opacity: 0;
        animation: fadeInUp 0.6s ease forwards;

        &:hover {
          transform: translateY(-5px) scale(1.01);
          box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);

          .cover-overlay {
            opacity: 1;
          }
        }

        .article-cover {
          width: 240px;
          flex-shrink: 0;
          background-size: cover;
          background-position: center;
          position: relative;

          .cover-overlay {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(135deg, rgba(102, 126, 234, 0.2), rgba(118, 75, 162, 0.2));
            opacity: 0;
            transition: opacity 0.3s ease;
          }
        }

        .article-content {
          flex: 1;
          padding: 25px;

          .article-title {
            font-size: 20px;
            font-weight: 600;
            margin: 0 0 12px 0;
            color: #333;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            transition: color 0.3s ease;
          }

          .article-summary {
            font-size: 14px;
            color: #666;
            line-height: 1.7;
            margin: 0 0 18px 0;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }

          .article-meta {
            display: flex;
            align-items: center;
            gap: 15px;
            margin-bottom: 12px;

            .meta-item {
              display: flex;
              align-items: center;
              gap: 5px;
              font-size: 13px;
              color: #999;
              transition: color 0.3s ease;
            }

            .category-tag {
              padding: 4px 12px;
              background: linear-gradient(135deg, #667eea, #764ba2);
              color: #fff;
              border-radius: 15px;
              font-size: 12px;
            }
          }

          .article-tags {
            display: flex;
            gap: 8px;
            flex-wrap: wrap;

            .tag {
              padding: 5px 12px;
              background: #f5f5f5;
              border-radius: 15px;
              font-size: 12px;
              color: #666;
              transition: all 0.3s ease;

              &:hover {
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: #fff;
              }
            }
          }
        }
      }
    }
  }

  .sidebar {
    opacity: 0;
    transform: translateX(20px);
    transition: opacity 0.6s ease, transform 0.6s ease;

    &.is-loaded {
      opacity: 1;
      transform: translateX(0);
    }

    .sidebar-section {
      background: #fff;
      border-radius: 16px;
      padding: 25px;
      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
      margin-bottom: 25px;
      transition: transform 0.3s ease, box-shadow 0.3s ease;

      &:hover {
        transform: translateY(-3px);
        box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
      }

      .sidebar-title {
        font-size: 18px;
        font-weight: 600;
        margin: 0 0 18px 0;
        color: #333;
        padding-bottom: 12px;
        border-bottom: 3px solid transparent;
        border-image: linear-gradient(135deg, #667eea, #764ba2) 1;
      }

      .sidebar-list {
        list-style: none;
        padding: 0;
        margin: 0;

        li {
          opacity: 0;
          animation: fadeInRight 0.4s ease forwards;

          a {
            display: flex;
            justify-content: space-between;
            padding: 12px 0;
            color: #666;
            text-decoration: none;
            cursor: pointer;
            transition: all 0.3s ease;
            border-radius: 8px;
            padding-left: 10px;
            padding-right: 10px;

            &:hover {
              color: #667eea;
              background: #f8f9ff;
              transform: translateX(5px);
            }

            .count {
              color: #999;
              font-size: 14px;
              transition: color 0.3s ease;
            }

            &:hover .count {
              color: #667eea;
            }
          }
        }
      }

      .tag-cloud {
        display: flex;
        flex-wrap: wrap;
        gap: 10px;

        .tag-cloud-item {
          padding: 8px 16px;
          background: #f5f5f5;
          border-radius: 25px;
          font-size: 14px;
          color: #666;
          cursor: pointer;
          transition: all 0.3s ease;
          opacity: 0;
          animation: fadeInScale 0.4s ease forwards;

          &:hover {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: #fff;
            transform: scale(1.1);
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .home {
    .hero {
      padding: 60px 20px 40px;

      .hero-title {
        font-size: 32px;
      }

      .hero-subtitle {
        font-size: 16px;
      }
    }

    .content-wrapper {
      grid-template-columns: 1fr;
    }

    .main-section {
      .article-list {
        .article-card {
          flex-direction: column;

          .article-cover {
            width: 100%;
            height: 180px;
          }
        }
      }
    }

    .sidebar {
      transform: translateY(0);
    }
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
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

@keyframes fadeInRight {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes fadeInScale {
  from {
    opacity: 0;
    transform: scale(0.8);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes rise {
  0% {
    bottom: -100px;
    transform: translateX(0);
  }
  50% {
    transform: translateX(50px);
  }
  100% {
    bottom: 100%;
    transform: translateX(-50px);
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

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateX(-50%) translateY(0);
  }
  40% {
    transform: translateX(-50%) translateY(-10px);
  }
  60% {
    transform: translateX(-50%) translateY(-5px);
  }
}

@keyframes scroll {
  0% {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
  100% {
    opacity: 0;
    transform: translateX(-50%) translateY(12px);
  }
}

// 深色模式样式
[data-theme='dark'] .home {
  background: #1a1a1a;

  .hero {
    background: linear-gradient(135deg, #4c3d7a 0%, #5a4a8a 100%);
  }

  .section-title,
  .hero-title,
  .hero-subtitle {
    color: #e0e0e0;
  }

  .featured-section .featured-card {
    background: #252530;

    .featured-overlay {
      background: linear-gradient(to top, rgba(0, 0, 0, 0.9), transparent);
    }
  }

  .main-section .section-header .filters .filter-btn {
    background: #2a2a3a;
    border-color: #3a3a4a;
    color: #e0e0e0;

    &:hover {
      border-color: #a78bfa;
      color: #a78bfa;
    }

    &.active {
      background: linear-gradient(135deg, #a78bfa, #818cf8);
    }
  }

  .article-list .article-card {
    background: #252530;
  }

  .sidebar .sidebar-section {
    background: #252530;
  }
}
</style>
