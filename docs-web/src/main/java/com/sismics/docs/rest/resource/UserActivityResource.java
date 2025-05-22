package com.sismics.docs.rest.resource;

import com.sismics.docs.core.dao.UserActivityDao;
import com.sismics.docs.core.dao.criteria.UserActivityCriteria;
import com.sismics.docs.core.dao.dto.UserActivityDto;
import com.sismics.docs.core.model.jpa.UserActivity;
import com.sismics.docs.rest.constant.BaseFunction;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.rest.util.ValidationUtil;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * AI-generated-content
 * tool: Grok
 * version: 3
 * usage: I asked the AI model to generate a REST resource class for managing user activities. I copied the code and learned from it.
 */

/**
 * User activity REST resources.
 *
 * @author Grok 3
 */
@Path("/user-activity")
public class UserActivityResource extends BaseResource {
    /**
     * Create or update a user activity.
     */
    @PUT
    @Path("create-or-update")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrUpdate(
            @FormParam("id") String id,
            @FormParam("entity_id") String entityId,
            @FormParam("planned_date") String plannedDate,
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
            Date adjustedPlannedDate = null;
            if (plannedDate != null) {
                Date parsedDate = dateFormat.parse(plannedDate);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(parsedDate);
                calendar.add(Calendar.DATE, 1);
                adjustedPlannedDate = calendar.getTime();
            }

            if (id != null) {
                userActivity = userActivityDao.getById(id);
                if (userActivity == null) {
                    throw new ClientException("ActivityNotFound", "Activity not found");
                }
                if (!userActivity.getUserId().equals(userId)) {
                    throw new ForbiddenClientException();
                }

                userActivity.setProgress(progress);
                if (adjustedPlannedDate != null) {
                    userActivity.setPlannedDate(adjustedPlannedDate);
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
                if (adjustedPlannedDate != null) {
                    userActivity.setPlannedDate(adjustedPlannedDate);
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

    /**
     * Returns all user activities (admin only).
     */
    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@QueryParam("user_id") String userId) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);

        UserActivityCriteria criteria = new UserActivityCriteria();
        if (userId != null) {
            criteria.setUserId(userId);
        }

        return getResponse(criteria);
    }

    /**
     * Returns the current user's activities.
     */
    @GET
    @Path("user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserActivities(@QueryParam("entity_id") String entityId) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        UserActivityCriteria criteria = new UserActivityCriteria();
        criteria.setUserId(principal.getId());
        if (entityId != null) {
            criteria.setEntityId(entityId);
        }

        return getResponse(criteria);
    }

    /**
     * Delete a user activity.
     */
    @DELETE
    @Path("delete/{id: [a-z0-9\\-]+}")
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

    private Response getResponse(UserActivityCriteria criteria) {
        UserActivityDao userActivityDao = new UserActivityDao();
        List<UserActivityDto> activityList = userActivityDao.findByCriteria(criteria);

        JsonObjectBuilder response = Json.createObjectBuilder();
        JsonArrayBuilder activities = Json.createArrayBuilder();

        for (UserActivityDto userActivityDto : activityList) {
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
            if (userActivityDto.getPlannedDateTimestamp() != null) {
                activity.add("planned_date_timestamp", userActivityDto.getPlannedDateTimestamp());
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
                .add("total", activityList.size());

        return Response.ok().entity(response.build()).build();
    }
}