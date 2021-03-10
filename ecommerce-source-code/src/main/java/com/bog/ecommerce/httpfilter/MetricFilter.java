package com.bog.ecommerce.httpfilter;

import com.bog.ecommerce.service.MetricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.*;

@Component
public class MetricFilter implements Filter {

    @Autowired
    private MetricService metricService;
    @Autowired
    private StringRedisTemplate rtpl;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws java.io.IOException, ServletException {
        rtpl.opsForSet().add("user:set:day",request.getRemoteAddr());
        chain.doFilter(request, response);
        //int status = ((HttpServletResponse) response).getStatus();
       // metricService.increaseCount(status);
    }
}
