package cloud.chenh.emiew.data.service;

import cloud.chenh.emiew.data.base.BaseService;
import cloud.chenh.emiew.data.entity.Block;
import cloud.chenh.emiew.data.repository.BlockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BlockService extends BaseService<Block> {

    @Autowired
    private BlockRepository blockRepository;

    @Override
    public BlockRepository getRepository() {
        return blockRepository;
    }

    public Boolean exist(String type, String value) {
        return getRepository().findFirstByTypeAndValue(type, value) != null;
    }

    public void add(String type, String value) {
        if (!exist(type, value)) {
            save(new Block(type, value));
        }
    }

    @Override
    public List<Block> findAll() {
        return getRepository().findAllByOrderByTypeAsc();
    }

}
