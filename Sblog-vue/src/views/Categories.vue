<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/axios'

const router = useRouter()

const loading = ref(false)
const categories = ref([])
const isVisible = ref(false)

// 获取分类列表
const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await request.get('/categories')
    categories.value = res.data || res
  } catch (error) {
    console.error('获取分类失败:', error)
  } finally {
    loading.value = false
  }
}

// 跳转到分类文章列表
const goToCategory = (categoryId) => {
  router.push(`/index?category=${categoryId}`)
}

onMounted(() => {
  fetchCategories()

  setTimeout(() => {
    isVisible.value = true
  }, 100)
})
</script>

<template>
  <div class="categories-page" :class="{ 'is-visible': isVisible }">
    <div class="page-header">
      <h1 class="page-title">📚 文章分类</h1>
      <p class="page-subtitle">探索不同主题的文章</p>
    </div>

    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>

    <div v-else class="categories-grid">
      <div
        v-for="(category, index) in categories"
        :key="category.id"
        class="category-card"
        :style="{ animationDelay: `${index * 0.08}s` }"
        @click="goToCategory(category.id)"
      >
        <div class="category-icon">
          <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path>
            <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path>
          </svg>
        </div>
        <h3 class="category-name">{{ category.name }}</h3>
        <p v-if="category.description" class="category-desc">{{ category.description }}</p>
        <span class="category-count">{{ category.articleCount || 0 }} 篇文章</span>
        <div class="card-shine"></div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.categories-page {
  max-width: 1000px;
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
      margin: 0 0 10px 0;
      color: #333;
      background: linear-gradient(135deg, #667eea, #764ba2);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }

    .page-subtitle {
      font-size: 18px;
      color: #999;
      margin: 0;
    }
  }

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

  .categories-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
    gap: 25px;

    .category-card {
      background: #fff;
      border-radius: 16px;
      padding: 35px 30px;
      text-align: center;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
      cursor: pointer;
      transition: transform 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275), box-shadow 0.4s ease;
      position: relative;
      overflow: hidden;
      opacity: 0;
      animation: fadeInUp 0.6s ease forwards;

      &:hover {
        transform: translateY(-8px) scale(1.02);
        box-shadow: 0 12px 35px rgba(102, 126, 234, 0.2);

        .category-icon {
          background: linear-gradient(135deg, #667eea, #764ba2);
          color: #fff;
          transform: scale(1.1) rotate(5deg);
        }

        .card-shine {
          opacity: 1;
        }
      }

      .card-shine {
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
        transition: left 0.6s ease;
        opacity: 0;
        pointer-events: none;
      }

      &:hover .card-shine {
        left: 100%;
        opacity: 1;
      }

      .category-icon {
        width: 70px;
        height: 70px;
        margin: 0 auto 20px;
        border-radius: 16px;
        background: linear-gradient(135deg, #f5f5f5, #e8e8f0);
        display: flex;
        align-items: center;
        justify-content: center;
        color: #667eea;
        transition: all 0.4s ease;
        box-shadow: 0 4px 15px rgba(102, 126, 234, 0.15);
      }

      .category-name {
        font-size: 22px;
        font-weight: 600;
        margin: 0 0 12px 0;
        color: #333;
      }

      .category-desc {
        font-size: 14px;
        color: #666;
        margin: 0 0 18px 0;
        line-height: 1.6;
        min-height: 40px;
      }

      .category-count {
        display: inline-block;
        padding: 6px 16px;
        background: linear-gradient(135deg, #f5f5f5, #e8e8f0);
        border-radius: 20px;
        font-size: 13px;
        color: #999;
        transition: all 0.3s ease;
      }

      &:hover .category-count {
        background: linear-gradient(135deg, #667eea, #764ba2);
        color: #fff;
      }
    }
  }
}

@media (max-width: 768px) {
  .categories-page {
    .page-header {
      .page-title {
        font-size: 28px;
      }

      .page-subtitle {
        font-size: 16px;
      }
    }

    .categories-grid {
      grid-template-columns: 1fr;
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

// 深色模式样式
[data-theme='dark'] .categories-page {
  background: #1a1a1a;

  .page-title {
    background: linear-gradient(135deg, #a78bfa, #818cf8);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .categories-grid .category-card {
    background: #252530;

    .category-icon {
      background: linear-gradient(135deg, #3a3a4a, #2a2a3a);
      color: #a78bfa;
    }

    .category-name {
      color: #e0e0e0;
    }

    .category-desc {
      color: #b0b0b0;
    }

    .category-count {
      background: #3a3a4a;
      color: #999;
    }

    &:hover .category-count {
      background: linear-gradient(135deg, #a78bfa, #818cf8);
    }
  }
}
</style>
