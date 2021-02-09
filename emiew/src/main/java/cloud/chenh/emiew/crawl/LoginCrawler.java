package cloud.chenh.emiew.crawl;

import cloud.chenh.emiew.constants.ConfigConstants;
import cloud.chenh.emiew.constants.EhConstants;
import cloud.chenh.emiew.exception.CaptchaException;
import cloud.chenh.emiew.exception.IncorrectFormDataException;
import cloud.chenh.emiew.exception.IpBannedException;
import cloud.chenh.emiew.exception.MissingCookieException;
import cloud.chenh.emiew.model.EhCookie;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginCrawler {

    private static final String INCORRECT_FORM_DATA_FLAG = "Username or password incorrect";
    private static final String CAPTCHA_FLAG = "The captcha was not entered correctly";

    @Autowired
    private CrawlClient crawlClient;

    public EhCookie getCookie(
            String username, String password
    ) throws IOException, IncorrectFormDataException, CaptchaException, IpBannedException, MissingCookieException {

        WebClient client = crawlClient.buildClient();
        HtmlPage page = client.getPage(EhConstants.LOGIN_URL);
        HtmlForm form = page.getFormByName("ipb_login_form");
        form.getInputByName("UserName").setValueAttribute(username);
        form.getInputByName("PassWord").setValueAttribute(password);
        page = form.getInputByName("ipb_login_submit").click();

        String response = page.getWebResponse().getContentAsString();
        if (response.contains(INCORRECT_FORM_DATA_FLAG)) {
            throw new IncorrectFormDataException();
        }
        if (response.contains(CAPTCHA_FLAG)) {
            throw new CaptchaException();
        }

        Cookie ipbPassHash = client.getCookieManager().getCookie(ConfigConstants.IPB_PASS_HASH);
        Cookie ipbMemberId = client.getCookieManager().getCookie(ConfigConstants.IPB_MEMBER_ID);
        EhCookie ehCookie = new EhCookie(
                ipbPassHash == null ? null : ipbPassHash.getValue(),
                ipbMemberId == null ? null : ipbMemberId.getValue()
        );

        if (!ehCookie.loaded()) {
            throw new MissingCookieException();
        }

        return ehCookie;
    }


}
