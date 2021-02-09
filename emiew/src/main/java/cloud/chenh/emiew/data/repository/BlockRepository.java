package cloud.chenh.emiew.data.repository;

import cloud.chenh.emiew.data.base.BaseRepository;
import cloud.chenh.emiew.data.entity.Block;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockRepository extends BaseRepository<Block> {

    Block findFirstByTypeAndValue(String type, String value);

    List<Block> findAllByOrderByTypeAsc();

}
