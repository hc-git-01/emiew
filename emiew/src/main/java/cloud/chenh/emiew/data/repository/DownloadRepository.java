package cloud.chenh.emiew.data.repository;

import cloud.chenh.emiew.data.base.BaseRepository;
import cloud.chenh.emiew.data.entity.Download;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DownloadRepository extends BaseRepository<Download> {

    List<Download> findAllByIdIn(List<Long> ids);

    List<Download> findAllByStatus(Download.Status status);

    List<Download> findAllByIdInAndStatus(List<Long> ids, Download.Status status);

    Download findFirstByUrl(String url);

    List<Download> findByUrlIn(List<String> url);

    Download findFirstByCoverUrl(String coverUrl);

}
