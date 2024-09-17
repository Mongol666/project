package by.itacademy.gpisarev;

import by.itacademy.gpisarev.entity.AbstractEntity;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public abstract class AbstractRepoJpa <T extends AbstractEntity> {
    private final SessionFactory factory;
    private final Class<T> entityClass;

    protected AbstractRepoJpa(SessionFactory factory, Class<T> entityClass) {
        this.factory = factory;
        this.entityClass = entityClass;
    }

    protected EntityManager getEntityManager() {
        return factory.createEntityManager();
    }

    protected Set<T> getAll() {
        Set<T> entities = new HashSet<>();
        EntityManager manager = getEntityManager();
        try {
            manager.getTransaction().begin();
            TypedQuery<T> queryForEntities = manager
                    .createQuery("from " + entityClass.getName(), entityClass);
            entities = new HashSet<>(queryForEntities.getResultList());
            if (!entities.isEmpty()) {
                log.info("Все {} найдены", entityClass.getName());
            } else {
                log.info("Ни одного {} нет в базе данных, {} не найдены", entityClass.getName(), entityClass.getName());
            }
            manager.getTransaction().commit();
        } catch (Exception e) {
            log.error("Ошибка нахождения всех {}: {}", entityClass.getName(), e.getMessage());
        } finally {
            manager.close();
        }
        return entities;
    }

    protected T getByID(int id) {
        EntityManager manager = getEntityManager();
        T entity = null;
        try {
            manager.getTransaction().begin();
            entity = manager.find(entityClass, id);
            if (entity != null) {
                log.info("{} найден по ID", entityClass.getName());
            } else {
                log.info("{} не найден по ID", entityClass.getName());
            }
            manager.getTransaction().commit();
        } catch (Exception e) {
            log.error("Ошибка нахождения {} по ID: {}", entityClass.getName(), e.getMessage());
        } finally {
            manager.close();
        }
        return entity;
    }


    protected boolean create(T entity) {
        if (entity == null) {
            return false;
        }
        EntityManager manager = getEntityManager();
        boolean isCreated = false;
        try {
            manager.getTransaction().begin();
            manager.persist(entity);
            isCreated = manager.contains(entity);
            if (isCreated) {
                log.info("{} был добавлен в базу данных", entityClass.getName());
            } else {
                log.info("{} не был добавлен в базу данных", entityClass.getName());
            }
            manager.getTransaction().commit();
        } catch (Exception e) {
            log.error("Ошибка добавления {} : {}", entityClass.getName(), e.getMessage());
            manager.getTransaction().rollback();
        } finally {
            manager.close();
        }
        return isCreated;
    }

    protected boolean update(T entity) {
        if (entity == null) {
            return false;
        }
        EntityManager manager = getEntityManager();
        boolean isUpdated = false;
        try {
            manager.getTransaction().begin();
            entity = manager.merge(entity);
            isUpdated = manager.contains(entity);
            if (isUpdated) {
                log.info("{} был обновлен", entityClass.getName());
            } else {
                log.info("{} не был обновлен", entityClass.getName());
            }
            manager.getTransaction().commit();
        } catch (Exception e) {
            log.error("Ошибка обновления {} : {}", entityClass.getName(), e.getMessage());
            manager.getTransaction().rollback();
        } finally {
            manager.close();
        }
        return isUpdated;
    }

    protected boolean remove(T entity) {
        if (entity == null) {
            return false;
        }
        EntityManager manager = getEntityManager();
        boolean isDeleted = false;
        try {
            manager.getTransaction().begin();
            manager.remove(entity);
            isDeleted = !manager.contains(entity);
            if (isDeleted) {
                log.info("{} был удалён", entityClass.getName());
            } else {
                log.info("{} не был удалён", entityClass.getName());
            }
            manager.getTransaction().commit();
        } catch (Exception e) {
            log.error("Ошибка удаления {} : {}", entityClass.getName(), e.getMessage());
            manager.getTransaction().rollback();
        } finally {
            manager.close();
        }
        return isDeleted;
    }

}
