package org.ausiankou.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.ausiankou.exception.UserServiceException;
import org.ausiankou.model.User;
import org.ausiankou.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class UserDaoImpl implements UserDao {
    private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);

    @Override
    public User save(User user) {
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            logger.info("User saved successfully with ID: {}", user.getId());
            return user;
        } catch (Exception ex){
            if(transaction != null){
                transaction.rollback();
            }
            logger.error("Error saving user: {}", ex.getMessage(), ex);
            throw new UserServiceException("Error saving user", ex);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            User user = session.get(User.class, id);
            if(user != null){
                logger.info("User found with ID: {}", id);
            } else {
                logger.info("User not found with ID: {}", id);
            }
            return Optional.ofNullable(user);
        } catch (Exception ex){
            logger.error("Error finding user by ID {}: {}", id, ex.getMessage(), ex);
            throw new UserServiceException("Error finding user by ID", ex);
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<User> cq = cb.createQuery(User.class);
            Root<User> root = cq.from(User.class);
            cq.select(root);
            Query<User> query = session.createQuery(cq);
            List<User> users = query.getResultList();
            logger.info("Found {} users", users.size());
            return users;
        } catch (Exception e) {
            logger.error("Error finding all users: {}", e.getMessage(), e);
            throw new UserServiceException("Error finding all users", e);
        }
    }

    @Override
    public User update(User user) {
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();
            User updateUser = session.merge(user);
            transaction.commit();
            logger.info("User updated successfully with ID: {}", updateUser.getId());
            return updateUser;
        } catch (Exception ex){
            if(transaction != null){
                transaction.rollback();
            }
            logger.error("Error updating user: {}", ex.getMessage(), ex);
            throw new UserServiceException("Error updating user", ex);
        }
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if(user != null){
                session.remove(user);
                logger.info("User deleted  successfully with ID: {}", id);
            } else {
                logger.warn("User not found for deletion with ID: {}", id);
            }
            transaction.commit();
        } catch (Exception ex){
            if(transaction != null){
                transaction.rollback();
            }
            logger.error("Error deleting user: {}", ex.getMessage(), ex);
            throw new UserServiceException("Error deleting user", ex);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            Query<User> query = session.createQuery(
                    "from User where email = :email", User.class);
            query.setParameter("email",email);
            User user = query.uniqueResult();
            return Optional.ofNullable(user);

        } catch (Exception ex){
            logger.error("Error finding user by email {}: {}", email, ex.getMessage(), ex);
            throw new UserServiceException("Error finding user by email", ex);
        }
    }

    @Override
    public List<User> findByName(String name) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            Query<User> query = session.createQuery(
                    "from User where name like :name", User.class);
            query.setParameter("name", "%" + name + "%");
            return query.getResultList();
        } catch (Exception ex){
            logger.error("Error finding users by name {}: {}", name, ex.getMessage(), ex);
            throw new UserServiceException("Error finding users by name", ex);
        }
    }
}
