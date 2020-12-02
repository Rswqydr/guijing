package com.xiannvzuo.guijing.config;

import com.xiannvzuo.guijing.common.Constants;
import com.xiannvzuo.guijing.interceptor.AdminLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GuiWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    AdminLoginInterceptor adminLoginInterceptor;

    // 注册拦截器
    public void addInterceptors(InterceptorRegistry registry) {
       registry.addInterceptor(adminLoginInterceptor).addPathPatterns("/admin/**")
               .excludePathPatterns("/admin/login")
               .excludePathPatterns("/admin/plugins/**")
               .excludePathPatterns("/admin/dist/**");


    }
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**").addResourceLocations("file:" + Constants.FILE_UPLOAD_DIC);
    }


}
