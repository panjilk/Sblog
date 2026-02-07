<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const categories = ref([])
const tags = ref([])

const activeCategory = ref(null)
const searchKeyword = ref('')
const isScrolled = ref(false)
const isVisible = ref(false)

// 监听滚动
const handleScroll = () => {
  isScrolled.value = window.scrollY > 50
}

// 点击关于时跳转到原来的 index.vue 个人介绍页
const goToAbout = () => {
  router.push('/index-intro')
}

const navigateTo = (path) => {
  router.push(path)
}

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push(`/search?keyword=${searchKeyword.value}`)
  }
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  setTimeout(() => {
    isVisible.value = true
  }, 100)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<template>
  <div class="front-layout" :class="{ 'is-visible': isVisible }">
    <!-- 导航栏 -->
    <header class="header" :class="{ 'is-scrolled': isScrolled }">
      <div class="header-container">
        <div class="logo" @click="navigateTo('/index')">
          <h1>✨ Sblog</h1>
        </div>

        <nav class="nav">
          <router-link to="/index" class="nav-link">
            <span class="link-icon">🏠</span>
            <span class="link-text">首页</span>
          </router-link>
          <router-link to="/categories" class="nav-link">
            <span class="link-icon">📚</span>
            <span class="link-text">分类</span>
          </router-link>
          <router-link to="/tags" class="nav-link">
            <span class="link-icon">🏷️</span>
            <span class="link-text">标签</span>
          </router-link>
          <a @click="goToAbout" class="nav-link">
            <span class="link-icon">👤</span>
            <span class="link-text">关于</span>
          </a>
        </nav>

        <div class="header-actions">
          <div class="search-box">
            <input
              v-model="searchKeyword"
              type="text"
              placeholder="搜索文章..."
              @keyup.enter="handleSearch"
            />
            <button @click="handleSearch">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="8"></circle>
                <path d="m21 21-4.35-4.35"></path>
              </svg>
            </button>
          </div>
          <router-link to="/login" class="btn-login">
            <span>🔑</span> 登录
          </router-link>
        </div>
      </div>
    </header>

    <!-- 主内容区域 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-container">
        <div class="footer-content">
          <div class="footer-section">
            <h3>💡 关于博客</h3>
            <p>Sblog - 一个简洁优雅的博客系统</p>
            <p>分享技术，记录生活，探索无限可能</p>
          </div>
          <div class="footer-section">
            <h3>🔗 快速链接</h3>
            <ul>
              <li><router-link to="/index">🏠 首页</router-link></li>
              <li><router-link to="/categories">📚 分类</router-link></li>
              <li><router-link to="/tags">🏷️ 标签</router-link></li>
              <li><a @click="goToAbout">👤 关于</a></li>
            </ul>
          </div>
          <div class="footer-section">
            <h3>📬 联系方式</h3>
            <ul>
              <li>📧 Email: 1223694121@qq.com</li>
              <li>💻 GitHub: sblog</li>
            </ul>
          </div>
        </div>
        <div class="footer-bottom">
          <p>© 2026 Sblog. Made with ❤️</p>
        </div>
      </div>
      <!-- 装饰性背景波浪 -->
      <div class="footer-waves">
        <svg viewBox="0 0 1200 120" preserveAspectRatio="none">
          <path d="M321.39,56.44c58-10.79,114.16-30.13,172-41.86,82.39-16.72,168.19-17.73,250.45-.39C823.78,31,906.67,72,985.66,92.83c70.05,18.48,146.53,26.09,214.34,3V0H0V27.35A600.21,600.21,0,0,0,321.39,56.44Z"></path>
        </svg>
      </div>
    </footer>
  </div>
</template>

<style scoped lang="scss">
.front-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  opacity: 0;
  transition: opacity 0.6s ease;

  &.is-visible {
    opacity: 1;
  }
}

.header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 100;
  transition: all 0.3s ease;

  &.is-scrolled {
    background: rgba(255, 255, 255, 0.98);
    box-shadow: 0 4px 30px rgba(0, 0, 0, 0.1);

    .header-container {
      height: 55px;
    }

    .logo h1 {
      font-size: 22px;
    }
  }

  .header-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    height: 65px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    transition: height 0.3s ease;
  }

  .logo {
    cursor: pointer;
    transition: transform 0.3s ease;

    &:hover {
      transform: scale(1.05);
    }

    h1 {
      font-size: 26px;
      font-weight: 700;
      background: linear-gradient(135deg, #667eea, #764ba2);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      margin: 0;
      transition: font-size 0.3s ease;
    }
  }

  .nav {
    display: flex;
    gap: 35px;

    .nav-link {
      text-decoration: none;
      color: #333;
      font-weight: 500;
      transition: all 0.3s ease;
      position: relative;
      display: flex;
      align-items: center;
      gap: 5px;
      padding: 8px 12px;
      border-radius: 10px;

      .link-icon {
        font-size: 16px;
        opacity: 0.7;
        transition: opacity 0.3s ease, transform 0.3s ease;
      }

      .link-text {
        position: relative;
        z-index: 1;
      }

      &::before {
        content: '';
        position: absolute;
        inset: 0;
        background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
        border-radius: 10px;
        opacity: 0;
        transition: opacity 0.3s ease;
      }

      &:hover {
        color: #667eea;
        transform: translateY(-2px);

        .link-icon {
          opacity: 1;
          transform: scale(1.2);
        }

        &::before {
          opacity: 1;
        }
      }

      &.router-link-active {
        color: #667eea;

        &::after {
          content: '';
          position: absolute;
          bottom: -2px;
          left: 50%;
          transform: translateX(-50%);
          width: 30px;
          height: 3px;
          background: linear-gradient(135deg, #667eea, #764ba2);
          border-radius: 2px;
          animation: expandWidth 0.3s ease;
        }
      }
    }
  }

  .header-actions {
    display: flex;
    align-items: center;
    gap: 15px;

    .search-box {
      display: flex;
      align-items: center;
      background: #f5f5f5;
      border-radius: 25px;
      padding: 8px 18px;
      transition: all 0.3s ease;
      border: 2px solid transparent;

      &:focus-within {
        background: #fff;
        border-color: #667eea;
        box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
      }

      input {
        border: none;
        background: transparent;
        outline: none;
        font-size: 14px;
        width: 160px;
        transition: width 0.3s ease;

        &:focus {
          width: 180px;
        }

        &::placeholder {
          color: #999;
        }
      }

      button {
        border: none;
        background: transparent;
        cursor: pointer;
        color: #666;
        display: flex;
        align-items: center;
        transition: all 0.3s ease;
        padding: 4px;

        &:hover {
          color: #667eea;
          transform: scale(1.1);
        }
      }
    }

    .btn-login {
      padding: 10px 24px;
      background: linear-gradient(135deg, #667eea, #764ba2);
      color: #fff;
      text-decoration: none;
      border-radius: 25px;
      font-size: 14px;
      font-weight: 500;
      transition: all 0.3s ease;
      display: flex;
      align-items: center;
      gap: 6px;
      box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);

      &:hover {
        transform: translateY(-3px);
        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
      }

      &:active {
        transform: translateY(-1px);
      }
    }
  }
}

.main-content {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 30px 20px;
}

.footer {
  background: linear-gradient(135deg, #1a1a2e, #16213e);
  color: #ecf0f1;
  margin-top: auto;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(102, 126, 234, 0.5), transparent);
  }

  .footer-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 50px 20px 25px;
    position: relative;
    z-index: 1;
  }

  .footer-content {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 40px;
    margin-bottom: 35px;
  }

  .footer-section {
    opacity: 0;
    animation: fadeInUp 0.6s ease forwards;

    &:nth-child(1) {
      animation-delay: 0.1s;
    }

    &:nth-child(2) {
      animation-delay: 0.2s;
    }

    &:nth-child(3) {
      animation-delay: 0.3s;
    }

    h3 {
      font-size: 18px;
      margin-bottom: 18px;
      color: #fff;
      position: relative;
      padding-bottom: 12px;

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 0;
        width: 40px;
        height: 3px;
        background: linear-gradient(135deg, #667eea, #764ba2);
        border-radius: 2px;
      }
    }

    p {
      color: #bdc3c7;
      line-height: 1.8;
      transition: color 0.3s ease;

      &:hover {
        color: #ecf0f1;
      }
    }

    ul {
      list-style: none;
      padding: 0;
      margin: 0;

      li {
        margin-bottom: 12px;

        a {
          color: #bdc3c7;
          text-decoration: none;
          transition: all 0.3s ease;
          display: inline-flex;
          align-items: center;
          gap: 8px;
          position: relative;

          &::before {
            content: '';
            position: absolute;
            left: 0;
            bottom: -2px;
            width: 0;
            height: 2px;
            background: linear-gradient(135deg, #667eea, #764ba2);
            transition: width 0.3s ease;
          }

          &:hover {
            color: #667eea;
            transform: translateX(5px);

            &::before {
              width: 100%;
            }
          }
        }
      }
    }
  }

  .footer-bottom {
    text-align: center;
    padding-top: 25px;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    opacity: 0;
    animation: fadeIn 0.6s ease 0.4s forwards;

    p {
      margin: 0;
      color: #95a5a6;
      font-size: 14px;
      transition: color 0.3s ease;

      &:hover {
        color: #bdc3c7;
      }
    }
  }

  .footer-waves {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    overflow: hidden;
    line-height: 0;
    transform: rotate(180deg);

    svg {
      position: relative;
      display: block;
      width: calc(100% + 1.3px);
      height: 50px;
    }

    path {
      fill: rgba(255, 255, 255, 0.02);
    }
  }
}

@media (max-width: 768px) {
  .header {
    .header-container {
      height: auto;
      padding: 15px 20px;
      flex-wrap: wrap;
      gap: 10px;
    }

    .nav {
      order: 3;
      width: 100%;
      justify-content: center;
      gap: 10px;

      .nav-link {
        padding: 6px 10px;
        font-size: 14px;

        .link-icon {
          font-size: 14px;
        }

        &.router-link-active::after {
          width: 20px;
        }
      }
    }

    .logo h1 {
      font-size: 22px;
    }

    .header-actions {
      .search-box input {
        width: 100px;
      }

      .btn-login span:first-child {
        display: none;
      }
    }
  }

  .footer {
    .footer-content {
      grid-template-columns: 1fr;
      gap: 30px;
    }
  }
}

@keyframes expandWidth {
  from {
    width: 0;
  }
  to {
    width: 30px;
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

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
