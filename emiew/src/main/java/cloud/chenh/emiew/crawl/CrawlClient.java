package cloud.chenh.emiew.crawl;

import cloud.chenh.emiew.constants.ConfigConstants;
import cloud.chenh.emiew.constants.EhConstants;
import cloud.chenh.emiew.data.service.ConfigService;
import cloud.chenh.emiew.model.EhCookie;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CrawlClient {

    private String proxyHost;
    private Integer proxyPort;
    private final Map<String, String> cookies = new HashMap<>();

    @Autowired
    private ConfigService configService;

    public Connection connect(String url) {
        return connectAnonymously(url).cookies(cookies);
    }

    public Connection connectAnonymously(String url) {
        Connection connection = Jsoup.connect(url)
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                .timeout(60000);

        if (StringUtils.isNotBlank(proxyHost) && proxyPort != null) {
            connection.proxy(proxyHost, proxyPort);
        }

        return connection;
    }

    @PostConstruct
    public void init() {
        try {
            String proxyHost = configService.getProxyHost();
            Integer proxyPort = configService.getProxyPort();
            if (StringUtils.isNotBlank(proxyHost) && proxyPort != null) {
                this.proxyHost = proxyHost;
                this.proxyPort = proxyPort;
            } else {
                System.setProperty("java.net.useSystemProxies", "true");
                List<Proxy> proxies = ProxySelector.getDefault().select(new URI(EhConstants.EH_URL));

                if (proxies != null && proxies.size() > 0 && proxies.get(0).address() != null) {
                    InetSocketAddress addr = (InetSocketAddress) proxies.get(0).address();
                    this.proxyHost = addr.getHostName();
                    this.proxyPort = addr.getPort();
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        cookies.clear();
        // extended mode
        cookies.put("sl", "dm_2");
        // never warn
        cookies.put("nw", "1");
        // ex
        EhCookie ehCookie = configService.getEhCookie();
        cookies.put(ConfigConstants.IPB_PASS_HASH, ehCookie.getIpbPassHash());
        cookies.put(ConfigConstants.IPB_MEMBER_ID, ehCookie.getIpbMemberId());
        cookies.put(ConfigConstants.IGNEOUS, ehCookie.getIgneous());

        log.info("Crawl client init finished");
    }

    public Boolean proxyEnabled() {
        return StringUtils.isNotBlank(proxyHost) && proxyPort != null;
    }

}
