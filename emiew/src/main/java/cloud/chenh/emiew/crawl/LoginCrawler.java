package cloud.chenh.emiew.crawl;

import cloud.chenh.emiew.constants.ConfigConstants;
import cloud.chenh.emiew.constants.EhConstants;
import cloud.chenh.emiew.exception.*;
import cloud.chenh.emiew.model.EhCookie;
import cloud.chenh.emiew.util.EhUtils;
import org.jsoup.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginCrawler {

    private static final String INCORRECT_FORM_DATA_FLAG = "Username or password incorrect";
    private static final String CAPTCHA_FLAG = "The captcha was not entered correctly";

    @Autowired
    private CrawlClient crawlClient;


    public EhCookie getCookie(String username, String password) throws
            IOException,
            IncorrectFormDataException,
            CaptchaException,
            IpBannedException,
            CookieNotFoundException,
            InvalidCookieException {

        Connection.Response response = crawlClient.connectAnonymously(EhConstants.LOGIN_URL)
                .data("CookieDate", "1")
                .data("b", "d")
                .data("bt", "1-1")
                .data("UserName", username)
                .data("PassWord", password)
                .data("ipb_login_submit", "Login!")
                .method(Connection.Method.POST)
                .execute();
        EhUtils.checkHtml(response.body());

        if (response.body().contains(INCORRECT_FORM_DATA_FLAG)) {
            throw new IncorrectFormDataException();
        }
        if (response.body().contains(CAPTCHA_FLAG)) {
            throw new CaptchaException();
        }

        String ipbPassHash = response.cookie(ConfigConstants.IPB_PASS_HASH);
        String ipbMemberId = response.cookie(ConfigConstants.IPB_MEMBER_ID);

        response = crawlClient.connectAnonymously(EhConstants.EX_URL).cookies(response.cookies()).execute();
        EhUtils.checkHtml(response.body());

        String igneous = response.cookie(ConfigConstants.IGNEOUS);

        EhCookie ehCookie = new EhCookie(ipbPassHash, ipbMemberId, igneous);
        if (!ehCookie.loaded()) {
            throw new CookieNotFoundException();
        }
        return ehCookie;
    }


}
