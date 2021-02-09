package cloud.chenh.emiew.crawl;

import cloud.chenh.emiew.data.entity.Block;
import cloud.chenh.emiew.data.entity.Download;
import cloud.chenh.emiew.data.service.BlockService;
import cloud.chenh.emiew.data.service.ConfigService;
import cloud.chenh.emiew.data.service.DownloadService;
import cloud.chenh.emiew.exception.IpBannedException;
import cloud.chenh.emiew.model.Book;
import cloud.chenh.emiew.model.CustomPage;
import cloud.chenh.emiew.util.CrawlParamsBuilder;
import com.gargoylesoftware.htmlunit.WebRequest;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
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

    public CustomPage<Book> getGallery(
            String searchValue,
            Integer categories,
            Integer rating,
            Integer minPages,
            Integer maxPages,
            Integer pageNumber
    ) throws IOException, IpBannedException {

        WebRequest request = new WebRequest(new URL(configService.getBaseUrl()));
        request.setRequestParameters(
                CrawlParamsBuilder.create()
                        .add("page", String.valueOf(pageNumber))
                        .add("f_cats", String.valueOf(categories))
                        .add("f_search", searchValue)
                        .add("advsearch", String.valueOf(1))
                        .add("f_sname", "on")
                        .add("f_stags", "on")
                        .add("f_sdesc", "on")
                        .add("f_sr", "on")
                        .add("f_srdd", String.valueOf(rating > 1 ? rating : 0))
                        .add("f_sp", "on")
                        .add("f_spf", String.valueOf(minPages))
                        .add("f_spt", String.valueOf(maxPages))
                        .get()
        );

        Document document = crawlClient.getDocument(request);

        if (document.selectFirst(".ptt") == null) {
            return new CustomPage<>(pageNumber, 0, new ArrayList<>());
        }

        Elements pageButtons = document.select(".ptt td");
        int totalPages = Integer.parseInt(pageButtons.get(pageButtons.size() - 2).selectFirst("a").text());

        List<Book> books = document.select(".itg > tbody > tr").stream()
                .filter(tr -> tr.selectFirst(".gl1e") != null)
                .map(this::parseBook)
                .collect(Collectors.toList());

        List<String> downloads = downloadService.findByUrl(
                books.parallelStream().map(Book::getUrl).collect(Collectors.toList())
        ).parallelStream().map(Download::getUrl).collect(Collectors.toList());
        books.parallelStream().forEach(book -> book.setDownloaded(downloads.contains(book.getUrl())));

        removeBlocked(books);

        return new CustomPage<>(pageNumber, totalPages, books);
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
        return StringUtils.capitalize(tagGroups.parallelStream()
                .filter(group -> "language".equals(group.getName()))
                .map(Book.TagGroup::getTags)
                .flatMap(Collection::stream)
                .findFirst()
                .orElse(null));
    }

    private void removeBlocked(List<Book> books) {
        List<Block> blocks = blockService.findAll();
        books.removeIf(book ->
                book.getTagGroups().parallelStream().anyMatch(group ->
                        group.getTags().parallelStream().anyMatch(
                                // tag block
                                tag -> blocks.parallelStream().anyMatch(
                                        block -> group.getName().equals(block.getType()) &&
                                                tag.equals(block.getValue())
                                )
                        )
                ) || blocks.parallelStream().anyMatch(
                        // uploader block
                        block -> "uploader".equals(block.getType()) &&
                                book.getUploader().equals(block.getValue())
                )
        );
    }

}
