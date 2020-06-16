package com.cloud.common.redis.config;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author hugh
 */
@Configuration
public class RedisConfig {

    /**
     * 注入RedisProperties
     */
    @Autowired
    RedisProperties redisProperties;

    /**
     * lettuce 连接工厂
     *
     * @return RedisConnectionFactory
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory factory;
        RedisStandaloneConfiguration redisStandaloneConfiguration;
        RedisSentinelConfiguration redisSentinelConfiguration;
        // 0. 获取Lettuce的连接池配置
        LettucePoolingClientConfiguration lettucePoolConfig = this.lettucePoolConfig();
        // 1. 获取单机的配置
        redisStandaloneConfiguration = this.redisStandaloneConfiguration();
        // 2.获取哨兵模式的配置
        redisSentinelConfiguration = this.redisSentinelConfiguration();
        // 3. 哨兵配置为空时，并创建单机Lettuce连接工厂
        if (redisSentinelConfiguration == null) {
            factory = new LettuceConnectionFactory(redisStandaloneConfiguration, lettucePoolConfig);
        } else {
            // 哨兵模式
            factory = new LettuceConnectionFactory(redisSentinelConfiguration, lettucePoolConfig);
        }

        return factory;
    }

    /**
     * 实例化 RedisTemplate 对象
     *
     * @return RedisTemplate<String, Object>
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<Object>(Object.class);
        redisTemplate.setHashValueSerializer(fastJsonRedisSerializer);
        redisTemplate.setValueSerializer(fastJsonRedisSerializer);
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        // 设置白名单---非常重要********
        ParserConfig.getGlobalInstance().addAccept("com.cloud");
        return redisTemplate;
    }

    /**
     * 实例化 HashOperations 对象,可以使用 Hash 类型操作
     *
     * param redisTemplate
     * @return HashOperations<String, String, Object>
     */
    @Bean
    public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    /**
     * 实例化 ValueOperations 对象,可以使用 String 操作
     *
     * param redisTemplate
     * @return ValueOperations<String, Object>
     */
    @Bean
    public ValueOperations<String, Object> valueOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    /**
     * 实例化 ListOperations 对象,可以使用 List 操作
     *
     * param redisTemplate
     * @return ListOperations<String, Object>
     */
    @Bean
    public ListOperations<String, Object> listOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForList();
    }

    /**
     * 实例化 SetOperations 对象,可以使用 Set 操作
     *
     * param redisTemplate
     * @return SetOperations<String, Object>
     */
    @Bean
    public SetOperations<String, Object> setOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    /**
     * 实例化 ZSetOperations 对象,可以使用 ZSet 操作
     *
     * param redisTemplate
     * @return ZSetOperations<String, Object>
     */
    @Bean
    public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForZSet();
    }


    /**
     * redis哨兵信息配置
     *
     * @return RedisSentinelConfiguration
     */
    private RedisSentinelConfiguration redisSentinelConfiguration() {
        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration();
        RedisProperties.Sentinel sentinel = redisProperties.getSentinel();
        String master = sentinel.getMaster();
        // 根据配置文件里面的master判断是否配置了哨兵
        if (master != null && !"".equals(master)) {
            sentinelConfig.setMaster(master);
            Set<RedisNode> sentinels = new HashSet<>();
            List<String> nodes = sentinel.getNodes();
            for (String redisHost : nodes) {
                String[] item = redisHost.split(":");
                String ip = item[0].trim();
                String port = item[1].trim();
                sentinels.add(new RedisNode(ip, Integer.parseInt(port)));
            }
            sentinelConfig.setSentinels(sentinels);
            sentinelConfig.setDatabase(redisProperties.getDatabase());
            //redis 密码
            sentinelConfig.setPassword(RedisPassword.of(redisProperties.getPassword()));
            return sentinelConfig;
        } else {
            return null;
        }

    }

    /**
     * redis 单节点信息配置
     *
     * @return RedisStandaloneConfiguration
     */
    private RedisStandaloneConfiguration redisStandaloneConfiguration() {
        RedisStandaloneConfiguration standConfig = new RedisStandaloneConfiguration();
        standConfig.setHostName(redisProperties.getHost());
        standConfig.setPort(redisProperties.getPort());
        standConfig.setDatabase(redisProperties.getDatabase());
        //redis 密码
        standConfig.setPassword(RedisPassword.of(redisProperties.getPassword()));
        return standConfig;
    }

    /**
     * lettuce 连接池配置
     *
     * @return LettucePoolingClientConfiguration
     */
    private LettucePoolingClientConfiguration lettucePoolConfig() {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();

        poolConfig.setMaxIdle(redisProperties.getLettuce().getPool().getMaxIdle());
        poolConfig.setMinIdle(redisProperties.getLettuce().getPool().getMinIdle());
        poolConfig.setMaxTotal(redisProperties.getLettuce().getPool().getMaxActive());
        poolConfig.setMaxWaitMillis(redisProperties.getLettuce().getPool().getMaxWait().toMillis());
        return LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig)
                .build();
    }

}
