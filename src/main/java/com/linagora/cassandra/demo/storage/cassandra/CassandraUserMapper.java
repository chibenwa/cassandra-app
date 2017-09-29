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

package com.linagora.cassandra.demo.storage.cassandra;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import javax.inject.Inject;

import com.linagora.cassandra.demo.model.User;
import com.linagora.cassandra.demo.model.ids.UserId;
import com.linagora.cassandra.demo.storage.api.UserMapper;

public class CassandraUserMapper implements UserMapper {
    private final CassandraUserDAO userDAO;

    @Inject
    public CassandraUserMapper(CassandraUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public CompletableFuture<Stream<User>> listUser() {
        return userDAO.retrieveUsers();
    }

    @Override
    public CompletableFuture<Optional<User>> retrieveUser(UserId userId) {
        return userDAO.retrieveUser(userId);
    }

    @Override
    public CompletableFuture<Void> deleteUser(UserId userId) {
        return userDAO.deleteUser(userId);
    }

    @Override
    public CompletableFuture<Void> upsertUser(User user) {
        return userDAO.addUser(user);
    }
}
