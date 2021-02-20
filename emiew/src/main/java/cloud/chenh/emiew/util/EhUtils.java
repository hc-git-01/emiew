package cloud.chenh.emiew.util;

import cloud.chenh.emiew.exception.InvalidCookieException;
import cloud.chenh.emiew.exception.IpBannedException;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class EhUtils {

    private static final String BANNED_SIGN = "Your IP address has been temporarily banned";
    private static final String HTML_SIGN = "div";

    public static void checkHtml(Document document) throws IpBannedException, InvalidCookieException, IOException {
        checkHtml(document.toString());
    }

    public static void checkHtml(String response) throws IpBannedException, InvalidCookieException, IOException {
        if (response.contains(BANNED_SIGN)) {
            throw new IpBannedException();
        }

        if (StringUtils.isBlank(response)) {
            throw new InvalidCookieException();
        }

        if (!response.contains(HTML_SIGN)) {
            throw new IOException("Bad html: \n" + response);
        }
    }

}
