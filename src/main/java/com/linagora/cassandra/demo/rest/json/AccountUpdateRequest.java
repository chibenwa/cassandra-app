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

package com.linagora.cassandra.demo.rest.json;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class AccountUpdateRequest {
    private final String type;
    private final int amount;

    @JsonCreator
    public AccountUpdateRequest(@JsonProperty("type") String type, @JsonProperty("amount") int amount) {
        Preconditions.checkArgument(amount > 0);
        this.type = type;
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public final boolean equals(Object o) {
        if (o instanceof AccountUpdateRequest) {
            AccountUpdateRequest that = (AccountUpdateRequest) o;

            return Objects.equals(this.amount, that.amount)
                && Objects.equals(this.type, that.type);
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(type, amount);
    }

    @Override
    public String toString() {
        return "AccountUpdateRequest{" +
            "type='" + type + '\'' +
            ", amount=" + amount +
            '}';
    }
}
