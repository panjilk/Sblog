import { createRouter, createWebHistory } from 'vue-router'
import request from '@/utils/axios'
import { getLogger } from '@/utils/logger'

const log = getLogger('Router')

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: () => import('@/layouts/FrontLayout.vue'),
      children: [
        {
          path: '',
          redirect: '/index'
        },
        {
          path: 'index',
          component: () => import('@/views/Home.vue')
        },
        {
          path: 'article/:id',
          component: () => import('@/views/ArticleDetail.vue')
        },
        {
          path: 'categories',
          component: () => import('@/views/Categories.vue')
        },
        {
          path: 'tags',
          component: () => import('@/views/Tags.vue')
        },
        {
          path: 'search',
          component: () => import('@/views/Search.vue')
        },
        {
          path: 'friend-links',
          component: () => import('@/views/FriendLinks.vue')
        },
        {
          path: 'about',
          redirect: '/index-intro'
        }
      ]
    },
    {
      path: '/index',
      redirect: '/'
    },
    {
      path: '/index-intro',
      component: () => import('@/views/index.vue')
    },
    {
      path: '/login',
      component: () => import('@/views/login.vue')
    },
    {
      path: '/register',
      component: () => import('@/views/register.vue')
    },
    {
      path: '/admin',
      component: () => import('@/views/admin.vue'),
      redirect: '/admin/dashboard',
      children: [
        {
          path: 'dashboard',
          component: () => import('@/views/admin/Dashboard.vue')
        },
        {
          path: 'article-list',
          component: () => import('@/views/admin/ArticleList.vue')
        },
        {
          path: 'article-create',
          component: () => import('@/views/admin/ArticleCreate.vue')
        },
        {
          path: 'article-edit/:id',
          component: () => import('@/views/admin/ArticleCreate.vue')
        },
        {
          path: 'article-draft',
          component: () => import('@/views/admin/ArticleDraft.vue')
        },
        {
          path: 'category',
          component: () => import('@/views/admin/Category.vue')
        },
        {
          path: 'tag',
          component: () => import('@/views/admin/Tag.vue')
        },
        {
          path: 'comment',
          component: () => import('@/views/admin/Comment.vue')
        },
        {
          path: 'friend-links',
          component: () => import('@/views/admin/FriendLinkManage.vue')
        },
        {
          path: 'message',
          component: () => import('@/views/admin/Message.vue')
        },
        {
          path: 'user-list',
          component: () => import('@/views/admin/UserList.vue')
        },
        {
          path: 'permission',
          component: () => import('@/views/admin/Permission.vue')
        },
        {
          path: 'settings-basic',
          component: () => import('@/views/admin/SettingsBasic.vue')
        },
        {
          path: 'security-password',
          component: () => import('@/views/admin/SecurityPassword.vue')
        },
        {
          path: 'system-monitor',
          component: () => import('@/views/admin/SystemMonitor.vue')
        },
        {
          path: 'visit-log',
          component: () => import('@/views/admin/VisitLog.vue')
        }
      ]
    }
  ]
})

// 访问记录节流缓存
const visitRecordCache = new Map()
const THROTTLE_DURATION = 5000 // 5秒内同一路径只记录一次

/**
 * 记录页面访问（带节流）
 */
const recordVisit = (to) => {
  // 只记录前台页面访问，不记录管理后台
  if (to.path.startsWith('/admin')) {
    return
  }

  // 过滤掉非正常路由（浏览器插件等发出的请求）
  const invalidPaths = ['/hybridaction', '/tracker', '/analytics', '/metrics']
  if (invalidPaths.some(prefix => to.path.startsWith(prefix))) {
    return
  }

  // 检查是否在节流期内
  const now = Date.now()
  const lastRecord = visitRecordCache.get(to.path)

  if (lastRecord && now - lastRecord < THROTTLE_DURATION) {
    log.debug(`访问日志节流: 跳过记录 ${to.path}，距离上次记录仅 ${now - lastRecord}ms`)
    return
  }

  log.debug('正在记录访问:', to.path)

  // 更新最后记录时间
  visitRecordCache.set(to.path, now)

  // 异步上报访问日志到后端
  request.post('/admin/visit-log/record', {
    path: to.path,
    fullPath: to.fullPath,
    query: to.query,
    referrer: document.referrer
  }).then(res => {
    log.debug('访问记录成功:', res)
  }).catch(err => {
    // 静默失败，不影响用户体验
    log.warn('记录访问失败:', err)
    // 记录失败时，清除缓存，允许重试
    visitRecordCache.delete(to.path)
  })
}

// 全局前置守卫 - 记录页面访问
router.beforeEach((to, from, next) => {
  // 记录所有页面访问（除了首次加载时的重复记录）
  // 只在页面真正变化时记录
  if (to.path !== from.path) {
    recordVisit(to)
  }
  next()
})

// 定期清理过期的缓存记录，防止内存泄漏
setInterval(() => {
  const now = Date.now()
  for (const [path, timestamp] of visitRecordCache.entries()) {
    if (now - timestamp > THROTTLE_DURATION * 2) {
      visitRecordCache.delete(path)
    }
  }
}, THROTTLE_DURATION)

export default router
