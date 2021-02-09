package cloud.chenh.emiew.api;

import cloud.chenh.emiew.crawl.BookCrawler;
import cloud.chenh.emiew.crawl.CrawlClient;
import cloud.chenh.emiew.crawl.GalleryCrawler;
import cloud.chenh.emiew.exception.InvalidCookieException;
import cloud.chenh.emiew.exception.IpBannedException;
import cloud.chenh.emiew.model.OperationResult;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("crawl")
public class CrawlApi {

    @Autowired
    private GalleryCrawler galleryCrawler;

    @Autowired
    private BookCrawler bookCrawler;

    @Autowired
    private CrawlClient crawlClient;

    @GetMapping("gallery")
    public OperationResult<?> getGallery(
            @RequestParam(required = false, defaultValue = "") String searchValue,
            @RequestParam(required = false, defaultValue = "0") Integer categories,
            @RequestParam(required = false, defaultValue = "1") Integer rating,
            @RequestParam(required = false, defaultValue = "0") Integer minPages,
            @RequestParam(required = false, defaultValue = "9999") Integer maxPages,
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber
    ) {
        try {
            return OperationResult.ok(
                    galleryCrawler.getGallery(searchValue, categories, rating, minPages, maxPages, pageNumber)
            );
        } catch (IOException e) {
            e.printStackTrace();
            return OperationResult.no("网络请求失败");
        } catch (IpBannedException e) {
            return OperationResult.no("本IP已被暂时封禁");
        } catch (InvalidCookieException e) {
            return OperationResult.no("该Cookie无法访问ExHentai");
        }
    }

    @GetMapping("book")
    public OperationResult<?> getBook(
            @RequestParam String url,
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber
    ) {
        try {
            return OperationResult.ok(
                    bookCrawler.getBook(url, pageNumber)
            );
        } catch (IpBannedException e) {
            return OperationResult.no("本IP已被暂时封禁");
        } catch (FailingHttpStatusCodeException e) {
            e.printStackTrace();
            return OperationResult.no("找不到该本子");
        } catch (IOException e) {
            e.printStackTrace();
            return OperationResult.no("网络请求失败");
        } catch (InvalidCookieException e) {
            return OperationResult.no("该Cookie无法访问ExHentai");
        }
    }

    @GetMapping("book/refresh")
    public OperationResult<?> refreshBook(@RequestParam String url) {
        bookCrawler.clearCache(url);
        return OperationResult.ok("已刷新");
    }

    @GetMapping("reload")
    public OperationResult<?> reload() {
        crawlClient.init();
        return OperationResult.ok("已重置爬虫客户端");
    }

}
