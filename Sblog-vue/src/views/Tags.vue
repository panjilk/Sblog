<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/axios'

const router = useRouter()

const loading = ref(false)
const tags = ref([])
const isVisible = ref(false)

// 获取标签列表
const fetchTags = async () => {
  loading.value = true
  try {
    const res = await request.get('/tags')
    tags.value = res.data || res
  } catch (error) {
    console.error('获取标签失败:', error)
  } finally {
    loading.value = false
  }
}

// 跳转到标签文章列表
const goToTag = (tagId) => {
  router.push(`/index?tag=${tagId}`)
}

onMounted(() => {
  fetchTags()

  setTimeout(() => {
    isVisible.value = true
  }, 100)
})
</script>

<template>
  <div class="tags-page" :class="{ 'is-visible': isVisible }">
    <div class="page-header">
      <h1 class="page-title">🏷️ 文章标签</h1>
      <p class="page-subtitle">通过标签发现更多感兴趣的内容</p>
    </div>

    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>

    <div v-else class="tags-cloud">
      <span
        v-for="(tag, index) in tags"
        :key="tag.id"
        class="tag-item"
        :style="{
          fontSize: Math.max(14, Math.min(20, 14 + (tag.articleCount || 0) * 0.8)) + 'px',
          animationDelay: `${index * 0.05}s`,
          transform: `scale(${0.8 + Math.random() * 0.4})`
        }"
        @click="goToTag(tag.id)"
      >
        {{ tag.name }}
        <span class="tag-count">{{ tag.articleCount || 0 }}</span>
        <div class="tag-glow"></div>
      </span>
    </div>
  </div>
</template>

<style scoped lang="scss">
.tags-page {
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

  .tags-cloud {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 18px;
    padding: 50px 30px;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
    border-radius: 20px;
    box-shadow: inset 0 2px 10px rgba(0, 0, 0, 0.03);

    .tag-item {
      padding: 12px 24px;
      background: #fff;
      border: 2px solid #e0e0e0;
      border-radius: 30px;
      cursor: pointer;
      transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
      display: flex;
      align-items: center;
      gap: 8px;
      position: relative;
      overflow: hidden;
      opacity: 0;
      animation: popIn 0.5s ease forwards;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

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
        color: #fff;
        border-color: transparent;
        transform: translateY(-5px) scale(1.1);
        box-shadow: 0 8px 25px rgba(102, 126, 234, 0.35);

        &::before {
          opacity: 1;
        }

        .tag-count {
          color: rgba(255, 255, 255, 0.9);
        }

        .tag-glow {
          opacity: 0.5;
        }
      }

      .tag-count {
        font-size: 11px;
        color: #999;
        background: #f5f5f5;
        padding: 2px 8px;
        border-radius: 12px;
        transition: all 0.3s ease;
        position: relative;
        z-index: 1;
      }

      &:hover .tag-count {
        background: rgba(255, 255, 255, 0.2);
      }

      .tag-glow {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        width: 100%;
        height: 100%;
        background: radial-gradient(circle, rgba(255, 255, 255, 0.8), transparent 70%);
        opacity: 0;
        transition: opacity 0.3s ease;
        pointer-events: none;
      }

      > span:not(.tag-count):not(.tag-glow) {
        position: relative;
        z-index: 1;
      }
    }
  }
}

@media (max-width: 768px) {
  .tags-page {
    .page-header {
      .page-title {
        font-size: 28px;
      }

      .page-subtitle {
        font-size: 16px;
      }
    }

    .tags-cloud {
      padding: 30px 15px;
      gap: 12px;

      .tag-item {
        padding: 10px 18px;
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

@keyframes popIn {
  from {
    opacity: 0;
    transform: scale(0.3);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

// 深色模式样式
[data-theme='dark'] .tags-page {
  background: #1a1a1a;

  .page-title {
    background: linear-gradient(135deg, #a78bfa, #818cf8);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .tags-cloud {
    background: linear-gradient(135deg, rgba(167, 139, 250, 0.08), rgba(129, 140, 248, 0.08));
    box-shadow: inset 0 2px 10px rgba(0, 0, 0, 0.2);

    .tag-item {
      background: #252530;
      border-color: #3a3a4a;
      color: #e0e0e0;

      .tag-count {
        background: #3a3a4a;
        color: #999;
      }

      &:hover .tag-count {
        background: rgba(255, 255, 255, 0.15);
      }
    }
  }
}
</style>
