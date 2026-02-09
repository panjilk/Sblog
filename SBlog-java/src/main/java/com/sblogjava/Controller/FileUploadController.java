package com.sblogjava.Controller;

import com.sblogjava.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/admin/upload")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Value("${upload.path:/tmp/sblog/uploads}")
    private String uploadPath;

    @Value("${upload.url-prefix:/uploads}")
    private String urlPrefix;

    // 允许的图片格式
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp");
    // 允许的文件扩展名
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp"));
    // 最大文件大小 10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    // 图片文件魔术数字（前几个字节）
    private static final Map<String, String> MAGIC_NUMBERS = new HashMap<>();
    static {
        MAGIC_NUMBERS.put("image/jpeg", "\uFFFD\uFFFD\uFFFD"); // FF D8 FF
        MAGIC_NUMBERS.put("image/png", "\uFFFD\uFFFD"); // 89 50 4E 47
        MAGIC_NUMBERS.put("image/gif", "GIF8"); // 47 49 46 38
        MAGIC_NUMBERS.put("image/webp", "RIFF"); // 52 49 46 46
    }

    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 验证文件
            if (file.isEmpty()) {
                return Result.error("请选择文件");
            }

            // 验证文件大小
            if (file.getSize() > MAX_FILE_SIZE) {
                return Result.error("图片大小不能超过 10MB");
            }

            // 获取原始文件名并验证扩展名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return Result.error("文件名无效");
            }

            String extension = getFileExtension(originalFilename);
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                return Result.error("只支持 JPG、PNG、GIF、WebP 格式的图片");
            }

            // 验证文件类型（Content-Type）
            String contentType = file.getContentType();
            if (!ALLOWED_IMAGE_TYPES.contains(contentType)) {
                return Result.error("文件类型不合法");
            }

            // 验证扩展名与Content-Type是否匹配
            if (!isExtensionMatchContentType(extension, contentType)) {
                return Result.error("文件扩展名与内容类型不匹配");
            }

            // 验证文件真实内容（魔术数字）
            if (!validateFileContent(file, contentType)) {
                logger.warn("检测到伪造文件类型: {}", originalFilename);
                return Result.error("文件内容与声明的类型不匹配");
            }

            // 生成安全的文件名（使用UUID + 原始扩展名）
            String filename = UUID.randomUUID().toString() + extension;

            // 按日期创建目录: /uploads/2026/02/
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
            String datePath = sdf.format(new Date());
            String relativePath = datePath + "/" + filename;
            String fullPath = uploadPath + "/" + relativePath;

            // 规范化路径，防止路径遍历攻击
            Path path = Paths.get(fullPath).normalize();
            if (!path.startsWith(Paths.get(uploadPath).normalize())) {
                logger.error("检测到路径遍历攻击: {}", fullPath);
                return Result.error("非法的文件路径");
            }

            // 确保目录存在
            Files.createDirectories(path.getParent());

            // 保存文件
            file.transferTo(path.toFile());

            logger.info("文件上传成功: {}, 大小: {} bytes", filename, file.getSize());

            // 返回访问URL
            Map<String, String> result = new HashMap<>();
            result.put("url", urlPrefix + "/" + relativePath);
            result.put("filename", filename);
            result.put("size", String.valueOf(file.getSize()));

            return Result.success("上传成功", result);
        } catch (IOException e) {
            logger.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件扩展名（小写）
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex).toLowerCase();
    }

    /**
     * 验证扩展名与Content-Type是否匹配
     */
    private boolean isExtensionMatchContentType(String extension, String contentType) {
        switch (contentType) {
            case "image/jpeg":
            case "image/jpg":
                return extension.equals(".jpg") || extension.equals(".jpeg");
            case "image/png":
                return extension.equals(".png");
            case "image/gif":
                return extension.equals(".gif");
            case "image/webp":
                return extension.equals(".webp");
            default:
                return false;
        }
    }

    /**
     * 验证文件真实内容（魔术数字）
     */
    private boolean validateFileContent(MultipartFile file, String contentType) {
        try (InputStream inputStream = file.getInputStream()) {
            byte[] header = new byte[12];
            int bytesRead = inputStream.read(header);

            if (bytesRead < 4) {
                return false;
            }

            String magicNumberPattern = MAGIC_NUMBERS.get(contentType);
            if (magicNumberPattern == null) {
                return true; // 如果没有定义魔术数字，跳过验证
            }

            String fileHeader = new String(header, 0, Math.min(bytesRead, magicNumberPattern.length()));

            // JPEG: FF D8 FF
            if ("image/jpeg".equals(contentType) || "image/jpg".equals(contentType)) {
                return (header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8 && (header[2] & 0xFF) == 0xFF;
            }
            // PNG: 89 50 4E 47
            else if ("image/png".equals(contentType)) {
                return header[0] == (byte) 0x89 && header[1] == (byte) 0x50 &&
                       header[2] == (byte) 0x4E && header[3] == (byte) 0x47;
            }
            // GIF: 47 49 46 38
            else if ("image/gif".equals(contentType)) {
                return header[0] == (byte) 0x47 && header[1] == (byte) 0x49 &&
                       header[2] == (byte) 0x46 && header[3] == (byte) 0x38;
            }
            // WebP: RIFF....WEBP
            else if ("image/webp".equals(contentType)) {
                if (bytesRead < 12) return false;
                return fileHeader.startsWith("RIFF") &&
                       new String(header, 8, 4).equals("WEBP");
            }

            return true;
        } catch (IOException e) {
            logger.error("读取文件内容失败", e);
            return false;
        }
    }

    @DeleteMapping("/image")
    public Result<Void> deleteImage(@RequestBody Map<String, String> request) {
        try {
            String url = request.get("url");
            if (url == null || url.isEmpty()) {
                return Result.error("URL不能为空");
            }

            // 从 URL 中提取相对路径
            String relativePath = url.replace(urlPrefix + "/", "");

            // 安全检查：防止路径遍历攻击
            if (relativePath.contains("..") || relativePath.contains("/") || relativePath.contains("\\")) {
                // 只允许单层目录结构 (yyyy/MM/filename.ext)
                if (relativePath.split("/").length > 3 || relativePath.split("\\\\").length > 3) {
                    logger.error("检测到路径遍历攻击: {}", relativePath);
                    return Result.error("非法的文件路径");
                }
            }

            // 验证扩展名是否合法
            String extension = getFileExtension(relativePath);
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                logger.error("尝试删除非法文件类型: {}", relativePath);
                return Result.error("不允许删除此类型的文件");
            }

            String fullPath = uploadPath + "/" + relativePath;

            // 规范化路径并验证是否在上传目录内
            Path path = Paths.get(fullPath).normalize();
            Path uploadDir = Paths.get(uploadPath).normalize();

            if (!path.startsWith(uploadDir)) {
                logger.error("检测到路径遍历攻击: {} 不在上传目录内", fullPath);
                return Result.error("非法的文件路径");
            }

            File file = path.toFile();
            if (file.exists() && file.isFile()) {
                if (file.delete()) {
                    logger.info("文件删除成功: {}", relativePath);
                    return Result.success("删除成功", null);
                } else {
                    logger.error("文件删除失败: {}", relativePath);
                    return Result.error("删除失败");
                }
            } else {
                logger.warn("文件不存在: {}", relativePath);
                return Result.error("文件不存在");
            }
        } catch (Exception e) {
            logger.error("删除文件时发生错误", e);
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}
