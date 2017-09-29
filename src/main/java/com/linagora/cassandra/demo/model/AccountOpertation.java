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

package com.linagora.cassandra.demo.model;

import java.util.Objects;

import com.linagora.cassandra.demo.model.ids.AccountId;
import com.linagora.cassandra.demo.model.ids.AccountOperationId;

public class AccountOpertation {
    enum Type{
        Debit,
        Credit
    }

    private final AccountOperationId operationId;
    private final AccountId accountId;
    private final Type type;
    private final int amount;

    public AccountOpertation(AccountOperationId operationId, AccountId accountId, Type type, int amount) {
        this.operationId = operationId;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
    }

    public AccountOperationId getOperationId() {
        return operationId;
    }

    public AccountId getAccountId() {
        return accountId;
    }

    public Type getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public final boolean equals(Object o) {
        if (o instanceof AccountOpertation) {
            AccountOpertation that = (AccountOpertation) o;

            return Objects.equals(this.amount, that.amount)
                && Objects.equals(this.accountId, that.accountId)
                && Objects.equals(this.operationId, that.operationId)
                && Objects.equals(this.type, that.type);
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(operationId, accountId, type, amount);
    }
}
