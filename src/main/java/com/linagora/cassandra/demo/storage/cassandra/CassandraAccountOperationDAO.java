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
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import com.datastax.driver.core.utils.UUIDs;
import com.linagora.cassandra.demo.model.AccountOpertation;
import com.linagora.cassandra.demo.model.ids.AccountId;
import com.linagora.cassandra.demo.model.ids.AccountOperationId;
import com.linagora.cassandra.demo.storage.cassandra.utils.CassandraAsyncExecutor;
import com.linagora.cassandra.demo.storage.cassandra.utils.CassandraUtils;

public class CassandraAccountOperationDAO {

    private static final String TABLE_NAME = "accountOperations";
    private static final String ACCOUNT_ID = "accountId";
    private static final String OPERATION_ID = "operationId";
    private static final String TYPE = "type";
    private static final String VALUE = "value";
    private static final String LIMIT = "limit";
    private final PreparedStatement select;
    private final PreparedStatement insert;
    private CassandraAsyncExecutor executor;

    @Inject
    public CassandraAccountOperationDAO(Session session, CassandraAsyncExecutor executor) {
        this.executor = executor;
        session.execute(SchemaBuilder.createTable(TABLE_NAME)
            .ifNotExists()
            .addPartitionKey(ACCOUNT_ID, DataType.timeuuid())
            .addClusteringColumn(OPERATION_ID, DataType.timeuuid())
            .addColumn(TYPE, DataType.text())
            .addColumn(VALUE, DataType.cint()));

        try {
            insert = prepareInsert(session);
            select = prepareSelect(session);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private PreparedStatement prepareSelect(Session session) {
        return session.prepare(selectCql());
    }

    private Select.Where selectCql() {
        return select(ACCOUNT_ID, OPERATION_ID, TYPE, VALUE)
            .from(TABLE_NAME)
            .where(eq(ACCOUNT_ID, bindMarker(ACCOUNT_ID)));
    }

    private PreparedStatement prepareInsert(Session session) {
        return session.prepare(
            insertInto(TABLE_NAME)
                .value(ACCOUNT_ID, bindMarker(ACCOUNT_ID))
                .value(OPERATION_ID, bindMarker(OPERATION_ID))
                .value(TYPE, bindMarker(TYPE))
                .value(VALUE, bindMarker(VALUE)));
    }

    public CompletableFuture<Void> add(AccountOpertation opertation) {
        return executor.executeVoid(
            insert.bind()
                .setUUID(ACCOUNT_ID, opertation.getAccountId().getId())
                .setUUID(OPERATION_ID, opertation.getOperationId().getId())
                .setString(TYPE, opertation.getType().getValue())
                .setInt(VALUE, opertation.getAmount()));
    }

    public CompletableFuture<Stream<AccountOpertation>> retrieveOpterations(AccountId accountId) {
        return executor.execute(
            select.bind()
                .setUUID(ACCOUNT_ID, accountId.getId()))
            .thenApply(CassandraUtils::convertToStream)
            .thenApply(stream -> stream.map(this::fromRow));
    }

    private AccountOpertation fromRow(Row row) {
        AccountId accountId = new AccountId(row.getUUID(ACCOUNT_ID));
        AccountOperationId operationId = new AccountOperationId(row.getUUID(OPERATION_ID));
        AccountOpertation.Type type = AccountOpertation.Type.retrieveType(row.getString(TYPE));
        return new AccountOpertation(operationId, accountId, type, row.getInt(VALUE));
    }
}
