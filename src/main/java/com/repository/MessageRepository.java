package com.repository;

import com.model.Message;
import com.model.User;
import config.HibernateConfig;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.Set;

public class MessageRepository {

    public boolean saveMessage(Message message) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            if (message.getId() <= 0) {
                session.save(message);
            } else {
                session.update(message);
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

    public boolean deleteMessage(Message message) {
        if (message == null || message.getId() <= 0) return false;
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.delete(message);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            return false;
        }
    }

    public Set<Message> findAllMessages() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return new HashSet<>(session.createQuery("FROM Message m", Message.class).list());
        }
    }

    public Set<Message> findAllMessages(User user) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            TypedQuery<Message> q = session.createQuery("SELECT m FROM Message m JOIN m.user u WHERE u = :user", Message.class);
            q.setParameter("user", user);
            return new HashSet<>(q.getResultList());
        }
    }

    public Message findById(long id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.get(Message.class, id);
        }
    }

    public Set<Message> findByText(String text) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return new HashSet<>(session.createQuery("FROM Message m WHERE m.text='" + text + "'", Message.class).list());
        }
    }
}
