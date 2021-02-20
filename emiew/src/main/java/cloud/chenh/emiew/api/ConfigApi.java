package cloud.chenh.emiew.api;

import cloud.chenh.emiew.crawl.CrawlClient;
import cloud.chenh.emiew.crawl.GithubCrawler;
import cloud.chenh.emiew.data.service.BlockService;
import cloud.chenh.emiew.data.service.ConfigService;
import cloud.chenh.emiew.model.OperationResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("config")
public class ConfigApi {

    @Autowired
    private ConfigService configService;

    @Autowired
    private BlockService blockService;

    @Autowired
    private CrawlClient crawlClient;

    @Autowired
    private GithubCrawler githubCrawler;

    @Value("${app.version}")
    private String appVersion;

    @GetMapping
    public OperationResult<?> get() {
        return OperationResult.ok(configService.get());
    }

    @GetMapping("cookie")
    public OperationResult<?> getCookie() {
        return OperationResult.ok(configService.getEhCookie());
    }

    @GetMapping("proxy")
    public OperationResult<?> getProxy() {
        Map<String, Object> result = new HashMap<>();
        result.put("host", configService.getProxyHost());
        result.put("port", configService.getProxyPort());
        return OperationResult.ok(result);
    }

    @PostMapping("proxy")
    public OperationResult<?> setProxy(
            @RequestParam(required = false) String host,
            @RequestParam(required = false) Integer port
    ) {
        configService.setProxyHost(host);
        configService.setProxyPort(port);
        crawlClient.init();
        return OperationResult.ok("已修改代理设置");
    }

    @GetMapping("display")
    public OperationResult<?> getForDisplay() {
        Map<String, Object> result = new HashMap<>();

        result.put("appVersion", appVersion);
        result.put("cookieLoaded", configService.getEhCookie().loaded());
        result.put("blockSize", blockService.findAll().size());
        result.put("proxySet",
                StringUtils.isNotBlank(configService.getProxyHost()) && configService.getProxyPort() != null
        );
        result.put("proxyEnabled", crawlClient.proxyEnabled());

        return OperationResult.ok(result);
    }

    @GetMapping("version/latest")
    public OperationResult<?> getLatestVersion() {
        try {
            return OperationResult.ok(githubCrawler.getLatestVersion());
        } catch (Exception e) {
            return OperationResult.no("网络请求失败");
        }
    }

}
