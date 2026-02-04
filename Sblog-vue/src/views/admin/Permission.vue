<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/axios'

const loading = ref(false)
const roles = ref([])

const rolePermissions = {
  admin: { label: '管理员', permissions: ['*'] },
  editor: { label: '编辑', permissions: ['article:read', 'article:create', 'article:update', 'article:delete', 'comment:read', 'comment:delete'] },
  user: { label: '普通用户', permissions: [] }
}

const fetchRoles = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/users/roles')
    const roleList = res.data || res
    roles.value = roleList.map(role => ({
      ...role,
      permissions: rolePermissions[role.value]?.permissions || []
    }))
  } catch (error) {
    console.error('获取角色列表失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchRoles()
})
</script>

<template>
  <div class="permission">
    <div class="page-header">
      <h2>权限管理</h2>
    </div>

    <el-card class="role-card" v-loading="loading">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" :lg="8" v-for="role in roles" :key="role.value">
          <div class="role-item">
            <div class="role-header">
              <h3>{{ role.label }}</h3>
              <el-tag :type="role.value === 'admin' ? 'danger' : role.value === 'editor' ? 'warning' : 'info'" size="large">
                {{ role.value }}
              </el-tag>
            </div>
            <div class="role-content">
              <p class="role-title">权限列表：</p>
              <div class="permissions">
                <el-tag
                  v-for="perm in role.permissions"
                  :key="perm"
                  type="success"
                  size="small"
                  style="margin: 2px"
                >
                  {{ perm === '*' ? '全部权限' : perm }}
                </el-tag>
                <span v-if="role.permissions.length === 0" class="no-permission">无特殊权限</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="info-card">
      <template #header>
        <span>权限说明</span>
      </template>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="管理员 (admin)">
          拥有系统全部权限，可以管理所有内容
        </el-descriptions-item>
        <el-descriptions-item label="编辑 (editor)">
          可以创建、编辑、删除文章，管理评论
        </el-descriptions-item>
        <el-descriptions-item label="普通用户 (user)">
          只能浏览前台内容，无后台管理权限
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.permission {
  .page-header {
    margin-bottom: 20px;

    h2 {
      font-size: 24px;
      font-weight: 600;
      margin: 0;
      color: #333;
    }
  }

  .role-card {
    margin-bottom: 20px;

    .role-item {
      border: 1px solid #e4e7ed;
      border-radius: 8px;
      padding: 20px;
      height: 100%;

      .role-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 15px;
        padding-bottom: 10px;
        border-bottom: 1px solid #e4e7ed;

        h3 {
          font-size: 18px;
          font-weight: 600;
          margin: 0;
          color: #333;
        }
      }

      .role-content {
        .role-title {
          font-size: 14px;
          color: #666;
          margin-bottom: 10px;
        }

        .permissions {
          .no-permission {
            color: #999;
            font-size: 14px;
          }
        }
      }
    }
  }

  .info-card {
    margin-top: 20px;
  }
}
</style>
