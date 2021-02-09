package cloud.chenh.emiew.data.base;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

public abstract class BaseService<T extends BaseEntity> {

    public abstract BaseRepository<T> getRepository();

    @Transactional(rollbackOn = Exception.class)
    public T save(T entity) {
        return getRepository().save(entity);
    }

    @Transactional(rollbackOn = Exception.class)
    public List<T> save(Collection<T> entities) {
        return getRepository().saveAll(entities);
    }

    @Transactional(rollbackOn = Exception.class)
    public void delete(T entity) {
        getRepository().delete(entity);
    }

    @Transactional(rollbackOn = Exception.class)
    public void delete(Collection<T> entities) {
        getRepository().deleteAll(entities);
    }

    @Transactional(rollbackOn = Exception.class)
    public void deleteById(Collection<Long> ids) {
        for (Long id : ids) {
            getRepository().deleteById(id);
        }
    }

    public T findById(Long id) {
        return getRepository().findById(id).orElse(null);
    }

    public List<T> findAll() {
        return getRepository().findAll();
    }

}
