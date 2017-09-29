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
import java.util.UUID;

public class Account {
    private final UUID accountId;
    private final int amount;

    public Account(UUID accountId, int amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public final boolean equals(Object o) {
        if (o instanceof Account) {
            Account account = (Account) o;

            return Objects.equals(this.amount, account.amount)
                && Objects.equals(this.accountId, account.accountId);
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(accountId, amount);
    }

    @Override
    public String toString() {
        return "Account{" +
            "accountId=" + accountId +
            ", amount=" + amount +
            '}';
    }
}
