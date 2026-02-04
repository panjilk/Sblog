<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { logout } from '@/api/user'
import {
  Document,
  Folder,
  ChatDotSquare,
  User,
  Setting,
  DataAnalysis,
  Lock,
  Message
} from '@element-plus/icons-vue'

const router = useRouter()
const activeMenu = ref('dashboard')
const isCollapse = ref(false)

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await logout()
    localStorage.removeItem('token')
    localStorage.removeItem('rememberedUsername')
    localStorage.removeItem('rememberedpwd')
    ElMessage.success('已退出登录')
    router.push('/login')
  } catch (error) {
    // 用户取消或网络错误
    if (error !== 'cancel') {
      console.error('退出登录失败:', error)
    }
  }
}

const menuList = [
  {
    title: '数据统计',
    icon: DataAnalysis,
    index: 'dashboard'
  },
  {
    title: '文章管理',
    icon: Document,
    index: 'article',
    children: [
      { title: '所有文章', index: 'article-list' },
      { title: '写文章', index: 'article-create' }
    ]
  },
  {
    title: '分类标签',
    icon: Folder,
    index: 'category-tag',
    children: [
      { title: '分类管理', index: 'category' },
      { title: '标签管理', index: 'tag' }
    ]
  },
  {
    title: '评论管理',
    icon: ChatDotSquare,
    index: 'comment'
  },
  {
    title: '留言管理',
    icon: Message,
    index: 'message'
  },
  {
    title: '用户管理',
    icon: User,
    index: 'user',
    children: [
      { title: '用户列表', index: 'user-list' },
      { title: '权限管理', index: 'permission' }
    ]
  },
  {
    title: '系统设置',
    icon: Setting,
    index: 'settings-basic'
  },
  {
    title: '安全维护',
    icon: Lock,
    index: 'security-password'
  }
]
</script>

<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <h2 v-if="!isCollapse">Sblog 后台</h2>
        <span v-else>S</span>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :unique-opened="true"
        router
      >
        <template v-for="item in menuList" :key="item.index">
          <!-- 有子菜单 -->
          <el-sub-menu v-if="item.children" :index="item.index">
            <template #title>
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.title }}</span>
            </template>
            <el-menu-item
              v-for="sub in item.children"
              :key="sub.index"
              :index="sub.index"
            >
              {{ sub.title }}
            </el-menu-item>
          </el-sub-menu>

          <!-- 无子菜单 -->
          <el-menu-item v-else :index="item.index">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container class="main-container">
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-button
            :icon="isCollapse ? 'Expand' : 'Fold'"
            circle
            @click="isCollapse = !isCollapse"
          />
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>首页</el-breadcrumb-item>
            <el-breadcrumb-item>控制台</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <el-button type="primary" @click="$router.push('/')">
            <el-icon><Document /></el-icon>
            查看前台
          </el-button>
          <el-dropdown>
            <div class="user-info">
              <el-avatar :size="32" src="/avator.jpg" />
              <span>管理员</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人中心</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区域 -->
      <el-main class="content">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<style scoped lang="scss">
.admin-layout {
  display: flex;
  height: 100vh;
  background: #f5f7fa;
}

.sidebar {
  background: #304156;
  transition: width 0.3s;
  overflow-x: hidden;

  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #2b3a4a;

    h2, span {
      color: #fff;
      margin: 0;
    }

    h2 {
      font-size: 20px;
    }

    span {
      font-size: 24px;
      font-weight: bold;
    }
  }

  :deep(.el-menu) {
    border-right: none;
    background: #304156;
  }

  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    color: #bfcbd9;

    &:hover {
      background: #263445 !important;
    }
  }

  :deep(.el-menu-item.is-active) {
    color: #409eff !important;
    background: #263445 !important;
  }
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;

  .header-left {
    display: flex;
    align-items: center;
    gap: 15px;
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 15px;

    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
    }
  }
}

.content {
  padding: 20px;
  overflow-y: auto;
}
</style>
