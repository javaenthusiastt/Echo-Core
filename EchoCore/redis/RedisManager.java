package sorryplspls.EchoCore.redis;
import redis.clients.jedis.*;

public class RedisManager {

    private final JedisPool jedisPool;
    private Thread subscriberThread;
    private volatile boolean subscribed = false;

    public RedisManager(String host, int port, String password) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);

        HostAndPort hostAndPort = new HostAndPort(host, port);

        JedisClientConfig clientConfig = DefaultJedisClientConfig.builder()
                .password(password)
                .ssl(false)
                .timeoutMillis(2000)
                .build();

        this.jedisPool = new JedisPool(hostAndPort, clientConfig);
    }

    public void subscribe(String channel, JedisPubSub subscriber) {
        if (subscribed) return;

        subscriberThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try (Jedis jedis = jedisPool.getResource()) {
                    jedis.subscribe(subscriber, channel);
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ignored) {}
                }
            }
        });

        subscriberThread.setName("RedisSubscriberThread");
        subscriberThread.start();
        subscribed = true;
    }

    public void publish(String channel, String message) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(channel, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    public void del(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }

    public void setex(String key, int seconds, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, seconds, value);
        }
    }

    public void close() {
        if (subscriberThread != null) {
            subscriberThread.interrupt();
        }
        jedisPool.close();
    }
}
