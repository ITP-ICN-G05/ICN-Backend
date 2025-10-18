package com.gof.ICNBack.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

@Configuration
public class FreemarkerConfig {

    @Bean
    public FreeMarkerConfigurationFactoryBean freemarkerConfiguration() {
        FreeMarkerConfigurationFactoryBean bean = new FreeMarkerConfigurationFactoryBean();
        bean.setTemplateLoaderPath("classpath:/");
        return bean;
    }

    @Bean
    public freemarker.template.Configuration configuration(
            FreeMarkerConfigurationFactoryBean factoryBean) throws Exception {
        return factoryBean.createConfiguration();
    }
}
