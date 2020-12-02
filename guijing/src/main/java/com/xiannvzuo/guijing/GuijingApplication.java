package com.xiannvzuo.guijing;

import com.xiannvzuo.guijing.controller.admin.GuijingGoodsCategoryController;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sun.util.locale.provider.LocaleServiceProviderPool;


@SpringBootApplication
@MapperScan("com.xiannvzuo.guijing.dao")
public class GuijingApplication {
	private static final Logger LOG = LoggerFactory.getLogger(GuijingApplication.class);

	public static void main(String[] args) {
		LOG.info("项目启动");
		LOG.warn("警告：日志启动");
		SpringApplication.run(GuijingApplication.class, args);
	}

}
