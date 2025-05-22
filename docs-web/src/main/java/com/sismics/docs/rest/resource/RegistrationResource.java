package com.sismics.docs.rest.resource;

import com.google.common.base.Strings;
import com.sismics.docs.core.constant.Constants;
import com.sismics.docs.core.dao.RegistrationDao;
import com.sismics.docs.core.dao.UserDao;
import com.sismics.docs.core.model.jpa.Registration;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.docs.rest.constant.BaseFunction;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.rest.exception.ServerException;
import com.sismics.rest.util.ValidationUtil;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

/**
 * User REST resources.
 *
 * @author fanxy
 */
@Path("/registration")
public class RegistrationResource extends BaseResource {
    /**
     * AI-generated-content
     * tool: Grok
     * version: 3
     * usage: I asked the AI model to fix my /registration/create API endpoint. I copied the code and learned from it.
     */
    /**
     * Stores a user registration request.
     *
     * @api {post} /registration/create Store user registration request
     * @apiName PostRegisterAccount
     * @apiGroup User
     * @apiParam jsonObject JSON object of random token, username and password
     * @apiSuccess {String} status Status OK
     * @apiVersion 1.0.0
     */
    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerAccount(JsonObject jsonObject) {
        String token = jsonObject.getString("token", null);
        if (token == null || token.length() < 8) {
            throw new ClientException("ValidationError", "Invalid token");
        }

        RegistrationDao registrationDao = new RegistrationDao();
        Registration registration = registrationDao.findByToken(token);

        if (registration == null) {
            String username = jsonObject.getString("username", null);
            String password = jsonObject.getString("password", null);

            if (username == null || password == null) {
                throw new ClientException("ValidationError", "Username and password are required");
            }
            username = ValidationUtil.validateLength(username, "username", 3, 50);
            ValidationUtil.validateUsername(username, "username");
            password = ValidationUtil.validateLength(password, "password", 8, 50);

            UserDao userDao = new UserDao();
            if (userDao.getActiveByUsername(username) != null) {
                throw new ClientException("AlreadyExistingUsername", "Username already exists");
            }

            String ip = request.getHeader("x-forwarded-for");
            if (Strings.isNullOrEmpty(ip)) {
                ip = request.getRemoteAddr();
            }

            registration = new Registration(token, ip, username, password);
            registrationDao.create(registration);

            return Response.ok().entity(Json.createObjectBuilder().add("status", 1).build()).build();
        }

        String status = registration.getStatus();
        int statusNum = 1;
        JsonObjectBuilder builder = Json.createObjectBuilder();

        switch (status) {
            case "PENDING":
                statusNum = 1;
                break;
            case "APPROVED":
                statusNum = 2;
                UserDao userDao = new UserDao();
                User user = userDao.getActiveByUsername(registration.getUsername());
                if (user == null) {
                    user = new User();
                    user.setRoleId(Constants.DEFAULT_USER_ROLE);
                    user.setUsername(registration.getUsername());
                    user.setPassword(registration.getPassword());
                    user.setEmail(token + "@guest.local");
                    user.setStorageQuota(10_000_000_000L);
                    user.setOnboarding(true);

                    try {
                        userDao.create(user, UUID.randomUUID().toString());
                        registrationDao.updatePassword(registration.getId(), "");
                    } catch (Exception e) {
                        if ("AlreadyExistingUsername".equals(e.getMessage())) {
                            throw new ClientException("AlreadyExistingUsername", "Username already exists");
                        } else {
                            throw new ServerException("UnknownError", "Error creating new user", e);
                        }
                    }
                }
                builder.add("username", user.getUsername());
                break;
            case "REJECTED":
                statusNum = 3;
                registrationDao.updatePassword(registration.getId(), "");
                break;
        }

        builder.add("status", statusNum);
        return Response.ok().entity(builder.build()).build();
    }

    /**
     * Returns all user registrations (admin only).
     *
     * @api {get} /registration/all Get user registrations
     * @apiName GetRegistrations
     * @apiGroup User
     * @apiPermission admin
     * @apiVersion 1.0.0
     */
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRegistrations() {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);

        RegistrationDao registrationDao = new RegistrationDao();
        List<Registration> registrations = registrationDao.findAll();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        for (Registration registration : registrations) {
            jsonArrayBuilder.add(Json.createObjectBuilder()
                    .add("id", registration.getId())
                    .add("token", registration.getToken())
                    .add("ip", registration.getIp())
                    .add("username", registration.getUsername())
                    .add("created_at", registration.getCreatedAtToString())
                    .add("status", registration.getStatus())
            );
        }

        return Response.ok().entity(Json.createObjectBuilder().add("registrations", jsonArrayBuilder).build()).build();
    }

    /**
     * Approves or rejects a user registration request (admin only).
     *
     * @api {post} /registration/update Approve or reject user registration request
     * @apiName PostUpdateRegistrationStatus
     * @apiGroup User
     * @apiPermission admin
     * @apiParam jsonObject JSON object of registration id and approval status
     * @apiVersion 1.0.0
     */
    @POST
    @Path("/status/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRegistrationStatus(JsonObject jsonObject) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);

        String id = jsonObject.getString("id", null);
        String status = jsonObject.getString("status", null);
        if (id == null || status == null || (!"APPROVED".equals(status) && !"REJECTED".equals(status))) {
            throw new ClientException("ValidationError", "Invalid id or status");
        }

        RegistrationDao registrationDao = new RegistrationDao();
        Registration registration = registrationDao.findById(id);
        if (registration == null) {
            throw new ClientException("ValidationError", "Registration not found");
        }

        registrationDao.updateStatus(id, status);

        return Response.ok().entity(Json.createObjectBuilder().add("status", "ok").build()).build();
    }
}
