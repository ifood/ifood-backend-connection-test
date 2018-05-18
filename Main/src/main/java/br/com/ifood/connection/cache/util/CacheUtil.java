package br.com.ifood.connection.cache.util;

public class CacheUtil {

    public static String buildStatusCacheKey(Long restaurantId) {
        return String.format("ifood/restaurant/%d", restaurantId);
    }

}
