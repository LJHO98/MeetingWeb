package com.MeetingWeb.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${groupUploadPath}")
    private String groupUploadPath;

    @Value("${groupImgPath}")
    private String groupImgPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/uploads/**")
//                .addResourceLocations("file:///" + groupImgPath + "/");

        // groupImgPath를 파일 시스템 경로로 설정
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:///" + new File(groupImgPath).getAbsolutePath() + "/");
    }
}
