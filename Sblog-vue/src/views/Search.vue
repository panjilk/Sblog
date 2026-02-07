<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import request from '@/utils/axios'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const articles = ref([])
const total = ref(0)
const isVisible = ref(false)

const searchKeyword = ref('')

// 获取搜索关键词
const keyword = computed(() => route.query.keyword || '')

// 搜索文章
const searchArticles = async () => {
  if (!keyword.value) return

  loading.value = true
  try {
    const res = await request.get('/articles', {
      params: {
        keyword: keyword.value,
        page: 1,
        pageSize: 20
      }
    })
    const data = res.data || res
    articles.value = data.list || []
    total.value = data.total || 0
    searchKeyword.value = keyword.value
  } catch (error) {
    console.error('搜索失败:', error)
    articles.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 跳转到文章详情
const goToArticle = (id) => {
  router.push(`/article/${id}`)
}

// 监听关键词变化
watch(keyword, (newKeyword) => {
  if (newKeyword) {
    searchArticles()
  }
})

onMounted(() => {
  if (keyword.value) {
    searchArticles()
  }

  setTimeout(() => {
    isVisible.value = true
  }, 100)
})
</script>

<template>
  <div class="search-page" :class="{ 'is-visible': isVisible }">
    <div class="page-header">
      <h1 class="page-title">🔍 搜索结果</h1>
      <p v-if="searchKeyword" class="search-info">
        关键词 "<span class="keyword">{{ searchKeyword }}</span>" 共找到 <span class="count">{{ total }}</span> 篇文章
      </p>
      <p v-else class="search-info">请输入搜索关键词</p>
    </div>

    <div v-if="loading && keyword" class="loading">
      <div class="spinner"></div>
      <p>搜索中...</p>
    </div>

    <div v-else-if="!keyword" class="empty">
      <div class="empty-icon">🔍</div>
      <p>请输入关键词进行搜索</p>
      <button @click="router.push('/index')" class="back-btn">🏠 返回首页</button>
    </div>

    <div v-else-if="articles.length === 0" class="empty">
      <div class="empty-icon">😕</div>
      <p>没有找到相关文章</p>
      <button @click="router.push('/index')" class="back-btn">🏠 返回首页</button>
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
            <span v-if="article.categoryName" class="category-tag">📁 {{ article.categoryName }}</span>
            <span class="meta-item">📅 {{ article.createdAt?.substring(0, 10) || '' }}</span>
            <span class="meta-item">👁️ {{ article.views || 0 }}</span>
          </div>
          <div v-if="article.tags && article.tags.length > 0" class="article-tags">
            <span v-for="tag in article.tags" :key="tag" class="tag">{{ tag }}</span>
          </div>
        </div>
      </article>
    </div>
  </div>
</template>

<style scoped lang="scss">
.search-page {
  max-width: 900px;
  margin: 0 auto;
  opacity: 0;
  transform: translateY(20px);
  transition: opacity 0.6s ease, transform 0.6s ease;

  &.is-visible {
    opacity: 1;
    transform: translateY(0);
  }

  .page-header {
    text-align: center;
    margin-bottom: 50px;
    opacity: 0;
    animation: fadeInDown 0.8s ease forwards;

    .page-title {
      font-size: 38px;
      font-weight: 700;
      margin: 0 0 20px 0;
      color: #333;
      background: linear-gradient(135deg, #667eea, #764ba2);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }

    .search-info {
      font-size: 18px;
      color: #666;
      margin: 0;

      .keyword {
        color: #667eea;
        font-weight: 600;
        background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
        padding: 2px 12px;
        border-radius: 6px;
      }

      .count {
        color: #764ba2;
        font-weight: 700;
        font-size: 20px;
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
      margin-bottom: 20px;
    }

    p {
      font-size: 18px;
      margin-bottom: 25px;
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

          .category-tag {
            padding: 4px 12px;
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: #fff;
            border-radius: 15px;
            font-size: 12px;
          }

          .meta-item {
            display: flex;
            align-items: center;
            gap: 5px;
            font-size: 13px;
            color: #999;
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

@media (max-width: 768px) {
  .search-page {
    .page-header {
      .page-title {
        font-size: 28px;
      }

      .search-info {
        font-size: 16px;
      }
    }

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
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
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
</style>
