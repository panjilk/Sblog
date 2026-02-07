import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
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
        }
      ]
    }
  ]
})

export default router
