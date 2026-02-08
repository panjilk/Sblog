use sblog;
-- 创建友情链接表
CREATE TABLE IF NOT EXISTS `friend_links` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '链接ID',
    `name` VARCHAR(100) NOT NULL COMMENT '链接名称',
    `url` VARCHAR(500) NOT NULL COMMENT '链接地址',
    `logo` VARCHAR(500) COMMENT 'Logo图片URL',
    `description` VARCHAR(200) COMMENT '链接描述',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序值，数字越小越靠前',
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_sort_order` (`sort_order`),
    INDEX `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='友情链接表';
