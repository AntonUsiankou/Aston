package org.ausiankou.util;


import org.ausiankou.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HibernateUtil {
    private static final Logger logger = LogManager.getLogger(HibernateUtil.class);
    private static SessionFactory sessionFactory;

    static {
        try{
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            configuration.addAnnotatedClass(User.class);
            sessionFactory = configuration.buildSessionFactory();
            logger.info("Hibernate sessionFactory created successFully");
        } catch (Throwable ex){
            logger.error("Initial SessionFactory creation failed: {}", ex.getMessage(), ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    public static SessionFactory getSessionFactory(){
        return sessionFactory;
    }

    public static void shutdown(){
        if(sessionFactory != null){
            sessionFactory.close();
            logger.info("Hibernate SessionFactory closed");
        }
    }
}
