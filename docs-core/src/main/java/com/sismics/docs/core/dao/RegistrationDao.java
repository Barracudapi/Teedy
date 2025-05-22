package com.sismics.docs.core.dao;

import com.sismics.docs.core.model.jpa.Registration;
import com.sismics.util.context.ThreadLocalContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.UUID;

public class RegistrationDao {
    public String create(Registration registration) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        if (registration.getId() == null) {
            registration.setId(UUID.randomUUID().toString());
        }
        em.persist(registration);
        return registration.getId();
    }

    public List<Registration> findAll() {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        TypedQuery<Registration> query = em.createQuery("SELECT r FROM Registration r ORDER BY r.createdAt DESC", Registration.class);
        return query.getResultList();
    }

    public Registration findByToken(String token) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        TypedQuery<Registration> query = em.createQuery("SELECT r FROM Registration r WHERE r.token = :token", Registration.class);
        query.setParameter("token", token);
        List<Registration> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    public Registration findById(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        return em.find(Registration.class, id);
    }

    public void updateStatus(String id, String status) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Registration request = em.find(Registration.class, id);
        if (request != null) {
            request.setStatus(status);
        }
    }

    public void updatePassword(String id, String newPassword) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Registration request = em.find(Registration.class, id);
        if (request != null) {
            request.setPassword(newPassword);
            em.merge(request);
        }
    }
}