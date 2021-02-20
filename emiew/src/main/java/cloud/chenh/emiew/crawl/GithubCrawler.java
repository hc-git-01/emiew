package cloud.chenh.emiew.crawl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GithubCrawler {

    public static final String RELEASES_URL = "https://api.github.com/repos/hc-git-01/emiew/releases";

    @Autowired
    private CrawlClient crawlClient;

    public String getLatestVersion() throws Exception {
        String response = crawlClient.connectAnonymously(RELEASES_URL).execute().body();
        JSONArray releases = JSONObject.parseArray(response);
        return releases.getJSONObject(0).getString("tag_name");
    }

}
