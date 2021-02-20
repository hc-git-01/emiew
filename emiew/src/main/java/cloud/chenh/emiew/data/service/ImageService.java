package cloud.chenh.emiew.data.service;

import cloud.chenh.emiew.data.base.BaseService;
import cloud.chenh.emiew.data.entity.Download;
import cloud.chenh.emiew.data.entity.Image;
import cloud.chenh.emiew.data.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageService extends BaseService<Image> {

    @Autowired
    private ImageRepository imageRepository;

    @Override
    public ImageRepository getRepository() {
        return imageRepository;
    }

    public List<Image> getAllDownloadable() {
        return getRepository().findAllByPathNullAndDownload_Status(Download.Status.NORMAL);
    }

    public Image findByBookAndIndex(String bookUrl, Integer index) {
        return getRepository().findFirstByBookUrlAndIndex(bookUrl, index);
    }

}
