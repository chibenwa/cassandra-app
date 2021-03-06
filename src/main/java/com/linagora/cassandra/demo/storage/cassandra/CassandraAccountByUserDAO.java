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
import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.insertInto;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import javax.inject.Inject;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import com.linagora.cassandra.demo.model.ids.AccountId;
import com.linagora.cassandra.demo.model.ids.UserId;
import com.linagora.cassandra.demo.storage.cassandra.utils.CassandraAsyncExecutor;
import com.linagora.cassandra.demo.storage.cassandra.utils.CassandraUtils;

public class CassandraAccountByUserDAO {

    private static final String TABLE_NAME = "accountByUser";
    private static final String ID = "id";
    private static final String USER_ID = "userId";
    private final CassandraAsyncExecutor executor;
    private final PreparedStatement select;
    private final PreparedStatement insert;

    @Inject
    public CassandraAccountByUserDAO(Session session, CassandraAsyncExecutor executor) {
        this.executor = executor;
        session.execute(SchemaBuilder.createTable(TABLE_NAME)
            .ifNotExists()
            .addPartitionKey(USER_ID, DataType.timeuuid())
            .addClusteringColumn(ID, DataType.timeuuid()));

        select = prepareSelect(session);
        insert = prepareInsert(session);
    }

    private PreparedStatement prepareInsert(Session session) {
        return session.prepare(insertInto(TABLE_NAME)
            .value(ID, bindMarker(ID))
            .value(USER_ID, bindMarker(USER_ID)));
    }

    private PreparedStatement prepareSelect(Session session) {
        return session.prepare(select(USER_ID, ID)
            .from(TABLE_NAME)
            .where(eq(USER_ID, bindMarker(USER_ID))));
    }

    public CompletableFuture<Stream<CassandraAccountDAO.AccountOwnerShip>> retrieveAccounts(UserId userId) {
        return executor.execute(
            select.bind()
                .setUUID(USER_ID, userId.getId()))
            .thenApply(CassandraUtils::convertToStream)
            .thenApply(stream -> stream.map(CassandraAccountDAO::fromRow));
    }

    public CompletableFuture<Void> addAccount(AccountId accountId, UserId owner) {
        return executor.executeVoid(
            insert.bind()
                .setUUID(ID, accountId.getId())
                .setUUID(USER_ID, owner.getId()));
    }

}
