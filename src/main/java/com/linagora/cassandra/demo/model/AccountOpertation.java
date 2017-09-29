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

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.linagora.cassandra.demo.model.ids.AccountId;
import com.linagora.cassandra.demo.model.ids.AccountOperationId;

public class AccountOpertation {
    public enum Type{
        Debit("debit"),
        Credit("credit");

        private String value;

        Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Type retrieveType(String value) {
            return Arrays.stream(Type.values())
                .filter(type -> type.getValue().equals(value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Can not retrieve type " + value));
        }
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

    @JsonIgnore
    public AccountOperationId getOperationId() {
        return operationId;
    }

    @JsonIgnore
    public AccountId getAccountId() {
        return accountId;
    }

    @JsonIgnore
    public Type getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    @JsonProperty("accountId")
    public String getRawAccountId() {
        return accountId.getId().toString();
    }

    @JsonProperty("operationId")
    public String getRawOperationId() {
        return operationId.getId().toString();
    }

    @JsonProperty("type")
    public String getRawType() {
        return type.getValue();
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
