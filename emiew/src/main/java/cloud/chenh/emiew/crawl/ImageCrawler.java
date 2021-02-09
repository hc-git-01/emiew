package cloud.chenh.emiew.crawl;

import cloud.chenh.emiew.constants.EhConstants;
import cloud.chenh.emiew.data.entity.Download;
import cloud.chenh.emiew.data.entity.Image;
import cloud.chenh.emiew.data.service.DownloadService;
import cloud.chenh.emiew.data.service.ImageService;
import cloud.chenh.emiew.exception.Image509Exception;
import cloud.chenh.emiew.exception.InvalidCookieException;
import cloud.chenh.emiew.exception.IpBannedException;
import cloud.chenh.emiew.model.ImageFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class ImageCrawler {

    private static final String GET_IMAGE_CACHE_NAME = "getImage";
    private static final String GET_COVER_CACHE_NAME = "getCover";

    @Autowired
    private CrawlClient crawlClient;

    @Autowired
    private BookCrawler bookCrawler;

    @Autowired
    private DownloadService downloadService;

    @Autowired
    private ImageService imageService;

    @Cacheable(value = GET_IMAGE_CACHE_NAME, key = "#url")
    public ImageFile getImageDirectly(String url) throws IOException, Image509Exception {
        if (FilenameUtils.getBaseName(url).startsWith(EhConstants.IMAGE_509_PREFIX)) {
            throw new Image509Exception();
        }
        return new ImageFile(
                FilenameUtils.getName(url),
                crawlClient.getBytes(url)
        );
    }

    @Cacheable(value = GET_COVER_CACHE_NAME, key = "#url")
    public ImageFile getCover(String url) throws IOException, Image509Exception {
        if (FilenameUtils.getBaseName(url).startsWith(EhConstants.IMAGE_509_PREFIX)) {
            throw new Image509Exception();
        }
        Download download = downloadService.findByCoverUrl(url);
        if (download != null && StringUtils.isNotBlank(download.getCover()) && new File(download.getCover()).exists()) {
            return new ImageFile(
                    FilenameUtils.getName(url),
                    FileUtils.readFileToByteArray(new File(download.getCover()))
            );
        }
        return new ImageFile(
                FilenameUtils.getName(url),
                crawlClient.getBytes(url)
        );
    }

    @Cacheable(value = GET_IMAGE_CACHE_NAME, key = "#url + '@' + #index")
    public ImageFile getImageByIndex(String url, Integer index)
            throws IOException, IpBannedException, Image509Exception, InvalidCookieException {

        Image image = imageService.findByBookAndIndex(url, index);
        if (image != null && StringUtils.isNotBlank(image.getPath()) && new File(image.getPath()).exists()) {
            return new ImageFile(
                    FilenameUtils.getName(image.getPath()),
                    FileUtils.readFileToByteArray(new File(image.getPath()))
            );
        }

        String pageUrl = bookCrawler
                .getBook(url, index / EhConstants.NORMAL_THUMB_PAGE_SIZE)
                .getImageUrls()
                .get(index % EhConstants.NORMAL_THUMB_PAGE_SIZE);
        Document document = crawlClient.getDocument(pageUrl);
        return getImageDirectly(document.selectFirst("#img").attr("src"));
    }

}
