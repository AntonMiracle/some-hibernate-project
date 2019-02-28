package config;


import com.model.Message;
import com.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class HibernateConfig {
    private static SessionFactory sessionFactory;
//pool ????????????/
    private static Properties getProperties() {
        Properties properties = new Properties();
        properties.put(Environment.DRIVER, "org.hsqldb.jdbc.JDBCDriver");
        properties.put(Environment.URL, "jdbc:hsqldb:file:dbName;create=true");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "root");
        properties.put(Environment.DIALECT, "org.hibernate.dialect.HSQLDialect");
        properties.put(Environment.SHOW_SQL, "false");
        properties.put(Environment.HBM2DDL_AUTO, "create-drop");
        return properties;
    }

    private static Set<Class> getMappedClasses() {
        Set<Class> classes = new HashSet<>();
        classes.add(User.class);
        classes.add(Message.class);

        return classes;
    }

    private static Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        configuration.setProperties(getProperties());
        for (Class clazz : getMappedClasses()) {
            configuration.addAnnotatedClass(clazz);
        }
        return configuration;
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = getConfiguration();
                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties())
                        .build();
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }
}
