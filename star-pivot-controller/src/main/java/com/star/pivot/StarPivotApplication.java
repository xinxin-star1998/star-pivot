package com.star.pivot;

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
@MapperScan("com.star.pivot.*.mapper")
@EnableCaching
@EnableAsync
@EnableScheduling
public class StarPivotApplication {
    public static void main(String[] args) {
        SpringApplication.run(StarPivotApplication.class, args);
        System.out.println("""
                
                ========================================
                StarPivot权限管理系统启动成功！
                访问地址: http://localhost:8080
                API文档: http://localhost:8080/api/swagger-ui/index.html
                OpenAPI JSON: http://localhost:8080/api/v3/api-docs
                文件存储: 阿里云 OSS
                Druid: http://localhost:8080/api/druid/index.html
                ========================================
                """);
    }
}
