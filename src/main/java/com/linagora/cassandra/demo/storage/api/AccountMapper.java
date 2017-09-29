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

package com.linagora.cassandra.demo.storage.api;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.linagora.cassandra.demo.model.Account;
import com.linagora.cassandra.demo.model.AccountOpertation;
import com.linagora.cassandra.demo.model.ids.AccountId;
import com.linagora.cassandra.demo.model.ids.UserId;

public interface AccountMapper {
    CompletableFuture<Stream<Account>> listAccounts(UserId userId);

    CompletableFuture<Optional<Account>> retrieveAccount(AccountId accountId);

    CompletableFuture<Void> openAccount(Account account);

    CompletableFuture<Void> credit(AccountId accountId, int amount);

    CompletableFuture<Void> debit(AccountId accountId, int amount);

    CompletableFuture<Stream<AccountOpertation>> history(AccountId accountId, Optional<Integer> limit);
}
