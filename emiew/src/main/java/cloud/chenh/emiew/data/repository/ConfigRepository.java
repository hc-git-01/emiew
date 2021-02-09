package cloud.chenh.emiew.data.repository;

import cloud.chenh.emiew.data.base.BaseRepository;
import cloud.chenh.emiew.data.entity.Config;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository extends BaseRepository<Config> {

    Config findFirstByKey(String key);

}
