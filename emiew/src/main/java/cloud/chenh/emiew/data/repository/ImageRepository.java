package cloud.chenh.emiew.data.repository;

import cloud.chenh.emiew.data.base.BaseRepository;
import cloud.chenh.emiew.data.entity.Download;
import cloud.chenh.emiew.data.entity.Image;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends BaseRepository<Image> {

    List<Image> findAllByPathNullAndDownload_Status(Download.Status status);

    Image findFirstByBookUrlAndIndex(String bookUrl, Integer index);

}
