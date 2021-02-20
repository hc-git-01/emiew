package cloud.chenh.emiew.api;

import cloud.chenh.emiew.crawl.ImageCrawler;
import cloud.chenh.emiew.exception.Image509Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("image")
public class ImageApi {

    @Autowired
    private ImageCrawler imageCrawler;

    @GetMapping("direct")
    public void getImageDirectly(@RequestParam String url, HttpServletResponse response) throws Exception {
        StreamUtils.copy(imageCrawler.getImageDirectly(url).getBytes(), response.getOutputStream());
    }

    @GetMapping("cover")
    public void getCover(@RequestParam String url, HttpServletResponse response) throws Exception {
        StreamUtils.copy(imageCrawler.getCover(url).getBytes(), response.getOutputStream());
    }

    @GetMapping("by-index")
    public void getImageByIndex(
            @RequestParam String url,
            @RequestParam(required = false, defaultValue = "0") Integer index,
            HttpServletResponse response
    ) throws Exception {
        try {
            StreamUtils.copy(imageCrawler.getImageByIndex(url, index).getBytes(), response.getOutputStream());
        } catch (Image509Exception e) {
            StreamUtils.copy(imageCrawler.get509().getBytes(), response.getOutputStream());
        }
    }

}
