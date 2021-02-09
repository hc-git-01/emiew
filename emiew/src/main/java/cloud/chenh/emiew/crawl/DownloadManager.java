package cloud.chenh.emiew.crawl;

import cloud.chenh.emiew.constants.DownloadConstants;
import cloud.chenh.emiew.data.entity.Download;
import cloud.chenh.emiew.data.entity.Image;
import cloud.chenh.emiew.data.service.DownloadService;
import cloud.chenh.emiew.data.service.ImageService;
import cloud.chenh.emiew.exception.IpBannedException;
import cloud.chenh.emiew.model.Book;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
public class DownloadManager {

    private final ExecutorService downloadPool = Executors.newFixedThreadPool(DownloadConstants.THREADS);
    private final List<Image> downloadingImages = new CopyOnWriteArrayList<>();

    @Autowired
    private BookCrawler bookCrawler;

    @Autowired
    private ImageCrawler imageCrawler;

    @Autowired
    private ImageService imageService;

    @Autowired
    private DownloadService downloadService;

    public void add(String url) throws IOException, IpBannedException {
        Book book = bookCrawler.getBook(url, 0);

        Download download = new Download();
        download.setUrl(url);
        download.setTitle(book.getTitle());
        download.setCoverUrl(book.getCoverUrl());
        download.setCover(downloadCover(download));
        download.setCategory(book.getCategory());
        download.setRating(book.getRating());
        download.setPages(book.getPages());
        download.setUploader(book.getUploader());
        download.setUploadTime(book.getUploadTime());
        download.setLanguage(book.getLanguage());
        download.setStatus(Download.Status.NORMAL);
        download = downloadService.save(download);

        List<Image> images = new ArrayList<>(download.getPages());
        for (int i = 0; i < download.getPages(); i++) {
            Image image = new Image();
            image.setBookUrl(url);
            image.setIndex(i);
            image.setDownload(download);
            images.add(image);
        }
        imageService.save(images);

        bookCrawler.clearCache(url);

        autoDownloadImage();
    }

    private String downloadCover(Download download) throws IOException {
        byte[] bytes = imageCrawler.getImageDirectly(download.getCoverUrl());

        String path = DownloadConstants.ROOT_DIR +
                download.getKey() +
                "/" +
                DownloadConstants.COVER_NAME +
                DownloadConstants.IMAGE_EXTENSION;

        FileUtils.writeByteArrayToFile(new File(path), bytes);

        return path;
    }

    public void pauseAll() {
        pause(downloadService.findByStatus(Download.Status.NORMAL));
    }

    public void pauseById(List<Long> ids) {
        pause(downloadService.findByIdAndStatus(ids, Download.Status.NORMAL));
    }

    public void pause(List<Download> downloads) {
        List<Image> images = downloads.parallelStream()
                .flatMap(download -> download.getImages().parallelStream())
                .collect(Collectors.toList());
        downloadingImages.removeAll(images);
        downloads.parallelStream()
                .filter(download -> !download.getFinished())
                .forEach(download -> download.setStatus(Download.Status.PAUSED));
        downloadService.save(downloads);
        autoDownloadImage();
    }

    public void resumeAll() {
        resume(downloadService.findAll());
    }

    public void resumeById(List<Long> ids) {
        resume(downloadService.findById(ids));
    }

    public void resume(List<Download> downloads) {
        downloads.parallelStream().forEach(download -> {
            download.getImages().parallelStream().forEach(image -> {
                if (StringUtils.isNotBlank(image.getPath()) && !new File(image.getPath()).exists()) {
                    image.setPath(null);
                }
            });
            download.setStatus(Download.Status.NORMAL);
        });
        downloadService.save(downloads);
        autoDownloadImage();
    }

    public void remove(List<Long> ids) {
        List<Download> downloads = downloadService.findById(ids);
        List<Image> images = downloads.parallelStream()
                .flatMap(download -> download.getImages().parallelStream())
                .collect(Collectors.toList());
        downloadingImages.removeAll(images);
        downloadService.delete(downloads);
        downloads.parallelStream().forEach(download -> bookCrawler.clearCache(download.getUrl()));
        autoDownloadImage();
    }

    @Scheduled(fixedRate = 60 * 1000L)
    @PostConstruct
    public void autoDownloadImage() {
        List<Image> images = imageService.getAllDownloadable();
        images.removeAll(downloadingImages);
        downloadingImages.addAll(images);
        images.forEach(image -> downloadPool.submit(() -> downloadImage(image)));
    }

    @Scheduled(fixedRate = 60 * 1000L)
    public void autoDeleteFile() {
        File rootDir = new File(DownloadConstants.ROOT_DIR);
        if (!rootDir.isDirectory()) {
            return;
        }
        File[] dirs = rootDir.listFiles();
        if (dirs == null || dirs.length <= 0) {
            return;
        }

        List<Download> downloads = downloadService.findAll();
        List<String> keys = downloads.parallelStream().map(Download::getKey).collect(Collectors.toList());

        for (File dir : dirs) {
            if (!keys.contains(dir.getName())) {
                FileUtils.deleteQuietly(dir);
            }
        }
    }

    private void downloadImage(Image image) {
        try {
            downloadImage(image, DownloadConstants.RETRY);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            downloadingImages.remove(image);
        }
    }

    private void downloadImage(Image image, Integer retry) {
        if (!downloadingImages.contains(image)) {
            return;
        }

        try {
            byte[] bytes = imageCrawler.getImageByIndex(image.getDownload().getUrl(), image.getIndex());
            String path = DownloadConstants.ROOT_DIR +
                    image.getDownload().getKey() +
                    "/" +
                    (image.getIndex() + 1) +
                    DownloadConstants.IMAGE_EXTENSION;
            FileUtils.writeByteArrayToFile(new File(path), bytes);
            image.setPath(path);
            imageService.saveWithCheck(image);
        } catch (IOException e) {
            if (retry > 0) {
                downloadImage(image, retry - 1);
            } else {
                onFail(image);
            }
            e.printStackTrace();
        } catch (IpBannedException e) {
            onFail(image);
        }
    }

    private void onFail(Image image) {
        downloadingImages.removeIf(i -> i.getDownload().getId().equals(image.getDownload().getId()));
        Download download = image.getDownload();
        download.setStatus(Download.Status.FAILED);
        downloadService.saveWithCheck(download);
    }

}
