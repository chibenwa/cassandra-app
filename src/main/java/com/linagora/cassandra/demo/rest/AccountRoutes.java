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

import com.github.steveash.guavate.Guavate;
import com.linagora.cassandra.demo.model.Account;
import com.linagora.cassandra.demo.model.AccountOpertation;
import com.linagora.cassandra.demo.model.ids.AccountId;
import com.linagora.cassandra.demo.model.ids.UserId;
import com.linagora.cassandra.demo.rest.json.AccountUpdateRequest;
import com.linagora.cassandra.demo.rest.json.JsonExtractor;
import com.linagora.cassandra.demo.rest.json.JsonTransformer;
import com.linagora.cassandra.demo.storage.api.AccountMapper;

import spark.Request;
import spark.Response;
import spark.Service;

public class AccountRoutes implements Routes {
    private static final String EMPTY_BODY = "";
    public static final Logger LOGGER = LoggerFactory.getLogger(AccountRoutes.class);

    private final AccountMapper accountMapper;
    private final JsonTransformer jsonTransformer;
    private final JsonExtractor<AccountUpdateRequest> requestJsonExtractor;

    @Inject
    public AccountRoutes(AccountMapper accountMapper, JsonTransformer jsonTransformer) {
        this.accountMapper = accountMapper;
        this.jsonTransformer = jsonTransformer;
        this.requestJsonExtractor = new JsonExtractor<>(AccountUpdateRequest.class);
    }

    @Override
    public void define(Service service) {
        LOGGER.debug("Defining account routes");

        service.get("/user/:userId/account", this::retrieveAccounts, jsonTransformer);

        service.get("/account/:accountId", (req, res) -> retrieveAccount(service, req), jsonTransformer);

        service.post("/account/:userid", this::createAccount, jsonTransformer);

        service.put("/account/:accountId", this::updateAccount, jsonTransformer);

        service.get("/account/:accountId/history", this::readHistory, jsonTransformer);
    }

    private Object readHistory(Request req, Response res) throws com.linagora.cassandra.demo.rest.json.JsonExtractException {
        AccountId accountId = extractAccountId(req);
        res.status(200);
        return accountMapper.history(accountId).join()
            .collect(Guavate.toImmutableList());
    }

    private Object updateAccount(Request req, Response res) throws com.linagora.cassandra.demo.rest.json.JsonExtractException {
        AccountUpdateRequest updateRequest = requestJsonExtractor.parse(req.body());
        AccountOpertation.Type type = AccountOpertation.Type.retrieveType(updateRequest.getType());
        AccountId accountId = extractAccountId(req);
        switch (type) {
            case Debit:
                accountMapper.debit(accountId, updateRequest.getAmount()).join();
                break;
            case Credit:
                accountMapper.credit(accountId, updateRequest.getAmount()).join();
                break;
        }
        res.status(200);
        return EMPTY_BODY;
    }

    private Object createAccount(Request req, Response res) {
        UserId userId = extractUserId(req);
        res.status(200);
        return accountMapper.openNewAccount(userId).join();
    }

    private Object retrieveAccount(Service service, Request req) {
        Optional<Account> account = accountMapper.retrieveAccount(extractAccountId(req)).join();
        if (!account.isPresent()) {
            throw service.halt(404);
        }
        return account.get();
    }

    private Object retrieveAccounts(Request req, Response res) {
        res.status(200);
        return accountMapper.listAccounts(extractUserId(req)).join()
            .collect(Guavate.toImmutableList());
    }

    private UserId extractUserId(Request req) {
        return new UserId(UUID.fromString(req.params("userId")));
    }

    private AccountId extractAccountId(Request req) {
        return new AccountId(UUID.fromString(req.params("accountId")));
    }

}
