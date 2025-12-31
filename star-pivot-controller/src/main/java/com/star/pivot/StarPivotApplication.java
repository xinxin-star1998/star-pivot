package com.star.pivot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 *
 * @author xinxin
 */
@SpringBootApplication(scanBasePackages = "com.star.pivot")
@MapperScan("com.star.pivot.*.mapper")
public class StarPivotApplication {
    public static void main(String[] args) {
        SpringApplication.run(StarPivotApplication.class, args);
        System.out.println("""
                
                ========================================
                StarPivot权限管理系统启动成功！
                访问地址: http://localhost:8080
                API文档: http://localhost:8080/doc.html
                ========================================
                """);
    }
}
