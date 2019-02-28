package com.repository;

import com.model.Role;
import com.model.User;
import config.HibernateConfig;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.Set;

public class UserRepository {

    public boolean saveUser(User user) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            if (findByEmail(user.getEmail()) == null) {
                session.save(user);
            } else {
                session.update(user);
            }
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    public User findByEmail(String email) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return (User) session.createQuery("FROM User u WHERE u.email='" + email + "'").uniqueResult();
        }
    }

    public Set<User> findAllUsers() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return new HashSet<>(session.createQuery("FROM User u", User.class).list());
        }
    }

    public boolean deleteUser(User user) {
        if (user == null) return false;
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.delete(user);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            return false;
        }
    }

    public Set<User> findUsersWithRole(Role role) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            TypedQuery<User> q = session.createQuery("SELECT u FROM User u JOIN u.roles r WHERE r = :role", User.class);
            q.setParameter("role", role);
            return new HashSet<>(q.getResultList());
        }
    }
}
