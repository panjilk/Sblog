<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/axios'

const loading = ref(false)
const friendLinks = ref([])

const fetchFriendLinks = async () => {
  loading.value = true
  try {
    const res = await request.get('/friend-links')
    friendLinks.value = res.data || res
  } catch (error) {
    console.error('获取友情链接失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchFriendLinks()
})
</script>

<template>
  <div class="friend-links-page">
    <div class="page-header">
      <h1>友情链接</h1>
      <p>与志同道合的朋友一起交流学习</p>
    </div>

    <div v-loading="loading" class="links-container">
      <div v-if="friendLinks.length === 0" class="empty-state">
        <el-empty description="暂无友情链接" />
      </div>
      <div v-else class="links-grid">
        <a
          v-for="link in friendLinks"
          :key="link.id"
          :href="link.url"
          target="_blank"
          class="link-card"
        >
          <div class="link-logo">
            <img v-if="link.logo" :src="link.logo" :alt="link.name" />
            <div v-else class="default-logo">{{ link.name.charAt(0) }}</div>
          </div>
          <div class="link-info">
            <div class="link-name">{{ link.name }}</div>
            <div v-if="link.description" class="link-description">{{ link.description }}</div>
          </div>
        </a>
      </div>
    </div>

    <div class="apply-section">
      <el-card>
        <h3>申请友情链接</h3>
        <p>欢迎志同道合的朋友交换友情链接，请通过网页最底部的邮箱通过以下格式进行联系：</p>
        <ul>
          <li>网站名称：您的网站名称</li>
          <li>网站地址：您的网站URL</li>
          <li>网站简介：简短描述您的网站</li>
          <li>联系方式：邮箱或其他联系方式</li>
        </ul>
        <p class="note">注：申请友链前请先添加本站链接，审核通过后会尽快添加。</p>
      </el-card>
    </div>
  </div>
</template>

<style scoped lang="scss">
.friend-links-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;

  .page-header {
    text-align: center;
    margin-bottom: 40px;

    h1 {
      font-size: 32px;
      font-weight: 600;
      margin-bottom: 10px;
      color: #333;
    }

    p {
      font-size: 16px;
      color: #666;
    }
  }

  .links-container {
    margin-bottom: 40px;
  }

  .links-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
  }

  .link-card {
    display: flex;
    align-items: center;
    gap: 15px;
    padding: 20px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    text-decoration: none;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    }
  }

  .link-logo {
    width: 60px;
    height: 60px;
    border-radius: 12px;
    overflow: hidden;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .default-logo {
      color: #fff;
      font-size: 24px;
      font-weight: 600;
    }
  }

  .link-info {
    flex: 1;
    min-width: 0;
  }

  .link-name {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin-bottom: 4px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .link-description {
    font-size: 14px;
    color: #666;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .apply-section {
    h3 {
      font-size: 20px;
      font-weight: 600;
      margin-bottom: 15px;
      color: #333;
    }

    p {
      color: #666;
      margin-bottom: 10px;
    }

    ul {
      margin: 15px 0;
      padding-left: 20px;

      li {
        color: #666;
        margin-bottom: 8px;
      }
    }

    .note {
      font-size: 14px;
      color: #999;
      margin-top: 15px;
    }
  }

  .empty-state {
    padding: 60px 0;
  }
}

// 深色模式样式
[data-theme='dark'] .friend-links-page {
  background: #1a1a1a;

  .page-header {
    h1 {
      color: #e0e0e0;
    }

    p {
      color: #b0b0b0;
    }
  }

  .link-card {
    background: #252530;

    .link-name {
      color: #e0e0e0;
    }

    .link-description {
      color: #b0b0b0;
    }
  }

  .apply-section {
    h3 {
      color: #e0e0e0;
    }

    p, ul li {
      color: #b0b0b0;
    }

    .note {
      color: #999;
    }
  }
}
</style>
