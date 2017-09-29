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

package com.linagora.cassandra.demo.guice;

import javax.inject.Singleton;

import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class CassandraSessionModule extends AbstractModule {

    private static final String KEYSPACE = "cassandraDemo4";
    private static final int REPLICATION_FACTOR = 1;
    private static final String DURABLE_WRITES = String.valueOf(true);
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CassandraSessionModule.class);

    @Override
    protected void configure() {
    }

    @Singleton
    @Provides
    public Cluster provideCluster() {
        return Cluster.builder()
            .addContactPoint("127.0.0.1").withPort(9042)
            .build();
    }

    @Singleton
    @Provides
    public Session provideSessio(Cluster cluster) {
        try (Session session = cluster.connect()) {
            session.execute("CREATE KEYSPACE IF NOT EXISTS " + KEYSPACE
                + " WITH replication = {'class':'SimpleStrategy', 'replication_factor':" + REPLICATION_FACTOR + "}"
                + " AND durable_writes = " + DURABLE_WRITES
                + ";");
            LOGGER.debug("Keyspace created");
        }
        return cluster.connect(KEYSPACE);
    }
}
