package com.MeetingWeb.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
//이 애너테이션은 Spring Data Redis의 리포지토리 기능을 활성화합니다.
// 즉, Redis를 사용하는 레포지토리를 생성할 수 있도록 지원합니다.
@EnableRedisRepositories
    public class RedisConfig {
    //@Value 애너테이션은 application.properties 파일에 정의된 값을 주입하는 데 사용됩니다.
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    //Redis와의 연결을 생성하는 RedisConnectionFactory Bean
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        //Redis 서버와의 연결을 처리합니다. 생성 시, 주입된 호스트(redisHost)와 포트(redisPort)를 사용
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    //RedisTemplate는 Redis 데이터베이스와 상호작용하는 템플릿입니다. 주로 Redis에서 데이터를 읽고 쓰는 데 사용됩니다.
    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        //RedisTemplate<byte[], byte[]>로 정의되었으며, 이는 키와 값을 바이트 배열로 저장하도록 설정한 것입니다.
        // 이 템플릿은 RedisConnectionFactory를 통해 Redis에 연결합니다.
        RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory((redisConnectionFactory()));
        return redisTemplate;
    }
}
