package cloud.chenh.emiew.util;

import com.gargoylesoftware.htmlunit.util.NameValuePair;

import java.util.ArrayList;
import java.util.List;

public class CrawlParamsBuilder {

    private final List<NameValuePair> params = new ArrayList<>();

    private CrawlParamsBuilder() {
    }

    public static CrawlParamsBuilder create() {
        return new CrawlParamsBuilder();
    }

    public CrawlParamsBuilder add(String key, String val) {
        params.add(new NameValuePair(key, val));
        return this;
    }

    public List<NameValuePair> get() {
        return params;
    }

}
