package com.gof.ICNBack;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class Application {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);

    }
    @Bean
    WebMvcConfigurer createWebMvcConfigurer(@Autowired HandlerInterceptor[] interceptors) {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // 映射路径`/static/`到classpath路径:
                registry.addResourceHandler("/static/**")
                        .addResourceLocations("classpath:/static/");
            }

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 对所有路径生效
                        .allowedOriginPatterns("*") // 允许所有源，生产环境建议指定具体域名
                        .allowedMethods("GET", "POST", "PUT")
                        .allowedHeaders("*")
                        .exposedHeaders("X-Error", "X-Total-Count", "X-Auth-Token") // 暴露自定义头信息
                        .allowCredentials(true) // 允许携带认证信息
                        .maxAge(3600); // 预检请求缓存时间（秒）

            }
        };
    }

    @Bean
    RestTemplate createRestTemplate(){
        return new RestTemplate();
    }

    @Bean
    ObjectMapper createObjectMapper(){
        return new ObjectMapper();
    }

}
