# Sblog 后台管理系统 API 文档

## 基础信息

| 配置项 | 值 |
|--------|-----|
| 基础路径 | `/api` |
| 请求头 | `Authorization: Bearer {token}` |
| 超时时间 | 10000ms |

---

## 一、用户认证模块

### 1.1 用户登录

**接口**: `POST /admin/users/login`

**请求参数**:
```json
{
  "username": "用户名",
  "password": "密码"
}
```

**字段说明**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |

**返回示例**:
```json
{
  "code": 200,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "role": "admin",
      "avatar": "头像URL"
    }
  },
  "message": "登录成功"
}
```

### 1.2 用户注册

**接口**: `POST /admin/users/register`

**请求参数**:
```json
{
  "username": "用户名",
  "password": "密码",
  "email": "邮箱"
}
```

**字段说明**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 用户名（需唯一） |
| password | string | 是 | 密码 |
| email | string | 是 | 邮箱 |

**返回示例**:
```json
{
  "code": 200,
  "data": {
    "id": 2,
    "username": "newuser",
    "email": "newuser@example.com",
    "role": "user"
  },
  "message": "注册成功"
}
```

### 1.3 获取用户信息

**接口**: `GET /admin/users/info`

**请求头**: 需要 token

**返回示例**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "role": "admin",
    "avatar": "头像URL",
    "createdAt": "2024-01-01 10:00:00"
  }
}
```

### 1.4 退出登录

**接口**: `POST /admin/users/logout`

**请求头**: 需要 token

**返回示例**:
```json
{
  "code": 200,
  "message": "退出成功"
}
```

### 1.5 检查用户名是否存在

**接口**: `GET /admin/users/check-username`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 待检查的用户名 |

**返回示例**:
```json
{
  "code": 200,
  "data": {
    "exists": false,
    "username": "newuser"
  }
}
```

---

## 二、数据统计模块

### 1.1 获取仪表盘数据

**接口**: `GET /admin/dashboard`

**请求头**: 需要 token

**返回示例**:
```json
{
  "code": 200,
  "data": {
    "articleCount": 100,
    "commentCount": 500,
    "userCount": 50,
    "viewCount": 10000,
    "todayViews": 200,
    "recentArticles": [
      { "id": 1, "title": "文章标题", "views": 100, "date": "2024-01-01" }
    ]
  }
}
```
123123
---

## 三、文章管理模块

### 3.1 获取文章列表

**接口**: `GET /admin/articles`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | number | 否 | 页码，默认1 |
| pageSize | number | 否 | 每页数量，默认10 |
| status | string | 否 | 状态：published/draft/recycle |
| keyword | string | 否 | 搜索关键词 |

**返回示例**:
```json
{
  "code": 200,
  "data": {
    "total": 100,
    "list": [
      {
        "id": 1,
        "title": "文章标题",
        "summary": "文章摘要",
        "cover": "封面图URL",
        "categoryId": 1,
        "categoryName": "技术",
        "tags": ["Vue", "前端"],
        "status": "published",
        "views": 100,
        "comments": 10,
        "createdAt": "2024-01-01 10:00:00",
        "updatedAt": "2024-01-01 10:00:00"
      }
    ]
  }
}
```

### 3.2 获取文章详情

**接口**: `GET /admin/articles/:id`

**返回示例**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "title": "文章标题",
    "content": "文章内容（Markdown）",
    "summary": "文章摘要",
    "cover": "封面图URL",
    "categoryId": 1,
    "tags": [1, 2],
    "status": "published",
    "views": 100,
    "allowComment": true,
    "createdAt": "2024-01-01 10:00:00"
  }
}
```

### 3.3 创建文章

**接口**: `POST /admin/articles`

**请求参数**:
```json
{
  "title": "文章标题",
  "content": "文章内容",
  "summary": "文章摘要",
  "cover": "封面图URL",
  "categoryId": 1,
  "tags": [1, 2],
  "status": "published",
  "allowComment": true
}
```

**字段说明**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | string | 是 | 文章标题 |
| content | string | 是 | 文章内容 |
| summary | string | 否 | 文章摘要 |
| cover | string | 否 | 封面图URL |
| categoryId | number | 是 | 分类ID |
| tags | array | 否 | 标签ID数组 |
| status | string | 是 | 状态：published/draft |
| allowComment | boolean | 否 | 是否允许评论 |

### 3.4 更新文章

**接口**: `PUT /admin/articles/:id`

**请求参数**: 同创建文章

### 3.5 删除文章

**接口**: `DELETE /admin/articles/:id`

---

## 四、分类管理模块

### 4.1 获取分类列表

**接口**: `GET /admin/categories`

**返回示例**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "name": "技术",
      "description": "技术文章",
      "articleCount": 50,
      "createdAt": "2024-01-01 10:00:00"
    }
  ]
}
```

### 4.2 创建分类

**接口**: `POST /admin/categories`

**请求参数**:
```json
{
  "name": "分类名称",
  "description": "分类描述"
}
```

### 4.3 更新分类

**接口**: `PUT /admin/categories/:id`

**请求参数**: 同创建分类

### 4.4 删除分类

**接口**: `DELETE /admin/categories/:id`

---

## 五、标签管理模块

### 5.1 获取标签列表

**接口**: `GET /admin/tags`

**返回示例**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "name": "Vue",
      "articleCount": 20,
      "createdAt": "2024-01-01 10:00:00"
    }
  ]
}
```

### 5.2 创建标签

**接口**: `POST /admin/tags`

**请求参数**:
```json
{
  "name": "标签名称"
}
```

### 5.3 更新标签

**接口**: `PUT /admin/tags/:id`

### 5.4 删除标签

**接口**: `DELETE /admin/tags/:id`

---

## 六、评论管理模块

### 6.1 获取评论列表

**接口**: `GET /admin/comments`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | number | 否 | 页码 |
| pageSize | number | 否 | 每页数量 |
| status | string | 否 | 状态：pending/approved/spam |
| articleId | number | 否 | 文章ID |

**返回示例**:
```json
{
  "code": 200,
  "data": {
    "total": 100,
    "list": [
      {
        "id": 1,
        "content": "评论内容",
        "author": "访客",
        "email": "user@example.com",
        "articleId": 1,
        "articleTitle": "文章标题",
        "status": "pending",
        "ip": "127.0.0.1",
        "createdAt": "2024-01-01 10:00:00"
      }
    ]
  }
}
```

### 6.2 回复评论

**接口**: `POST /admin/comments/:id/reply`

**请求参数**:
```json
{
  "content": "回复内容"
}
```

### 6.3 删除评论

**接口**: `DELETE /admin/comments/:id`

---

## 七、留言管理模块

### 7.1 获取留言列表

**接口**: `GET /admin/messages`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | number | 否 | 页码 |
| status | string | 否 | 状态：pending/replied |

**返回示例**:
```json
{
  "code": 200,
  "data": {
    "total": 50,
    "list": [
      {
        "id": 1,
        "content": "留言内容",
        "nickname": "访客",
        "contact": "联系方式",
        "status": "pending",
        "reply": "回复内容",
        "createdAt": "2024-01-01 10:00:00"
      }
    ]
  }
}
```

### 7.2 回复留言

**接口**: `POST /admin/messages/:id/reply`

**请求参数**:
```json
{
  "reply": "回复内容"
}
```

### 7.3 删除留言

**接口**: `DELETE /admin/messages/:id`

---

## 八、用户管理模块

### 8.1 获取用户列表

**接口**: `GET /admin/users`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | number | 否 | 页码 |
| pageSize | number | 否 | 每页数量 |
| keyword | string | 否 | 搜索关键词 |

**返回示例**:
```json
{
  "code": 200,
  "data": {
    "total": 50,
    "list": [
      {
        "id": 1,
        "username": "admin",
        "email": "admin@example.com",
        "role": "admin",
        "avatar": "头像URL",
        "status": "active",
        "createdAt": "2024-01-01 10:00:00"
      }
    ]
  }
}
```

### 8.2 创建用户

**接口**: `POST /admin/users`

**请求参数**:
```json
{
  "username": "用户名",
  "password": "密码",
  "email": "邮箱",
  "role": "user"
}
```

**角色说明**: admin、editor、user

### 8.3 更新用户

**接口**: `PUT /admin/users/:id`

**请求参数**:
```json
{
  "email": "邮箱",
  "role": "editor",
  "status": "active"
}
```

### 8.4 删除用户

**接口**: `DELETE /admin/users/:id`

### 8.5 获取角色列表

**接口**: `GET /admin/roles`

**返回示例**:
```json
{
  "code": 200,
  "data": [
    { "value": "admin", "label": "管理员", "permissions": ["*"] },
    { "value": "editor", "label": "编辑", "permissions": ["article:*", "comment:*"] },
    { "value": "user", "label": "普通用户", "permissions": [] }
  ]
}
```

---

## 九、系统设置模块

### 9.1 获取基本设置

**接口**: `GET /admin/settings`

**返回示例**:
```json
{
  "code": 200,
  "data": {
    "siteName": "Sblog",
    "siteUrl": "https://example.com",
    "logo": "logo.png",
    "favicon": "favicon.ico",
    "description": "网站描述",
    "keywords": "关键词",
    "footer": "© 2024 Sblog"
  }
}
```

### 9.2 更新基本设置

**接口**: `PUT /admin/settings`

**请求参数**:
```json
{
  "siteName": "Sblog",
  "siteUrl": "https://example.com",
  "logo": "logo.png",
  "description": "网站描述",
  "keywords": "关键词",
  "footer": "页脚信息"
}
```

---

## 十、安全维护模块

### 10.1 修改密码

**接口**: `PUT /admin/password`

**请求参数**:
```json
{
  "oldPassword": "旧密码",
  "newPassword": "新密码"
}
```

---

## 通用返回格式

### 成功返回
```json
{
  "code": 200,
  "data": {},
  "message": "操作成功"
}
```

### 失败返回
```json
{
  "code": 400,
  "message": "错误信息"
}
```

### 状态码说明
| code | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权/登录失效 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |
