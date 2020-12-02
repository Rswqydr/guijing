package com.xiannvzuo.guijing.controller.common;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class KaptchaConfig {

    @Bean
    public DefaultKaptcha getDefaultKaptcha() {
        // 获取kaptcha的defaultKaptch
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        // 配置属性properties
        Properties properties = new Properties();
        properties.put("kaptcha.border", "no");
        properties.put("kaptcha.textproducer.font.color", "black");
        properties.put("kaptcha.image.width", "150");
        properties.put("kaptcha.image.height", "40");
        properties.put("kaptcha.textproducer.font.size", "30");
        properties.put("kaptcha.session.key", "verifyCode");
        properties.put("kaptcha.textproducer.char.space", "5");
        // 将属性封装给Config
        Config config = new Config(properties);
        // 设置defaultKaptcha的默认config
        defaultKaptcha.setConfig(config);
        return  defaultKaptcha;

    }
}
