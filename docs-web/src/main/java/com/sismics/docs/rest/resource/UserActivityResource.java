package com.sismics.docs.rest.resource;

import com.sismics.docs.core.dao.UserActivityDao;
import com.sismics.docs.core.dao.criteria.UserActivityCriteria;
import com.sismics.docs.core.dao.dto.UserActivityDto;
import com.sismics.docs.core.model.jpa.UserActivity;
import com.sismics.docs.core.util.jpa.PaginatedList;
import com.sismics.docs.core.util.jpa.PaginatedLists;
import com.sismics.docs.core.util.jpa.SortCriteria;
import com.sismics.docs.rest.constant.BaseFunction;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.rest.util.ValidationUtil;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

import java.text.SimpleDateFormat;
import java.util.Date;

@Path("/useractivity")
public class UserActivityResource extends BaseResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(
            @QueryParam("limit") Integer limit,
            @QueryParam("offset") Integer offset,
            @QueryParam("sort_column") Integer sortColumn,
            @QueryParam("asc") Boolean asc,
            @QueryParam("user_id") String userId) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);
        
        SortCriteria sortCriteria = new SortCriteria(sortColumn, asc);
        
        UserActivityCriteria criteria = new UserActivityCriteria();
        if (userId != null) {
            criteria.setUserId(userId);
        }
        
        PaginatedList<UserActivityDto> paginatedList = PaginatedLists.create(limit, offset);
        UserActivityDao userActivityDao = new UserActivityDao();
        userActivityDao.findByCriteria(paginatedList, criteria, sortCriteria);

        JsonObjectBuilder response = Json.createObjectBuilder();
        JsonArrayBuilder activities = Json.createArrayBuilder();

        for (UserActivityDto userActivityDto : paginatedList.getResultList()) {
            JsonObjectBuilder activity = Json.createObjectBuilder()
                    .add("id", userActivityDto.getId())
                    .add("user_id", userActivityDto.getUserId())
                    .add("username", userActivityDto.getUsername())
                    .add("progress", userActivityDto.getProgress());
            
            if (userActivityDto.getEntityId() != null) {
                activity.add("entity_id", userActivityDto.getEntityId());
            }
            
            if (userActivityDto.getEntityName() != null) {
                activity.add("entity_name", userActivityDto.getEntityName());
            }
            
            if (userActivityDto.getDeadlineTimestamp() != null) {
                activity.add("planned_date_timestamp", userActivityDto.getDeadlineTimestamp());
            }
            
            if (userActivityDto.getCompletedDateTimestamp() != null) {
                activity.add("completed_date_timestamp", userActivityDto.getCompletedDateTimestamp());
            }
            
            if (userActivityDto.getCreateTimestamp() != null) {
                activity.add("create_timestamp", userActivityDto.getCreateTimestamp());
            }
            
            activities.add(activity);
        }
        
        response.add("activities", activities)
                .add("total", paginatedList.getResultCount());
        
        return Response.ok().entity(response.build()).build();
    }
    
    @GET
    @Path("user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentUserActivities(
            @QueryParam("limit") Integer limit,
            @QueryParam("offset") Integer offset,
            @QueryParam("sort_column") Integer sortColumn,
            @QueryParam("asc") Boolean asc,
            @QueryParam("entity_id") String entityId) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        
        SortCriteria sortCriteria = new SortCriteria(sortColumn, asc);
        UserActivityCriteria criteria = new UserActivityCriteria();
        criteria.setUserId(principal.getId());
        if (entityId != null) {
            criteria.setEntityId(entityId);
        }
        
        PaginatedList<UserActivityDto> paginatedList = PaginatedLists.create(limit, offset);
        UserActivityDao userActivityDao = new UserActivityDao();
        userActivityDao.findByCriteria(paginatedList, criteria, sortCriteria);

        JsonObjectBuilder response = Json.createObjectBuilder();
        JsonArrayBuilder activities = Json.createArrayBuilder();

        for (UserActivityDto userActivityDto : paginatedList.getResultList()) {
            JsonObjectBuilder activity = Json.createObjectBuilder()
                    .add("id", userActivityDto.getId())
                    .add("user_id", userActivityDto.getUserId())
                    .add("username", userActivityDto.getUsername())
                    .add("progress", userActivityDto.getProgress());
            
            if (userActivityDto.getEntityId() != null) {
                activity.add("entity_id", userActivityDto.getEntityId());
            }
            
            if (userActivityDto.getEntityName() != null) {
                activity.add("entity_name", userActivityDto.getEntityName());
            }
            
            if (userActivityDto.getDeadlineTimestamp() != null) {
                activity.add("planned_date_timestamp", userActivityDto.getDeadlineTimestamp());
            }
            
            if (userActivityDto.getCompletedDateTimestamp() != null) {
                activity.add("completed_date_timestamp", userActivityDto.getCompletedDateTimestamp());
            }
            
            if (userActivityDto.getCreateTimestamp() != null) {
                activity.add("create_timestamp", userActivityDto.getCreateTimestamp());
            }
            
            activities.add(activity);
        }
        
        response.add("activities", activities)
                .add("total", paginatedList.getResultCount());
        
        return Response.ok().entity(response.build()).build();
    }
    
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrUpdate(
            @FormParam("id") String id,
            @FormParam("entity_id") String entityId,
            @FormParam("planned_date") String deadline,
            @FormParam("progress") Integer progress) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        
        ValidationUtil.validateRequired(progress, "progress");
        
        String userId = principal.getId();
        
        UserActivityDao userActivityDao = new UserActivityDao();
        UserActivity userActivity;
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (id != null) {
                userActivity = userActivityDao.getById(id);
                if (userActivity == null) {
                    throw new ClientException("ActivityNotFound", "Activity not found");
                }
                
                if (!userActivity.getUserId().equals(userId)) {
                    throw new ForbiddenClientException();
                }
                
                userActivity.setProgress(progress);
                if (deadline != null) {
                    userActivity.setDeadline(dateFormat.parse(deadline));
                }
                
                if (progress == 100) {
                    userActivity.setCompletedDate(new Date());
                } else {
                    userActivity.setCompletedDate(null);
                }
                
                userActivityDao.update(userActivity);
            } else {
                userActivity = new UserActivity();
                userActivity.setUserId(userId);
                userActivity.setEntityId(entityId);
                userActivity.setProgress(progress);
                if (deadline != null) {
                    userActivity.setDeadline(dateFormat.parse(deadline));
                }
                
                if (progress == 100) {
                    userActivity.setCompletedDate(new Date());
                }
                
                id = userActivityDao.create(userActivity);
            }
        } catch (Exception e) {
            throw new ClientException("ValidationError", e.getMessage());
        }
        
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("id", id);
        
        return Response.ok().entity(response.build()).build();
    }
    
    @DELETE
    @Path("{id: [a-z0-9\\-]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") String id) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        
        UserActivityDao userActivityDao = new UserActivityDao();
        UserActivity userActivity = userActivityDao.getById(id);
        
        if (userActivity == null) {
            throw new ClientException("NotFound", "Activity not found");
        }
        
        if (!principal.getId().equals(userActivity.getUserId()) && !hasBaseFunction(BaseFunction.ADMIN)) {
            throw new ForbiddenClientException();
        }
        
        userActivityDao.delete(id);
        
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("status", "ok");
        
        return Response.ok().entity(response.build()).build();
    }
} 