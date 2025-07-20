package com.study.tony.wrench.ratelimiter;

import com.study.tony.wrench.ratelimiter.types.annotations.RateLimiterAccessInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/index/")
public class IndexController {

    /**
     * curl --request GET \
     *   --url 'http://127.0.0.1:9191/api/v1/index/draw?userId=xiaofuge'
     */
    @RateLimiterAccessInterceptor(key = "userId", fallbackMethod = "drawErrorRateLimiter", permitsPerSecond = 1.0d, blacklistCount = 1)
    @RequestMapping(value = "draw", method = RequestMethod.GET)
    public String draw(String userId) {
        return "test" + new Random().nextInt(100);
    }

    public String drawErrorRateLimiter(String userId) {
        return "rateLimiter";
    }

}
