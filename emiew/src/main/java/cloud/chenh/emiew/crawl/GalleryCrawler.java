package cloud.chenh.emiew.crawl;

import cloud.chenh.emiew.data.entity.Block;
import cloud.chenh.emiew.data.entity.Download;
import cloud.chenh.emiew.data.service.BlockService;
import cloud.chenh.emiew.data.service.ConfigService;
import cloud.chenh.emiew.data.service.DownloadService;
import cloud.chenh.emiew.exception.InvalidCookieException;
import cloud.chenh.emiew.exception.IpBannedException;
import cloud.chenh.emiew.model.Book;
import cloud.chenh.emiew.model.GalleryPage;
import cloud.chenh.emiew.util.CrawlParamsBuilder;
import cloud.chenh.emiew.util.EhUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GalleryCrawler {

    @Autowired
    private CrawlClient crawlClient;

    @Autowired
    private ConfigService configService;

    @Autowired
    private BlockService blockService;

    @Autowired
    private DownloadService downloadService;

    public GalleryPage getGallery(
            String searchValue,
            Integer categories,
            Integer rating,
            Integer minPages,
            Integer maxPages,
            Integer pageNumber
    ) throws IOException, IpBannedException, InvalidCookieException {

        Document document = crawlClient.connect(configService.getBaseUrl())
                .data(
                        CrawlParamsBuilder.create()
                                .add("page", pageNumber)
                                .add("f_cats", categories)
                                .add("f_search", searchValue)
                                .add("advsearch", 1)
                                .add("f_sname", "on")
                                .add("f_stags", "on")
                                .add("f_sdesc", "on")
                                .add("f_sr", "on")
                                .add("f_srdd", rating != null && rating > 1 ? rating : 0)
                                .add("f_sp", "on")
                                .add("f_spf", minPages)
                                .add("f_spt", maxPages)
                                .get()
                ).get();

        EhUtils.checkHtml(document);

        if (document.selectFirst(".ptt") == null) {
            return new GalleryPage(pageNumber, 0, new ArrayList<>());
        }

        Elements pageButtons = document.select(".ptt td");
        Integer nextPage = parseNextPage(pageButtons);
        int totalPages = Integer.parseInt(pageButtons.get(pageButtons.size() - 2).selectFirst("a").text());

        List<Book> books = document.select(".itg > tbody > tr").stream()
                .filter(tr -> tr.selectFirst(".gl1e") != null)
                .map(this::parseBook)
                .collect(Collectors.toList());

        List<String> downloads = downloadService.findByUrl(
                books.stream().map(Book::getUrl).collect(Collectors.toList())
        ).stream().map(Download::getUrl).collect(Collectors.toList());
        books.forEach(book -> book.setDownloaded(downloads.contains(book.getUrl())));

        removeBlocked(books);

        return new GalleryPage(pageNumber, nextPage, totalPages, books);
    }

    private Book parseBook(Element tr) {
        String url = tr.selectFirst(".gl1e a").attr("href");
        String title = tr.selectFirst(".glink").text();
        String coverUrl = tr.selectFirst(".gl1e img").attr("src");
        String category = tr.selectFirst(".cn").text();
        double rating = parseRating(tr.selectFirst(".ir"));
        int pages = Integer.parseInt(tr.select(".gl3e > div").get(4).text().split("\\s+")[0]);
        String uploader = tr.select(".gl3e > div").get(3).selectFirst("a").text();
        String uploadTime = tr.select(".gl3e > div").get(1).text();
        List<Book.TagGroup> tagGroups = parseTags(tr.select(".gl4e tr"));
        String language = getLanguageFromTags(tagGroups);

        Book book = new Book();
        book.setUrl(url);
        book.setTitle(title);
        book.setCoverUrl(coverUrl);
        book.setCategory(category);
        book.setRating(rating);
        book.setPages(pages);
        book.setUploader(uploader);
        book.setUploadTime(uploadTime);
        book.setTagGroups(tagGroups);
        book.setLanguage(language);

        return book;
    }

    private double parseRating(Element ir) {
        String style = ir.attr("style").split(":")[1].split(";")[0];

        int x = Integer.parseInt(style.split("px")[0]) / 16;
        int y = Integer.parseInt(style.split("px")[1].trim()) / 21;

        return 5 + x + (0.5 * y);
    }

    private List<Book.TagGroup> parseTags(Elements trs) {
        return trs.stream().map(tr -> {
            String groupName = tr.selectFirst(".tc").text().split(":")[0];
            List<String> tags = tr.select(".gt, .gtl, .gtw").stream().map(Element::text).collect(Collectors.toList());

            Book.TagGroup tagGroup = new Book.TagGroup();
            tagGroup.setName(groupName);
            tagGroup.setTags(tags);
            return tagGroup;
        }).collect(Collectors.toList());
    }

    private String getLanguageFromTags(List<Book.TagGroup> tagGroups) {
        return StringUtils.capitalize(tagGroups.stream()
                .filter(group -> "language".equals(group.getName()))
                .map(Book.TagGroup::getTags)
                .flatMap(Collection::stream)
                .findFirst()
                .orElse(null));
    }

    private void removeBlocked(List<Book> books) {
        List<Block> blocks = blockService.findAll();
        books.removeIf(book ->
                book.getTagGroups().stream().anyMatch(group ->
                        group.getTags().stream().anyMatch(
                                // tag block
                                tag -> blocks.stream().anyMatch(
                                        block -> group.getName().equals(block.getType()) &&
                                                tag.equals(block.getValue())
                                )
                        )
                ) || blocks.stream().anyMatch(
                        // uploader block
                        block -> "uploader".equals(block.getType()) &&
                                book.getUploader().equals(block.getValue())
                )
        );
    }

    private Integer parseNextPage(Elements tds) {
        for (int i = 0; i < tds.size(); i++) {
            if (tds.get(i).hasClass("ptds") && i + 1 < tds.size() - 1) {
                return Integer.parseInt(tds.get(i + 1).text()) - 1;
            }
        }
        return null;
    }

}
