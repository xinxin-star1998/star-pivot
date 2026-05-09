package com.star.pivot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 *
 * @author xinxin
 */
@SpringBootApplication(scanBasePackages = "com.star.pivot")
@MapperScan(basePackages = {
        "com.star.pivot.*.mapper"
})
@EnableCaching
@EnableAsync
@EnableScheduling
public class StarPivotApplication {

    private static final Logger log = LoggerFactory.getLogger(StarPivotApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(StarPivotApplication.class, args);
        log.info("""
                
                ========================================
                StarPivot 权限管理系统启动成功！
                访问地址: http://localhost:8080
                API 文档: http://localhost:8080/api/swagger-ui/index.html
                OpenAPI JSON: http://localhost:8080/api/v3/api-docs
                文件存储: 阿里云 OSS / MinIO
                Druid: http://localhost:8080/api/druid/index.html
                ========================================
                """);
    }
}
