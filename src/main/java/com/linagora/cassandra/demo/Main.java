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

package com.linagora.cassandra.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import com.linagora.cassandra.demo.guice.CassandraModule;
import com.linagora.cassandra.demo.guice.CassandraSessionModule;
import com.linagora.cassandra.demo.guice.RestServerModule;
import com.linagora.cassandra.demo.rest.RestServer;

public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    // docker run -d -p 9042:9042 cassandra:3

    public static void main(String... args) {
        Injector injector = Guice.createInjector(Modules.combine(
            new CassandraModule(),
            new CassandraSessionModule(),
            new RestServerModule()));

        injector.getInstance(RestServer.class).start();

        LOGGER.info("Server started");
    }
}
