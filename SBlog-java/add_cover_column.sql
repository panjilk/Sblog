-- 添加 cover 列到 articles 表
-- 如果列已存在，执行此语句会报错，可以忽略
ALTER TABLE `articles`
ADD COLUMN `cover` VARCHAR(500) COMMENT '封面图URL'
AFTER `summary`;
