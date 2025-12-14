package org.ausiankou.util;

import org.ausiankou.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtilTest {
    private static SessionFactory sessionFactory;

    public static void configureHibernate(String url, String username, String password) {
        try {
            Configuration configuration = new Configuration();

            configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
            configuration.setProperty("hibernate.connection.url", url);
            configuration.setProperty("hibernate.connection.username", username);
            configuration.setProperty("hibernate.connection.password", password);

            configuration.setProperty("hibernate.hbm2ddl.auto", "create");
            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

            configuration.addAnnotatedClass(User.class);

            sessionFactory = configuration.buildSessionFactory();

        } catch (Exception e) {
            throw new RuntimeException("Failed to configure test Hibernate", e);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            throw new IllegalStateException("SessionFactory not initialized");
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }
}