package com.bog.ecommerce.service;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class MetricService {


    @Autowired
    private MeterRegistry registry;

    private List<String> statusList;


    public void increaseCount(final int status) {
        String counterName = "counter.status";
        registry.counter(counterName).increment(1);
        System.out.println(registry.summary("counter.status").totalAmount());
//        if (!statusList.contains(counterName)) {
//            statusList.add(counterName);
//        }
    }
}


