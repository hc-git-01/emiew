package cloud.chenh.emiew.api;

import cloud.chenh.emiew.crawl.LoginCrawler;
import cloud.chenh.emiew.data.service.ConfigService;
import cloud.chenh.emiew.exception.*;
import cloud.chenh.emiew.model.OperationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("login")
public class LoginApi {

    @Autowired
    private ConfigService configService;

    @Autowired
    private LoginCrawler loginCrawler;

    @PostMapping
    public OperationResult<?> login(@RequestParam String username, @RequestParam String password) {
        try {
            return OperationResult.ok(loginCrawler.getCookie(username, password));
        } catch (IncorrectFormDataException e) {
            return OperationResult.no("不正确的用户名或密码");
        } catch (CaptchaException e) {
            return OperationResult.no("需要人机验证，请尝试更换IP或直接填写Cookie");
        } catch (IpBannedException e) {
            return OperationResult.no("本IP已被暂时封禁");
        } catch (CookieNotFoundException | InvalidCookieException e) {
            return OperationResult.no("获取Cookie失败，请重试或直接填写Cookie");
        } catch (Exception e) {
            e.printStackTrace();
            return OperationResult.no("网络请求失败");
        }
    }

    @GetMapping("cookie")
    public OperationResult<?> getCookie() {
        return OperationResult.ok(configService.getEhCookie());
    }

    @PostMapping("cookie")
    public OperationResult<?> setCookie(
            @RequestParam(required = false) String ipbPassHash,
            @RequestParam(required = false) String ipbMemberId,
            @RequestParam(required = false) String igneous
    ) {
        configService.setEhCookie(ipbPassHash, ipbMemberId, igneous);
        return OperationResult.ok("保存成功");
    }

}
