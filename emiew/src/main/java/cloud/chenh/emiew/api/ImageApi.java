package cloud.chenh.emiew.api;

import cloud.chenh.emiew.crawl.ImageCrawler;
import cloud.chenh.emiew.exception.IpBannedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("image")
public class ImageApi {

    @Autowired
    private ImageCrawler imageCrawler;

    @GetMapping("direct")
    public void getImageDirectly(@RequestParam String url, HttpServletResponse response) throws IOException {
        StreamUtils.copy(imageCrawler.getImageDirectly(url), response.getOutputStream());
    }

    @GetMapping("cover")
    public void getCover(@RequestParam String url, HttpServletResponse response) throws IOException {
        StreamUtils.copy(imageCrawler.getCover(url), response.getOutputStream());
    }

    @GetMapping("by-index")
    public void getImageByIndex(
            @RequestParam String url,
            @RequestParam(required = false, defaultValue = "0") Integer index,
            HttpServletResponse response
    ) throws IOException, IpBannedException {
        StreamUtils.copy(imageCrawler.getImageByIndex(url, index), response.getOutputStream());
    }

}
