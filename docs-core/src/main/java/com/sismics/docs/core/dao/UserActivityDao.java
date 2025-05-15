package com.sismics.docs.core.dao;

import com.sismics.docs.core.dao.criteria.UserActivityCriteria;
import com.sismics.docs.core.dao.dto.UserActivityDto;
import com.sismics.docs.core.model.jpa.UserActivity;
import com.sismics.docs.core.util.jpa.PaginatedList;
import com.sismics.docs.core.util.jpa.PaginatedLists;
import com.sismics.docs.core.util.jpa.QueryParam;
import com.sismics.docs.core.util.jpa.SortCriteria;
import com.sismics.util.context.ThreadLocalContext;

import jakarta.persistence.EntityManager;
import java.sql.Timestamp;
import java.util.*;

public class UserActivityDao {
    public String create(UserActivity userActivity) {
        userActivity.setId(UUID.randomUUID().toString());
        
        EntityManager entityManager = ThreadLocalContext.get().getEntityManager();
        userActivity.setCreateDate(new Date());
        entityManager.persist(userActivity);
        
        return userActivity.getId();
    }
    
    public UserActivity update(UserActivity userActivity) {
        EntityManager entityManager = ThreadLocalContext.get().getEntityManager();
        
        UserActivity userActivityDb = entityManager.find(UserActivity.class, userActivity.getId());
        if (userActivityDb == null) {
            return null;
        }
        
        userActivityDb.setProgress(userActivity.getProgress());
        if (userActivity.getDeadline() != null) {
            userActivityDb.setDeadline(userActivity.getDeadline());
        }
        if (userActivity.getCompletedDate() != null) {
            userActivityDb.setCompletedDate(userActivity.getCompletedDate());
        }
        
        return userActivityDb;
    }
    
    public UserActivity getById(String id) {
        EntityManager entityManager = ThreadLocalContext.get().getEntityManager();
        try {
            return entityManager.find(UserActivity.class, id);
        } catch (Exception e) {
            return null;
        }
    }
    
    public void delete(String id) {
        EntityManager entityManager = ThreadLocalContext.get().getEntityManager();
        
        UserActivity userActivityDb = entityManager.find(UserActivity.class, id);
        if (userActivityDb == null) {
            return;
        }
        
        Date dateNow = new Date();
        userActivityDb.setDeleteDate(dateNow);
    }

    public void findByCriteria(PaginatedList<UserActivityDto> paginatedList, UserActivityCriteria criteria, SortCriteria sortCriteria) {
        Map<String, Object> parameterMap = new HashMap<>();
        
        StringBuilder sb = new StringBuilder("select ua.ID, ua.USER_ID, u.USE_USERNAME_C, ua.ENTITY_ID, ");
        sb.append("d.DOC_TITLE_C, ua.PROGRESS, ua.DEADLINE, ua.COMPLETED_DATE, ua.CREATE_DATE ");
        sb.append("from USER_ACTIVITY ua ");
        sb.append("join T_USER u on ua.USER_ID = u.USE_ID_C ");
        sb.append("left join T_DOCUMENT d on ua.ENTITY_ID = d.DOC_ID_C ");
        
        List<String> criteriaList = new ArrayList<>();
        criteriaList.add("ua.DELETE_DATE is null");
        
        if (criteria.getUserId() != null) {
            criteriaList.add("ua.USER_ID = :userId");
            parameterMap.put("userId", criteria.getUserId());
        }

        if (criteria.getEntityId() != null) {
            criteriaList.add("ua.ENTITY_ID = :entityId");
            parameterMap.put("entityId", criteria.getEntityId());
        }
        
        if (!criteriaList.isEmpty()) {
            sb.append(" where ");
            sb.append(String.join(" and ", criteriaList));
        }
        
        QueryParam queryParam = new QueryParam(sb.toString(), parameterMap);
        List<Object[]> l = PaginatedLists.executePaginatedQuery(paginatedList, queryParam, sortCriteria);
        
        List<UserActivityDto> userActivityDtoList = new ArrayList<>();
        for (Object[] o : l) {
            int i = 0;
            UserActivityDto userActivityDto = new UserActivityDto();
            userActivityDto.setId((String) o[i++]);
            userActivityDto.setUserId((String) o[i++]);
            userActivityDto.setUsername((String) o[i++]);
            userActivityDto.setEntityId((String) o[i++]);
            userActivityDto.setEntityName((String) o[i++]);
            userActivityDto.setProgress((Integer) o[i++]);
            
            Timestamp deadline = (Timestamp) o[i++];
            if (deadline != null) {
                userActivityDto.setDeadlineTimestamp(deadline.getTime());
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
        
        paginatedList.setResultList(userActivityDtoList);
    }
} 