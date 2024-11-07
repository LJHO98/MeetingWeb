package com.MeetingWeb.Util;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class RedisUtil {
    private final StringRedisTemplate template;

    //key로 조회
    public String getData(String key) {
        ValueOperations<String, String> valueOperations = template.opsForValue();
        return valueOperations.get(key);
    }
    //key 존재 여부 확인
    public boolean existData(String key) {
        return Boolean.TRUE.equals(template.hasKey(key));
    }

    //키 와 벨류의 만료시간 설정
    public void setDataExpire(String key, String value, long duration) {
        ValueOperations<String, String> valueOperations = template.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    //데이터 삭제
    public void deleteData(String key) {
        template.delete(key);
    }
}

