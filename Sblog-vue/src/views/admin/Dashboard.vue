<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import request from '@/utils/axios'
import { Document, ChatDotSquare, User, View, Message, TrendCharts } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const loading = ref(false)
const chartLoading = ref(false)
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

// 图表相关
const trendChartRef = ref(null)
const categoryChartRef = ref(null)
const visitChartRef = ref(null)
let trendChart = null
let categoryChart = null
let visitChart = null

// 图表数据
const trendData = ref({
  dates: [],
  articles: [],
  comments: []
})

const categoryData = ref([])

const visitData = ref({
  dates: [],
  views: []
})

const fetchDashboardData = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/dashboard')

    // 确保数据结构正确
    const data = res.data || res

    dashboardData.value = {
      articleCount: data.articleCount || 0,
      draftCount: data.draftCount || 0,
      commentCount: data.commentCount || 0,
      messageCount: data.messageCount || 0,
      userCount: data.userCount || 0,
      viewCount: data.viewCount || 0,
      todayViews: data.todayViews || 0,
      recentArticles: data.recentArticles || [],
      recentComments: data.recentComments || []
    }

    console.log('Dashboard data:', dashboardData.value)

    // 获取图表数据
    fetchChartData()
  } catch (error) {
  } finally {
    loading.value = false
  }
}

// 初始化所有图表
const initCharts = () => {
  initTrendChart()
  initCategoryChart()
  initVisitChart()
}

// 初始化趋势图表
const initTrendChart = () => {
  if (!trendChartRef.value) return

  trendChart = echarts.init(trendChartRef.value, null, {
    renderer: 'canvas',
    useDirtyRect: true
  })

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['文章发布', '评论数量'],
      top: 10
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: trendData.value.dates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '文章发布',
        type: 'line',
        smooth: true,
        data: trendData.value.articles,
        itemStyle: {
          color: '#667eea'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(102, 126, 234, 0.3)' },
            { offset: 1, color: 'rgba(102, 126, 234, 0.05)' }
          ])
        }
      },
      {
        name: '评论数量',
        type: 'line',
        smooth: true,
        data: trendData.value.comments,
        itemStyle: {
          color: '#764ba2'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(118, 75, 162, 0.3)' },
            { offset: 1, color: 'rgba(118, 75, 162, 0.05)' }
          ])
        }
      }
    ]
  }

  trendChart.setOption(option)
}

// 初始化分类图表
const initCategoryChart = () => {
  if (!categoryChartRef.value) return

  categoryChart = echarts.init(categoryChartRef.value, null, {
    renderer: 'canvas',
    useDirtyRect: true
  })

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 10,
      top: 'center'
    },
    series: [
      {
        name: '文章分类',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: categoryData.value
      }
    ]
  }

  categoryChart.setOption(option)
}

// 初始化访问量图表
const initVisitChart = () => {
  if (!visitChartRef.value) return

  visitChart = echarts.init(visitChartRef.value, null, {
    renderer: 'canvas',
    useDirtyRect: true
  })

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: visitData.value.dates,
      axisTick: {
        alignWithLabel: true
      }
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '访问量',
        type: 'bar',
        barWidth: '60%',
        data: visitData.value.views,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#667eea' },
            { offset: 1, color: '#764ba2' }
          ]),
          borderRadius: [8, 8, 0, 0]
        }
      }
    ]
  }

  visitChart.setOption(option)
}

// 窗口大小改变时重新调整图表大小
const handleResize = () => {
  trendChart?.resize()
  categoryChart?.resize()
  visitChart?.resize()
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取图表数据
const fetchChartData = async () => {
  chartLoading.value = true
  try {
    const res = await request.get('/admin/dashboard/chart', {
      params: { days: 7 }
    })

    const data = res.data || res

    // 处理趋势数据
    if (data.trendData && data.trendData.length > 0) {
      trendData.value = {
        dates: data.trendData.map(item => item.date),
        articles: data.trendData.map(item => item.articleCount || 0),
        comments: data.trendData.map(item => item.commentCount || 0)
      }

      visitData.value = {
        dates: data.trendData.map(item => item.date),
        views: data.trendData.map(item => item.viewCount || 0)
      }
    }

    // 处理分类数据
    if (data.categoryStats && data.categoryStats.length > 0) {
      categoryData.value = data.categoryStats.map(item => ({
        value: item.count,
        name: item.name
      }))
    }

    console.log('Chart data:', { trendData: trendData.value, categoryData: categoryData.value, visitData: visitData.value })

    // 初始化图表
    await nextTick()
    initCharts()
  } catch (error) {
    console.error('获取图表数据失败:', error)
  } finally {
    chartLoading.value = false
  }
}

onMounted(() => {
  fetchDashboardData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  categoryChart?.dispose()
  visitChart?.dispose()
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

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :sm="24" :md="16" :lg="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>📈 内容发布趋势</span>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="8" :lg="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>📊 分类统计</span>
            </div>
          </template>
          <div ref="categoryChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :sm="24" :md="24" :lg="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>👁️ 访问量统计</span>
            </div>
          </template>
          <div ref="visitChartRef" class="chart-container-large"></div>
        </el-card>
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
            <el-table-column prop="title" label="标题" show-overflow-tooltip />
            <el-table-column prop="views" label="浏览" width="80" />
            <el-table-column label="日期" width="120">
              <template #default="{ row }">
                {{ formatDate(row.createdAt) }}
              </template>
            </el-table-column>
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
            <el-table-column label="日期" width="160">
              <template #default="{ row }">
                {{ formatDate(row.createdAt) }}
              </template>
            </el-table-column>
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

  .chart-row {
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

  .chart-card {
    height: 100%;

    .card-header {
      font-weight: 600;
      color: #333;
    }

    .chart-container {
      height: 300px;
      width: 100%;
    }

    .chart-container-large {
      height: 250px;
      width: 100%;
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
