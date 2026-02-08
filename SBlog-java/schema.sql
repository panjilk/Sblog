-- ============================================
-- SBlog 博客系统数据库建表语句
-- ============================================
use sblog;
-- 1. 用户表
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    `email` VARCHAR(100) UNIQUE COMMENT '邮箱',
    `avatar` VARCHAR(500) COMMENT '头像URL',
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色：USER/ADMIN',
    `status` VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态：active/inactive',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_username` (`username`),
    INDEX `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 分类表
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL UNIQUE COMMENT '分类名称',
    `description` VARCHAR(200) COMMENT '分类描述',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- 3. 标签表
DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '标签ID',
    `name` VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- 4. 文章表
DROP TABLE IF EXISTS `articles`;
CREATE TABLE `articles` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '文章ID',
    `title` VARCHAR(200) NOT NULL COMMENT '文章标题',
    `content` LONGTEXT NOT NULL COMMENT '文章内容（Markdown）',
    `summary` VARCHAR(500) COMMENT '文章摘要',
    `cover` VARCHAR(500) COMMENT '封面图URL',
    `category_id` BIGINT COMMENT '分类ID',
    `status` VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '状态：published/draft/recycle',
    `views` BIGINT NOT NULL DEFAULT 0 COMMENT '浏览量',
    `comments` BIGINT NOT NULL DEFAULT 0 COMMENT '评论数',
    `allow_comment` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否允许评论',
    `author_id` BIGINT COMMENT '作者ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`category_id`) REFERENCES `categories`(`id`) ON DELETE SET NULL,
    FOREIGN KEY (`author_id`) REFERENCES `users`(`id`) ON DELETE SET NULL,
    INDEX `idx_title` (`title`),
    INDEX `idx_category` (`category_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_at` (`created_at`),
    INDEX `idx_author` (`author_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

-- 5. 文章标签关联表（多对多）
DROP TABLE IF EXISTS `article_tags`;
CREATE TABLE `article_tags` (
    `article_id` BIGINT NOT NULL COMMENT '文章ID',
    `tag_id` BIGINT NOT NULL COMMENT '标签ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`article_id`, `tag_id`),
    FOREIGN KEY (`article_id`) REFERENCES `articles`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`tag_id`) REFERENCES `tags`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签关联表';

-- 6. 评论表
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `author` VARCHAR(50) NOT NULL COMMENT '评论者昵称',
    `email` VARCHAR(100) COMMENT '评论者邮箱',
    `article_id` BIGINT NOT NULL COMMENT '文章ID',
    `parent_id` BIGINT COMMENT '父评论ID（用于回复）',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态：pending/approved/spam',
    `ip` VARCHAR(50) COMMENT 'IP地址',
    `user_agent` VARCHAR(500) COMMENT '用户代理',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`article_id`) REFERENCES `articles`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`parent_id`) REFERENCES `comments`(`id`) ON DELETE CASCADE,
    INDEX `idx_article` (`article_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 7. 留言表
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '留言ID',
    `content` TEXT NOT NULL COMMENT '留言内容',
    `nickname` VARCHAR(50) NOT NULL COMMENT '留言者昵称',
    `contact` VARCHAR(100) COMMENT '联系方式',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态：pending/replied',
    `reply` TEXT COMMENT '回复内容',
    `ip` VARCHAR(50) COMMENT 'IP地址',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_status` (`status`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='留言表';

-- 8. 系统设置表
DROP TABLE IF EXISTS `settings`;
CREATE TABLE `settings` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '设置ID',
    `key` VARCHAR(50) NOT NULL UNIQUE COMMENT '设置键',
    `value` TEXT COMMENT '设置值',
    `description` VARCHAR(200) COMMENT '设置描述',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_key` (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统设置表';

-- ============================================
-- 初始化数据
-- ============================================

-- 插入默认管理员账户（密码：admin123，使用BCrypt加密）
INSERT INTO `users` (`username`, `password`, `email`, `role`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin@sblog.com', 'ADMIN');

-- 插入默认分类
INSERT INTO `categories` (`name`, `description`) VALUES
('技术', '技术相关文章'),
('生活', '生活随笔'),
('读书', '读书笔记');

-- 插入默认标签
INSERT INTO `tags` (`name`) VALUES
('Java'),
('Spring Boot'),
('Vue'),
('前端'),
('后端'),
('数据库');

-- 插入系统默认设置
INSERT INTO `settings` (`key`, `value`, `description`) VALUES
('siteName', 'SBlog', '网站名称'),
('siteUrl', 'https://example.com', '网站地址'),
('logo', '/logo.png', '网站Logo'),
('favicon', '/favicon.ico', '网站图标'),
('description', '欢迎来到我的博客', '网站描述'),
('keywords', '博客,技术,Java', '网站关键词'),
('footer', '© 2024 SBlog. All rights reserved.', '页脚信息');
