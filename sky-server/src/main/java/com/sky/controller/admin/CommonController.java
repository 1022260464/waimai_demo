package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/common")
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;


@PostMapping("/upload")
public Result<String> upload(MultipartFile file) {
    log.info("接收到文件上传请求：{}", file.getOriginalFilename());
    try {
        // 1. 获取原始文件名和后缀
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        // ================== 文件名 ==================
        // 2. 指定你的文件夹名称 (注意：最后一定要带上斜杠 /)
        String folderName = "cangqiong/images/";

        // 3. 构造新的文件名称： 文件夹名 + UUID + 后缀名
        // 生成的结果类似：cangqiong/images/31e24845-e64d-4b1d.jpg
        String objectName = folderName + UUID.randomUUID().toString() + extension;
        // ===================================================

        // 4. 调用工具类上传
        byte[] bytes = file.getBytes();
        String filePath = aliOssUtil.upload(bytes, objectName);

        if (filePath == null) {
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
        return Result.success(filePath);

    } catch (IOException e) {
        log.error("文件上传读取异常", e);
        e.printStackTrace();
//        return Result.error(MessageConstant.UPLOAD_FAILED);
    }

    return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}