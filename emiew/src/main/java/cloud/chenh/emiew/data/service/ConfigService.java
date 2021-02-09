package cloud.chenh.emiew.data.service;

import cloud.chenh.emiew.constants.ConfigConstants;
import cloud.chenh.emiew.constants.EhConstants;
import cloud.chenh.emiew.crawl.CrawlClient;
import cloud.chenh.emiew.data.base.BaseService;
import cloud.chenh.emiew.data.entity.Config;
import cloud.chenh.emiew.data.repository.ConfigRepository;
import cloud.chenh.emiew.model.EhCookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ConfigService extends BaseService<Config> {

    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private CrawlClient crawlClient;

    @Override
    public ConfigRepository getRepository() {
        return configRepository;
    }

    public void set(String key, String val) {
        Config config = getRepository().findFirstByKey(key);
        if (config == null) {
            config = new Config(key, val);
        }
        config.setValue(val);
        save(config);
    }

    public void setInt(String key, Integer val) {
        String valStr = val == null ? null : String.valueOf(val);
        set(key, valStr);
    }

    public Map<String, Object> get() {
        return findAll().parallelStream()
                .collect(Collectors.toMap(Config::getKey, Config::getValue));
    }

    public String get(String key) {
        Config config = getRepository().findFirstByKey(key);
        return config == null ? null : config.getValue();
    }

    public Integer getInt(String key) {
        String val = get(key);
        return val == null ? null : Integer.valueOf(val);
    }

    public String getBaseUrl() {
        return getEhCookie().loaded() ? EhConstants.EX_URL : EhConstants.EH_URL;
    }

    public String getProxyHost() {
        return get(ConfigConstants.PROXY_HOST);
    }

    public void setProxyHost(String host) {
        set(ConfigConstants.PROXY_HOST, host);
    }

    public Integer getProxyPort() {
        return getInt(ConfigConstants.PROXY_PORT);
    }

    public void setProxyPort(Integer port) {
        setInt(ConfigConstants.PROXY_PORT, port);
    }

    public void setEhCookie(String ipbPassHash, String ipbMemberId) {
        set(ConfigConstants.IPB_PASS_HASH, ipbPassHash);
        set(ConfigConstants.IPB_MEMBER_ID, ipbMemberId);

        crawlClient.init();
    }

    public EhCookie getEhCookie() {
        return new EhCookie(get(ConfigConstants.IPB_PASS_HASH), get(ConfigConstants.IPB_MEMBER_ID));
    }

}
