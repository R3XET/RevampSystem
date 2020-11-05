package eu.revamp.system.database.redis.other.publisher;


import com.google.gson.JsonObject;
import eu.revamp.system.plugin.RevampSystem;
import lombok.RequiredArgsConstructor;
import eu.revamp.system.database.redis.other.settings.JedisSettings;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RequiredArgsConstructor
public class JedisPublisher {

    private final RevampSystem plugin = RevampSystem.INSTANCE;

    private JedisSettings jedisSettings;

    public JedisPublisher(JedisSettings settings) {
        this.jedisSettings = settings;
    }

    public void write(String channel, JsonObject payload) {
        JedisPool pool = plugin.getRedisData().getPool();
        if (pool == null) return;

        try (Jedis jedis = plugin.getRedisData().getPool().getResource()) {

            if (plugin.getRedisData().getSettings().hasPassword()) {
                jedis.auth(plugin.getRedisData().getSettings().getPassword());
            }

            jedis.publish(channel, payload.toString());
        }
    }
}
