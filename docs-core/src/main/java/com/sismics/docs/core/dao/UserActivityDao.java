package com.sismics.docs.core.dao;

import com.sismics.docs.core.dao.criteria.UserActivityCriteria;
import com.sismics.docs.core.dao.dto.UserActivityDto;
import com.sismics.docs.core.model.jpa.UserActivity;
import com.sismics.util.context.ThreadLocalContext;
import jakarta.persistence.EntityManager;

import java.sql.Timestamp;
import java.util.*;

/**
 * AI-generated-content
 * tool: Grok
 * version: 3
 * usage: I asked the AI model to generate a DAO class for managing user activities. I copied the code and learned from it.
 */

/**
 * User activity DAO.
 *
 * @author Grok 3
 */
public class UserActivityDao {
    public String create(UserActivity userActivity) {
        userActivity.setId(UUID.randomUUID().toString());
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        userActivity.setCreateDate(new Date());
        em.persist(userActivity);
        return userActivity.getId();
    }

    public UserActivity getById(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            return em.find(UserActivity.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    public UserActivity update(UserActivity userActivity) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        UserActivity userActivityDb = em.find(UserActivity.class, userActivity.getId());
        if (userActivityDb == null) {
            return null;
        }
        userActivityDb.setProgress(userActivity.getProgress());
        if (userActivity.getPlannedDate() != null) {
            userActivityDb.setPlannedDate(userActivity.getPlannedDate());
        }
        if (userActivity.getCompletedDate() != null) {
            userActivityDb.setCompletedDate(userActivity.getCompletedDate());
        }
        return userActivityDb;
    }

    public void delete(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        UserActivity userActivityDb = em.find(UserActivity.class, id);
        if (userActivityDb == null) {
            return;
        }
        Date dateNow = new Date();
        userActivityDb.setDeleteDate(dateNow);
    }

    public List<UserActivityDto> findByCriteria(UserActivityCriteria criteria) {
        Map<String, Object> parameterMap = new HashMap<>();

        StringBuilder sb = new StringBuilder("select ua.UTA_ID_C, ua.UTA_IDUSER_C, u.USE_USERNAME_C, ua.UTA_ENTITY_ID_C, ");
        sb.append("d.DOC_TITLE_C, ua.UTA_PROGRESS_N, ua.UTA_PLANNED_DATE_D, ua.UTA_COMPLETED_DATE_D, ua.UTA_CREATEDATE_D ");
        sb.append("from T_USER_ACTIVITY ua ");
        sb.append("join T_USER u on ua.UTA_IDUSER_C = u.USE_ID_C ");
        sb.append("left join T_DOCUMENT d on ua.UTA_ENTITY_ID_C = d.DOC_ID_C ");

        List<String> criteriaList = new ArrayList<>();
        criteriaList.add("ua.UTA_DELETEDATE_D is null");

        if (criteria.getUserId() != null) {
            criteriaList.add("ua.UTA_IDUSER_C = :userId");
            parameterMap.put("userId", criteria.getUserId());
        }
        if (criteria.getEntityId() != null) {
            criteriaList.add("ua.UTA_ENTITY_ID_C = :entityId");
            parameterMap.put("entityId", criteria.getEntityId());
        }

        sb.append(" where ");
        sb.append(String.join(" and ", criteriaList));

        EntityManager em = ThreadLocalContext.get().getEntityManager();
        jakarta.persistence.Query query = em.createNativeQuery(sb.toString());
        for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        @SuppressWarnings("unchecked")
        List<Object[]> resultList = query.getResultList();

        List<UserActivityDto> userActivityDtoList = new ArrayList<>();
        for (Object[] o : resultList) {
            int i = 0;
            UserActivityDto userActivityDto = new UserActivityDto();
            userActivityDto.setId((String) o[i++]);
            userActivityDto.setUserId((String) o[i++]);
            userActivityDto.setUsername((String) o[i++]);
            userActivityDto.setEntityId((String) o[i++]);
            userActivityDto.setEntityName((String) o[i++]);
            userActivityDto.setProgress((Integer) o[i++]);

            Timestamp plannedDate = (Timestamp) o[i++];
            if (plannedDate != null) {
                userActivityDto.setPlannedDateTimestamp(plannedDate.getTime());
            }
            Timestamp completedDate = (Timestamp) o[i++];
            if (completedDate != null) {
                userActivityDto.setCompletedDateTimestamp(completedDate.getTime());
            }
            Timestamp createDate = (Timestamp) o[i++];
            if (createDate != null) {
                userActivityDto.setCreateTimestamp(createDate.getTime());
            }

            userActivityDtoList.add(userActivityDto);
        }

        return userActivityDtoList;
    }
}