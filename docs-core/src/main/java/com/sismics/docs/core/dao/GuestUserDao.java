package com.sismics.docs.core.dao;

import com.sismics.docs.core.model.jpa.GuestUser;
import com.sismics.util.context.ThreadLocalContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

/**
 * Guest user DAO.
 */
public class GuestUserDao {
    
    public String create(GuestUser guestUser) {
        EntityManager entityManager = ThreadLocalContext.get().getEntityManager();
        if (guestUser.getIdentifier() == null) {
            guestUser.setIdentifier(UUID.randomUUID().toString());
        }
        entityManager.persist(guestUser);
        return guestUser.getIdentifier();
    }

    public List<GuestUser> findAll() {
        EntityManager entityManager = ThreadLocalContext.get().getEntityManager();
        TypedQuery<GuestUser> query = entityManager.createQuery(
            "SELECT g FROM GuestUser g ORDER BY g.createdDate DESC", GuestUser.class);
        return query.getResultList();
    }

    public void updateStatus(String identifier, String status) {
        EntityManager entityManager = ThreadLocalContext.get().getEntityManager();
        GuestUser guestUser = entityManager.find(GuestUser.class, identifier);
        if (guestUser != null) {
            guestUser.setRequestStatus(status);
        }
    }

    public GuestUser findByToken(String accessToken) {
        EntityManager entityManager = ThreadLocalContext.get().getEntityManager();
        TypedQuery<GuestUser> query = entityManager.createQuery(
            "SELECT g FROM GuestUser g WHERE g.accessToken = :token", GuestUser.class);
        query.setParameter("token", accessToken);
        List<GuestUser> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    public GuestUser findById(String identifier) {
        EntityManager entityManager = ThreadLocalContext.get().getEntityManager();
        return entityManager.find(GuestUser.class, identifier);
    }
    
    public List<GuestUser> findByStatus(String status) {
        EntityManager entityManager = ThreadLocalContext.get().getEntityManager();
        TypedQuery<GuestUser> query = entityManager.createQuery(
            "SELECT g FROM GuestUser g WHERE g.requestStatus = :status ORDER BY g.createdDate DESC", 
            GuestUser.class);
        query.setParameter("status", status);
        return query.getResultList();
    }
    
    public void delete(String identifier) {
        EntityManager entityManager = ThreadLocalContext.get().getEntityManager();
        GuestUser guestUser = entityManager.find(GuestUser.class, identifier);
        if (guestUser != null) {
            entityManager.remove(guestUser);
        }
    }
}