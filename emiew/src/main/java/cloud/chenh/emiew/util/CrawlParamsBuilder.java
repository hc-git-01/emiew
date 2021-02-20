package cloud.chenh.emiew.util;

import java.util.HashMap;
import java.util.Map;

public class CrawlParamsBuilder {

    private final Map<String, String> params = new HashMap<>();

    private CrawlParamsBuilder() {
    }

    public static CrawlParamsBuilder create() {
        return new CrawlParamsBuilder();
    }

    public CrawlParamsBuilder add(String key, Object val) {
        if (key != null && val != null) {
            params.put(key, String.valueOf(val));
        }
        return this;
    }

    public Map<String, String> get() {
        return params;
    }

}
