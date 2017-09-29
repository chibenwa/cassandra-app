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

import static com.datastax.driver.core.querybuilder.QueryBuilder.bindMarker;
import static com.datastax.driver.core.querybuilder.QueryBuilder.delete;
import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.insertInto;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import com.linagora.cassandra.demo.model.User;
import com.linagora.cassandra.demo.model.ids.UserId;
import com.linagora.cassandra.demo.storage.cassandra.utils.CassandraAsyncExecutor;
import com.linagora.cassandra.demo.storage.cassandra.utils.CassandraUtils;

public class CassandraUserDAO {

    public static final String TABLE_NAME = "user";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String MAIL = "mail";
    public static final Logger LOGGER = LoggerFactory.getLogger(CassandraUserDAO.class);
    private final CassandraAsyncExecutor executor;
    private final PreparedStatement selectAll;
    private final PreparedStatement select;
    private final PreparedStatement insert;
    private final PreparedStatement delete;

    @Inject
    public CassandraUserDAO(CassandraAsyncExecutor executor, Session session) {
        this.executor = executor;
        LOGGER.debug("Creating user table");
        session.execute(SchemaBuilder.createTable(TABLE_NAME)
            .ifNotExists()
            .addPartitionKey(ID, DataType.uuid())
            .addColumn(NAME, DataType.text())
            .addColumn(MAIL, DataType.text()));

        selectAll = prepareSelectAll(session);
        select = prepareSelect(session);
        insert = prepareInsert(session);
        delete = prepareDelete(session);
    }

    private PreparedStatement prepareSelectAll(Session session) {
        return session.prepare(
            select(ID, NAME, MAIL)
                .from(TABLE_NAME));
    }

    private PreparedStatement prepareSelect(Session session) {
        return session.prepare(
            select(ID, NAME, MAIL)
                .from(TABLE_NAME)
                .where(eq(ID, bindMarker(ID))));
    }

    private PreparedStatement prepareInsert(Session session) {
        return session.prepare(
            insertInto(TABLE_NAME)
                .value(ID, bindMarker(ID))
                .value(NAME, bindMarker(NAME))
                .value(MAIL, bindMarker(MAIL)));
    }

    private PreparedStatement prepareDelete(Session session) {
        return session.prepare(
            delete()
                .from(TABLE_NAME)
                .where(eq(ID, bindMarker(ID))));
    }

    CompletableFuture<Void> addUser(User user) {
        return executor.executeVoid(
            insert.bind()
                .setUUID(ID, user.getUserId().getId())
                .setString(NAME, user.getUsername())
                .setString(MAIL, user.getMailAddress()));
    }

    CompletableFuture<Void> deleteUser(UserId userId) {
        return executor.executeVoid(
            delete.bind()
                .setUUID(ID, userId.getId()));
    }

    CompletableFuture<Optional<User>> retrieveUser(UserId userId) {
        return executor.executeSingleRow(
            select.bind()
                .setUUID(ID, userId.getId()))
            .thenApply(optional -> optional.map(this::toUser));
    }

    CompletableFuture<Stream<User>> retrieveUsers() {
        return executor.execute(
            selectAll.bind())
            .thenApply(CassandraUtils::convertToStream)
            .thenApply(stream -> stream.map(this::toUser));
    }

    private User toUser(Row row) {
        UserId userId = new UserId(row.getUUID(ID));
        return new User(userId,
            row.getString(NAME),
            row.getString(MAIL));
    }
}
