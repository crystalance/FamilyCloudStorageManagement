package com.example.familycloudstoragemanagement;

import cn.dev33.satoken.SaManager;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@MapperScan("com.example.familycloudstoragemanagement.UserManagement.dataAccess.mapper")
@EnableWebSecurity
@NacosPropertySource(dataId = "example", autoRefreshed = true)
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class }) //避开springboot的安全检查 //
public class FamilyCloudStorageManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(FamilyCloudStorageManagementApplication.class, args);
//        显示sa-token框架的配置
        //System.out.println("启动成功，Sa-Token 配置如下：" + SaManager.getConfig());
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
