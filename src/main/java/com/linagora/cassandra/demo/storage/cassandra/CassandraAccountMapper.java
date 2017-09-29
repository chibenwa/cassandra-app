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

import com.datastax.driver.core.utils.UUIDs;
import com.google.common.base.Preconditions;
import com.linagora.cassandra.demo.model.Account;
import com.linagora.cassandra.demo.model.AccountOpertation;
import com.linagora.cassandra.demo.model.ids.AccountId;
import com.linagora.cassandra.demo.model.ids.AccountOperationId;
import com.linagora.cassandra.demo.model.ids.UserId;
import com.linagora.cassandra.demo.storage.api.AccountMapper;
import com.linagora.cassandra.demo.storage.cassandra.utils.FluentFutureStream;

public class CassandraAccountMapper implements AccountMapper {
    private final CassandraAccountDAO accountDAO;
    private final CassandraAccountAmountDAO amountDAO;
    private final CassandraAccountByUserDAO accountByUserDAO;
    private final CassandraAccountOperationDAO operationDAO;

    @Inject
    public CassandraAccountMapper(CassandraAccountDAO accountDAO, CassandraAccountAmountDAO amountDAO,
                                  CassandraAccountByUserDAO accountByUserDAO, CassandraAccountOperationDAO operationDAO) {
        this.accountDAO = accountDAO;
        this.amountDAO = amountDAO;
        this.accountByUserDAO = accountByUserDAO;
        this.operationDAO = operationDAO;
    }

    @Override
    public CompletableFuture<Stream<Account>> listAccounts(UserId userId) {
        return FluentFutureStream.of(accountByUserDAO.retrieveAccounts(userId))
            .thenComposeOnAll(ownership -> amountDAO.getAmount(ownership.getAccountId())
                .thenApply(amount -> computeAccount(ownership, amount)))
            .completableFuture();
    }

    private Account computeAccount(CassandraAccountDAO.AccountOwnerShip ownership, Optional<Integer> amount) {
        return new Account(
            ownership.getAccountId(),
            ownership.getUserId(),
            amount.orElse(0));
    }

    @Override
    public CompletableFuture<Optional<Account>> retrieveAccount(AccountId accountId) {
        return accountDAO.retrieveOwner(accountId)
            .thenCompose(accountOwnerShip -> amountDAO.getAmount(accountId)
                .thenApply(amount -> accountOwnerShip.map(ownership -> computeAccount(ownership, amount))));
    }

    @Override
    public CompletableFuture<AccountId> openNewAccount(UserId userId) {
        AccountId accountId = new AccountId(UUIDs.timeBased());
        return accountDAO.addAccount(accountId, userId)
            .thenCompose(any -> accountByUserDAO.addAccount(accountId, userId))
            .thenApply(any -> accountId);
    }

    @Override
    public CompletableFuture<Void> credit(AccountId accountId, int amount) {
        Preconditions.checkArgument(amount > 0);

        return operationDAO.add(
            new AccountOpertation(
            new AccountOperationId(UUIDs.timeBased()),
            accountId,
            AccountOpertation.Type.Credit,
            amount))
            .thenCompose(any -> amountDAO.increment(accountId, amount));
    }

    @Override
    public CompletableFuture<Void> debit(AccountId accountId, int amount) {
        Preconditions.checkArgument(amount > 0);

        return operationDAO.add(
            new AccountOpertation(
                new AccountOperationId(UUIDs.timeBased()),
                accountId,
                AccountOpertation.Type.Debit,
                amount))
            .thenCompose(any -> amountDAO.decrement(accountId, amount));
    }

    @Override
    public CompletableFuture<Stream<AccountOpertation>> history(AccountId accountId) {
        return operationDAO.retrieveOpterations(accountId);
    }
}
