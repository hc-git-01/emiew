package cloud.chenh.emiew.data.service;

import cloud.chenh.emiew.data.base.BaseService;
import cloud.chenh.emiew.data.entity.Download;
import cloud.chenh.emiew.data.repository.DownloadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DownloadService extends BaseService<Download> {

    @Autowired
    private DownloadRepository downloadRepository;

    @Override
    public DownloadRepository getRepository() {
        return downloadRepository;
    }

    public Download findByUrl(String url) {
        return getRepository().findFirstByUrl(url);
    }

    public List<Download> findByUrl(List<String> urls) {
        return getRepository().findByUrlIn(urls);
    }

    public List<Download> findById(List<Long> ids) {
        return getRepository().findAllByIdIn(ids);
    }

    public Download findByCoverUrl(String coverUrl) {
        return getRepository().findFirstByCoverUrl(coverUrl);
    }

    public List<Download> findByIdAndStatus(List<Long> ids, Download.Status status) {
        return getRepository().findAllByIdInAndStatus(ids, status);
    }

    public List<Download> findByStatus(Download.Status status) {
        return getRepository().findAllByStatus(status);
    }

    public void saveWithCheck(Download download) {
        if (findById(download.getId()) != null) {
            save(download);
        }
    }

}
