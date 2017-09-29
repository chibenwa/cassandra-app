/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package com.linagora.cassandra.demo.rest;

import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.utils.UUIDs;
import com.github.steveash.guavate.Guavate;
import com.linagora.cassandra.demo.model.User;
import com.linagora.cassandra.demo.model.ids.UserId;
import com.linagora.cassandra.demo.rest.json.JsonExtractor;
import com.linagora.cassandra.demo.rest.json.JsonTransformer;
import com.linagora.cassandra.demo.storage.api.UserMapper;

import spark.Request;
import spark.Response;
import spark.Service;

public class UserRoutes implements Routes {
    private static final String EMPTY_BODY = "";
    public static final Logger LOGGER = LoggerFactory.getLogger(UserRoutes.class);

    private final UserMapper userMapper;
    private final JsonTransformer jsonTransformer;
    private final JsonExtractor<UserRequest> requestJsonExtractor;

    @Inject
    public UserRoutes(UserMapper userMapper, JsonTransformer jsonTransformer) {
        this.userMapper = userMapper;
        this.jsonTransformer = jsonTransformer;
        this.requestJsonExtractor = new JsonExtractor<>(UserRequest.class);
    }

    @Override
    public void define(Service service) {
        LOGGER.debug("Defining user routes");

        service.get("/user", this::getUsers, jsonTransformer);

        service.get("/user/:id", (req, res) -> getUser(service, req, res), jsonTransformer);

        service.delete("/user/:id", this::deleteUser, jsonTransformer);

        service.post("/user", this::createUser, jsonTransformer);

        service.put("/user/:id", this::updateUser);
    }

    private Object updateUser(Request req, Response res) throws com.linagora.cassandra.demo.rest.json.JsonExtractException {
        UserRequest userRequest = requestJsonExtractor.parse(req.body());
        UserId userId = extractId(req);
        User user = new User(userId, userRequest.getName(), userRequest.getMailAddress());
        userMapper.upsertUser(user).join();
        res.status(200);
        return EMPTY_BODY;
    }

    private Object createUser(Request req, Response res) throws com.linagora.cassandra.demo.rest.json.JsonExtractException {
        UserRequest userRequest = requestJsonExtractor.parse(req.body());
        UserId userId = new UserId(UUIDs.timeBased());
        User user = new User(userId, userRequest.getName(), userRequest.getMailAddress());
        userMapper.upsertUser(user).join();
        res.status(200);
        return userId;
    }

    private Object deleteUser(Request req, Response res) {
        userMapper.deleteUser(extractId(req)).join();
        res.status(200);
        return "";
    }

    private Object getUser(Service service, Request req, Response res) {
        Optional<User> user = userMapper.retrieveUser(extractId(req)).join();
        if (!user.isPresent()) {
            throw service.halt(404);
        }
        res.status(200);
        return user.get();
    }

    private UserId extractId(Request req) {
        return new UserId(UUID.fromString(req.params("id")));
    }

    private Object getUsers(Request req, Response res) {
        res.status(200);
        return userMapper.listUser()
            .join()
            .collect(Guavate.toImmutableList());
    }
}
