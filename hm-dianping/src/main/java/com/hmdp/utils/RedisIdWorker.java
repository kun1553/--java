package com.hmdp.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class RedisIdWorker {
    // 基准时间：2022-01-01 00:00:00 UTC。用它来压缩时间戳（必须取一个"过去"的时间点，否则 now-BEGIN 为负）
    private static final long BEGIN_TIMESTAMP = 1640995200L;
    private static final int COUNT_BITS =32;
    StringRedisTemplate stringRedisTemplate;

    public RedisIdWorker(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    public long nextId(String keyPrefix) {
        //1.生成时间戳
  LocalDateTime now = LocalDateTime.now();
  long nowsecond=now.toEpochSecond(ZoneOffset.UTC);
  long timestamp = nowsecond-BEGIN_TIMESTAMP;
        //2.生成序列号
String date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
long count =stringRedisTemplate.opsForValue().increment("icr:"+keyPrefix+":"+date);

        //3.拼接并返回
        return timestamp<<COUNT_BITS|count;
    }

}
