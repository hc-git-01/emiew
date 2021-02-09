package cloud.chenh.emiew.crawl;

import cloud.chenh.emiew.data.service.DownloadService;
import cloud.chenh.emiew.exception.InvalidCookieException;
import cloud.chenh.emiew.exception.IpBannedException;
import cloud.chenh.emiew.model.Book;
import cloud.chenh.emiew.util.CrawlParamsBuilder;
import com.gargoylesoftware.htmlunit.WebRequest;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BookCrawler {

    private static final String GET_BOOK_CACHE_NAME = "getBook";
    private static final String[] MONTHS = {
            "january",
            "february",
            "march",
            "april",
            "may",
            "june ",
            "july",
            "august",
            "september",
            "october",
            "november",
            "december"
    };

    @Autowired
    private CrawlClient crawlClient;

    @Autowired
    private DownloadService downloadService;

    @Autowired
    private CacheManager cacheManager;

    public void clearCache(String url) {
        CaffeineCache cache = (CaffeineCache) cacheManager.getCache(GET_BOOK_CACHE_NAME);
        if (cache == null) {
            return;
        }
        cache.getNativeCache()
                .asMap()
                .keySet()
                .parallelStream()
                .filter(key -> ((String) key).split("@")[0].equals(url))
                .forEach(cache::evict);
    }

    @Cacheable(value = GET_BOOK_CACHE_NAME, key = "#url + '@' + #pageNumber")
    public Book getBook(String url, Integer pageNumber) throws IOException, IpBannedException, InvalidCookieException {
        WebRequest request = new WebRequest(new URL(url));
        request.setRequestParameters(
                CrawlParamsBuilder.create()
                        .add("p", String.valueOf(pageNumber))
                        .add("hc", String.valueOf(pageNumber == 0 ? 1 : 0)) // show all comments
                        .add("inline_set", "ts_m") // normal thumbs
                        .get()
        );
        Document document = crawlClient.getDocument(request);

        Map<String, String> info = parseInfo(document.select("#gdd tr"));
        String parentUrl = parseParentUrl(document.selectFirst(".gdt2 a"));
        String title = document.selectFirst("#gn").text();
        String subtitle = document.selectFirst("#gj").text();
        String coverUrl = parseCoverUrl(document.selectFirst("#gd1 div"));
        String category = document.selectFirst(".cs").text();
        double rating = parseRating(document.selectFirst("#rating_label"));
        int pages = Integer.parseInt(info.get("Length").split("\\s+")[0]);
        String uploader = document.selectFirst("#gdn a").text();
        String uploadTime = info.get("Posted");
        String language = info.get("Language").split("\\s+")[0];
        List<Book.TagGroup> tagGroups = parseTags(document.select("#taglist tr"));
        List<Book.Comment> comments = parseComments(document.select(".c1"));
        List<String> thumbUrls = parseThumbs(document);
        List<String> imageUrls = document.select(".gdtm a,.gdtl a").eachAttr("href");

        Book book = new Book();
        book.setUrl(url);
        book.setParentUrl(parentUrl);
        book.setTitle(title);
        book.setSubtitle(subtitle);
        book.setCoverUrl(coverUrl);
        book.setCategory(category);
        book.setRating(rating);
        book.setPages(pages);
        book.setUploader(uploader);
        book.setUploadTime(uploadTime);
        book.setLanguage(language);
        book.setTagGroups(tagGroups);
        book.setComments(comments);
        book.setThumbUrls(thumbUrls);
        book.setImageUrls(imageUrls);
        book.setDownloaded(downloadService.findByUrl(url) != null);
        return book;
    }

    private String parseCoverUrl(Element gd1) {
        String style = gd1.attr("style");
        return style.split("[()]")[1];
    }

    private List<String> parseThumbs(Document document) {
        if (document.selectFirst(".gdtl img") != null) {
            return document.select(".gdtl img").eachAttr("src");
        } else {
            return Collections.singletonList(
                    document.selectFirst(".gdtm div").attr("style").split("[()]")[1]
            );
        }
    }

    private double parseRating(Element ratingLabel) {
        try {
            return Double.parseDouble(ratingLabel.text().split(":")[1].trim());
        } catch (Throwable throwable) {
            return 0;
        }
    }

    private Map<String, String> parseInfo(Elements trs) {
        Map<String, String> result = new HashMap<>();
        trs.parallelStream().forEach(tr -> {
            result.put(tr.selectFirst(".gdt1").text().split(":")[0], tr.selectFirst(".gdt2").text());
        });
        return result;
    }

    private String parseParentUrl(Element a) {
        try {
            return a.attr("href");
        } catch (Throwable throwable) {
            return null;
        }
    }

    private List<Book.TagGroup> parseTags(Elements trs) {
        return trs.stream().map(tr -> {
            String groupName = tr.selectFirst(".tc").text().split(":")[0];
            List<String> tags = tr.select(".gt a, .gtl a, .gtw a")
                    .stream()
                    .map(a -> a.text().split("\\|")[0].trim())
                    .collect(Collectors.toList());

            Book.TagGroup tagGroup = new Book.TagGroup();
            tagGroup.setName(groupName);
            tagGroup.setTags(tags);
            return tagGroup;
        }).collect(Collectors.toList());
    }

    private List<Book.Comment> parseComments(Elements c1s) {
        return c1s.stream().map(c1 -> {
            String publisher = c1.selectFirst(".c3 a").text();
            boolean isUploader = c1.selectFirst(".c4") != null;
            String publishTime = parsePublishTime(c1.selectFirst(".c3").text());
            int score = parseScore(c1.selectFirst(".c5 span"));
            String content = c1.selectFirst(".c6").html();

            Book.Comment comment = new Book.Comment();
            comment.setPublisher(publisher);
            comment.setIsUploader(isUploader);
            comment.setPublishTime(publishTime);
            comment.setScore(score);
            comment.setContent(content);

            return comment;
        }).collect(Collectors.toList());
    }

    private String parsePublishTime(String text) {
        try {
            String[] parts = text.split("\\s+");
            int date = Integer.parseInt(parts[2]);
            String month = parts[3].toLowerCase();
            int year = Integer.parseInt(parts[4].split(",")[0]);
            String time = parts[5];

            for (int i = 0; i < MONTHS.length; i++) {
                if (month.startsWith(MONTHS[i])) {
                    return String.format("%04d-%02d-%02d %s", year, i + 1, date, time);
                }
            }
        } catch (Throwable ignored) {
        }

        return "未知时间";
    }

    private int parseScore(Element c5span) {
        try {
            return Integer.parseInt(c5span.text());
        } catch (Throwable throwable) {
            return 0;
        }
    }

}
