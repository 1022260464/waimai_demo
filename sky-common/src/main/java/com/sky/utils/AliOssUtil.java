package com.sky.utils;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;

@Data
@AllArgsConstructor
@Slf4j
public class AliOssUtil {

    // 去掉了错误的 @Value 注解，由 OssConfiguration 统一通过构造器注入
    private String endpoint;
    private String bucketName;
    private String region;

    /**
     * 文件上传
     *
     * @param bytes 文件字节数组
     * @param objectName 文件名
     * @return 成功返回文件URL，失败返回 null
     */
    public String upload(byte[] bytes, String objectName) {
        OSS ossClient = null;
        try {
            // 1. 获取凭证 (自动读取环境变量)
            String accessKeyId = System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID");
            String accessKeySecret = System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET");

            // 增加安全校验：如果环境变量没读到，直接拦截，避免向阿里云发起无效请求
            if (accessKeyId == null || accessKeySecret == null) {
                log.error("致命错误: 未读取到环境变量 ALIBABA_CLOUD_ACCESS_KEY_ID 或 ALIBABA_CLOUD_ACCESS_KEY_SECRET");
                return null; // 直接返回 null，防止假上传
            }

            DefaultCredentialProvider credentialsProvider = new DefaultCredentialProvider(accessKeyId, accessKeySecret);

            // 2. 显式声明使用 V4 签名算法
            ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
            clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);

            // 3. 创建 OSSClient 实例
            ossClient = OSSClientBuilder.create()
                    .endpoint(endpoint)
                    .credentialsProvider(credentialsProvider)
                    .clientConfiguration(clientBuilderConfiguration)
                    .region(region) // V4 签名必须指定 region
                    .build();

            // 4. 执行上传请求
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));

            // 5. 拼装成功后的访问路径
            String cleanEndpoint = endpoint.startsWith("https://") ? endpoint.substring(8) : endpoint;
            String fileUrl = "https://" + bucketName + "." + cleanEndpoint + "/" + objectName;

            log.info("文件上传成功，访问路径: {}", fileUrl);
            return fileUrl;

        } catch (OSSException oe) {
            log.error("OSS请求被拒绝。错误代码: {}, 错误信息: {}, Request ID: {}",
                    oe.getErrorCode(), oe.getErrorMessage(), oe.getRequestId());
            return null; // 关键修复：失败后返回 null，Controller层判断为null则提示前端上传失败
        } catch (ClientException ce) {
            log.error("客户端网络或内部错误: {}", ce.getMessage());
            return null;
        } catch (Exception e) {
            log.error("文件上传发生未知异常", e);
            return null;
        } finally {
            // 释放资源，防止内存泄漏
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}