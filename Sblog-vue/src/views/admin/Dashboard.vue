<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/axios'
import { Document, ChatDotSquare, User, View, Message, TrendCharts } from '@element-plus/icons-vue'

const loading = ref(false)
const dashboardData = ref({
  articleCount: 0,
  draftCount: 0,
  commentCount: 0,
  messageCount: 0,
  userCount: 0,
  viewCount: 0,
  todayViews: 0,
  recentArticles: [],
  recentComments: []
})

const fetchDashboardData = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/dashboard')
    dashboardData.value = res.data || res
  } catch (error) {
    console.error('获取仪表盘数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchDashboardData()
})
</script>

<template>
  <div class="dashboard">
    <h2 class="page-title">数据统计</h2>

    <el-row :gutter="20" class="stats-row" v-loading="loading">
      <el-col :xs="12" :sm="8" :md="6" :lg="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: #409eff">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ dashboardData.articleCount }}</div>
            <div class="stat-label">文章总数</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="8" :md="6" :lg="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: #67c23a">
            <el-icon><ChatDotSquare /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ dashboardData.commentCount }}</div>
            <div class="stat-label">评论总数</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="8" :md="6" :lg="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: #e6a23c">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ dashboardData.userCount }}</div>
            <div class="stat-label">用户总数</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="8" :md="6" :lg="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: #f56c6c">
            <el-icon><View /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ dashboardData.viewCount }}</div>
            <div class="stat-label">总访问量</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="stats-row">
      <el-col :xs="12" :sm="8" :md="8" :lg="8">
        <div class="stat-card">
          <div class="stat-icon" style="background: #909399">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ dashboardData.draftCount }}</div>
            <div class="stat-label">草稿数量</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="8" :md="8" :lg="8">
        <div class="stat-card">
          <div class="stat-icon" style="background: #606266">
            <el-icon><Message /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ dashboardData.messageCount }}</div>
            <div class="stat-label">留言总数</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="8" :md="8" :lg="8">
        <div class="stat-card">
          <div class="stat-icon" style="background: #409eff">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ dashboardData.todayViews }}</div>
            <div class="stat-label">今日访问</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="content-row">
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card class="recent-card">
          <template #header>
            <div class="card-header">
              <span>最近文章</span>
            </div>
          </template>
          <el-table :data="dashboardData.recentArticles" style="width: 100%">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="views" label="浏览" width="80" />
            <el-table-column prop="date" label="日期" width="120" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card class="recent-card">
          <template #header>
            <div class="card-header">
              <span>最近评论</span>
            </div>
          </template>
          <el-table :data="dashboardData.recentComments" style="width: 100%">
            <el-table-column prop="content" label="内容" show-overflow-tooltip />
            <el-table-column prop="author" label="作者" width="100" />
            <el-table-column prop="date" label="日期" width="120" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped lang="scss">
.dashboard {
  .page-title {
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 20px;
    color: #333;
  }

  .stats-row {
    margin-bottom: 20px;
  }

  .stat-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    align-items: center;
    gap: 15px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
    }

    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-size: 24px;
    }

    .stat-content {
      flex: 1;

      .stat-number {
        font-size: 28px;
        font-weight: 700;
        color: #333;
        line-height: 1;
        margin-bottom: 5px;
      }

      .stat-label {
        font-size: 14px;
        color: #999;
      }
    }
  }

  .content-row {
    .recent-card {
      height: 100%;

      .card-header {
        font-weight: 600;
        color: #333;
      }
    }
  }
}
</style>
