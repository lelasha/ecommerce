package com.bog.ecommerce.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "config")
//@PropertySource(value = "classpath:application.properties", encoding = "UTF-8")
@Component
@Getter
@Setter
public class ConfigProperties {

    private double fee = 0.1;
    private String sendto;
    private String subject;
    private String text;
}
