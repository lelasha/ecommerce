package com.bog.ecommerce.schedule;

import com.bog.ecommerce.config.ConfigProperties;
import com.bog.ecommerce.service.EmailService;
import com.bog.ecommerce.service.ExcelService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Slf4j
public class ExcelScheduler {

    @Autowired
    private ExcelService excelService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ConfigProperties configProperties;
    @Autowired
    private MeterRegistry registry;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final static String CRON = "00 55 23 * * *";
    private final static String CRON_TEST = "30 24 18 * * *";


    @Scheduled(cron = CRON)
    public void analyticsToEmailScheduler(){
        try {
            Long viewSize = stringRedisTemplate.opsForSet().size("user:set:day");
            if (viewSize == null) viewSize = 0L;
            Long authSize = stringRedisTemplate.opsForSet().size("auth:success");
            if (authSize == null) authSize = 0L;
            stringRedisTemplate.delete("user:set:day");
            stringRedisTemplate.delete("auth:success");
            emailService.sendSimpleMessage(
                    configProperties.getSendto(),
                    configProperties.getSubject(),
                    configProperties.getText(),
                    excelService.createXLS(viewSize,authSize));
            log.info("email has been sent successfully " + Instant.now());
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


}
