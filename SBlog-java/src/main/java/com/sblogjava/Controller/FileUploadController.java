package com.sblogjava.Controller;

import com.sblogjava.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/admin/upload")
public class FileUploadController {

    @Value("${upload.path:/tmp/sblog/uploads}")
    private String uploadPath;

    @Value("${upload.url-prefix:/uploads}")
    private String urlPrefix;

    // 允许的图片格式
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp");
    // 最大文件大小 10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 验证文件
            if (file.isEmpty()) {
                return Result.error("请选择文件");
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (!ALLOWED_IMAGE_TYPES.contains(contentType)) {
                return Result.error("只支持 JPG、PNG、GIF、WebP 格式的图片");
            }

            // 验证文件大小
            if (file.getSize() > MAX_FILE_SIZE) {
                return Result.error("图片大小不能超过 10MB");
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;

            // 按日期创建目录: /uploads/2026/02/
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
            String datePath = sdf.format(new Date());
            String relativePath = datePath + "/" + filename;
            String fullPath = uploadPath + "/" + relativePath;

            // 确保目录存在
            Path path = Paths.get(fullPath);
            Files.createDirectories(path.getParent());

            // 保存文件
            file.transferTo(path.toFile());

            // 返回访问URL
            Map<String, String> result = new HashMap<>();
            result.put("url", urlPrefix + "/" + relativePath);
            result.put("filename", filename);
            result.put("size", String.valueOf(file.getSize()));

            return Result.success("上传成功", result);
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/image")
    public Result<Void> deleteImage(@RequestBody Map<String, String> request) {
        try {
            String url = request.get("url");
            if (url != null && !url.isEmpty()) {
                // 从 URL 中提取相对路径
                String relativePath = url.replace(urlPrefix + "/", "");
                String fullPath = uploadPath + "/" + relativePath;

                File file = new File(fullPath);
                if (file.exists() && file.isFile()) {
                    file.delete();
                }
            }
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}
