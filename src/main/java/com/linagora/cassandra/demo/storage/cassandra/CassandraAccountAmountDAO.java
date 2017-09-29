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
import static com.datastax.driver.core.querybuilder.QueryBuilder.decr;
import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.incr;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static com.datastax.driver.core.querybuilder.QueryBuilder.update;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import com.linagora.cassandra.demo.model.ids.AccountId;
import com.linagora.cassandra.demo.storage.cassandra.utils.CassandraAsyncExecutor;

public class CassandraAccountAmountDAO {

    private static final String TABLE_NAME = "accountAmount";
    private static final String ID = "id";
    private static final String AMOUNT = "amount";
    private final CassandraAsyncExecutor executor;
    private final PreparedStatement increment;
    private final PreparedStatement decrement;
    private final PreparedStatement select;

    @Inject
    public CassandraAccountAmountDAO(Session session, CassandraAsyncExecutor executor) {
        this.executor = executor;
        session.execute(SchemaBuilder.createTable(TABLE_NAME)
            .ifNotExists()
            .addPartitionKey(ID, DataType.timeuuid())
            .addColumn(AMOUNT, DataType.counter()));

        increment = prepareIncrement(session);
        decrement = prepareDecrement(session);
        select = prepareSelect(session);
    }

    private PreparedStatement prepareSelect(Session session) {
        return session.prepare(
            select(AMOUNT)
                .from(TABLE_NAME)
                .where(eq(ID, bindMarker(ID))));
    }

    private PreparedStatement prepareIncrement(Session session) {
        return session.prepare(update(TABLE_NAME)
            .with(incr(AMOUNT, bindMarker(AMOUNT)))
            .where(eq(ID, bindMarker(ID))));
    }

    private PreparedStatement prepareDecrement(Session session) {
        return session.prepare(update(TABLE_NAME)
            .with(decr(AMOUNT, bindMarker(AMOUNT)))
            .where(eq(ID, bindMarker(ID))));
    }

    public CompletableFuture<Void> increment(AccountId accountId, int value) {
        return executor.executeVoid(
            increment.bind()
                .setUUID(ID, accountId.getId())
                .setLong(AMOUNT, value));
    }

    public CompletableFuture<Void> decrement(AccountId accountId, int value) {
        return executor.executeVoid(
            decrement.bind()
                .setUUID(ID, accountId.getId())
                .setLong(AMOUNT, value));
    }

    public CompletableFuture<Optional<Integer>> getAmount(AccountId accountId) {
        return executor.executeSingleRow(
            select.bind()
                .setUUID(ID, accountId.getId()))
            .thenApply(optional -> optional.map(row -> Long.valueOf(row.getLong(AMOUNT)).intValue()));
    }
}
