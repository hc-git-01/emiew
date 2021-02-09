package cloud.chenh.emiew.crawl;

import cloud.chenh.emiew.constants.ConfigConstants;
import cloud.chenh.emiew.constants.EhConstants;
import cloud.chenh.emiew.data.service.ConfigService;
import cloud.chenh.emiew.exception.InvalidCookieException;
import cloud.chenh.emiew.exception.IpBannedException;
import cloud.chenh.emiew.model.EhCookie;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class CrawlClient {

    private static final String BANNED_SIGN = "Your IP address has been temporarily banned";
    private static final String CHARSET_SIGN = "div";
    private static final Integer REQUEST_RETRY = 3;

    private WebClient client;

    @Autowired
    private ConfigService configService;

    @PostConstruct
    public void init() {
        if (client != null) {
            client.close();
        }

        client = buildClient();

        // extended mode
        addCookie("sl", "dm_2");
        // never warn
        addCookie("nw", "1");
        // login cookie
        EhCookie ehCookie = configService.getEhCookie();
        addCookie(ConfigConstants.IPB_PASS_HASH, ehCookie.getIpbPassHash());
        addCookie(ConfigConstants.IPB_MEMBER_ID, ehCookie.getIpbMemberId());

        log.info("Crawl client init finished");
    }

    public WebClient buildClient() {
        WebClient client = new WebClient(BrowserVersion.CHROME);
        client.getOptions().setJavaScriptEnabled(false);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setDownloadImages(false);
        client.getOptions().setThrowExceptionOnScriptError(false);
        client.getOptions().setPrintContentOnFailingStatusCode(false);
        client.getOptions().setTimeout(60000);

        String proxyHost = configService.getProxyHost();
        Integer proxyPort = configService.getProxyPort();
        if (StringUtils.isNotBlank(proxyHost) && proxyPort != null) {
            client.getOptions().getProxyConfig().setProxyHost(proxyHost);
            client.getOptions().getProxyConfig().setProxyPort(proxyPort);
        } else {
            try {
                System.setProperty("java.net.useSystemProxies", "true");
                List<Proxy> proxies = ProxySelector.getDefault().select(new URI(EhConstants.EH_URL));

                if (proxies != null && proxies.size() > 0 && proxies.get(0).address() != null) {
                    InetSocketAddress addr = (InetSocketAddress) proxies.get(0).address();
                    client.getOptions().getProxyConfig().setProxyHost(addr.getHostName());
                    client.getOptions().getProxyConfig().setProxyPort(addr.getPort());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return client;
    }

    public void addCookie(String domain, String name, String value) {
        client.getCookieManager().addCookie(new Cookie(domain, name, value));
    }

    public void addCookie(String name, String value) {
        addCookie(EhConstants.EH_DOMAIN, name, value);
        addCookie(EhConstants.EX_DOMAIN, name, value);
    }

    public Document getDocument(String url) throws IOException, IpBannedException, InvalidCookieException {
        return getDocument(new WebRequest(new URL(url)));
    }

    public Document getDocument(WebRequest request) throws IOException, IpBannedException, InvalidCookieException {
        return Jsoup.parse(getResponse(request));
    }

    public String getResponse(String url) throws IOException, IpBannedException, InvalidCookieException {
        return getResponse(new WebRequest(new URL(url)));
    }

    public String getResponse(WebRequest request) throws IOException, IpBannedException, InvalidCookieException {
        request.setCharset(StandardCharsets.UTF_8);

        for (int i = 0; i < REQUEST_RETRY; i++) {
            String response = client.getPage(request).getWebResponse().getContentAsString();
            if (response.contains(BANNED_SIGN)) {
                throw new IpBannedException();
            }
            if (configService.getEhCookie().loaded() && StringUtils.isBlank(response)) {
                throw new InvalidCookieException();
            }
            if (response.contains(CHARSET_SIGN)) {
                return response;
            } else {
                System.err.println(response);
            }
        }
        throw new IOException("Garbled html.");
    }

    public byte[] getBytes(String url) throws IOException {
        for (int i = 0; i < REQUEST_RETRY; i++) {
            try {
                return StreamUtils.copyToByteArray(client.getPage(url).getWebResponse().getContentAsStream());
            } catch (Throwable ignored) {
            }
        }
        throw new IOException("Cannot get response from " + url);
    }

    public Boolean proxyEnabled() {
        ProxyConfig proxyConfig = client.getOptions().getProxyConfig();
        return StringUtils.isNotBlank(proxyConfig.getProxyHost()) && proxyConfig.getProxyPort() > 0;
    }

}
