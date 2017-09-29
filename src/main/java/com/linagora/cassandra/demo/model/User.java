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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linagora.cassandra.demo.model.ids.UserId;

public class User {
    private final UserId userId;
    private final String username;
    private final String mailAddress;

    public User(UserId userId, String username, String mailAddress) {
        this.userId = userId;
        this.username = username;
        this.mailAddress = mailAddress;
    }

    @JsonIgnore
    public UserId getUserId() {
        return userId;
    }

    public String getId() {
        return userId.getId().toString();
    }

    public String getUsername() {
        return username;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    @Override
    public final boolean equals(Object o) {
        if (o instanceof User) {
            User user = (User) o;

            return Objects.equals(this.userId, user.userId)
                && Objects.equals(this.username, user.username)
                && Objects.equals(this.mailAddress, user.mailAddress);
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(userId, username, mailAddress);
    }

    @Override
    public String toString() {
        return "User{" +
            "userId=" + userId +
            ", username='" + username + '\'' +
            ", mailAddress='" + mailAddress + '\'' +
            '}';
    }
}
