/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.cuba.core.sys.connectionpool;

import com.haulmont.cuba.core.sys.connectionpool.poolinfo.CommonsConnectionPoolInfo;
import com.haulmont.cuba.core.sys.connectionpool.poolinfo.ConnectionPoolInfo;
import com.haulmont.cuba.core.sys.connectionpool.poolinfo.HikariConnectionPoolInfo;
import com.haulmont.cuba.core.sys.connectionpool.poolinfo.TomcatConnectionPoolInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ConnectionPoolSpecificFactory {
    private static Log log = LogFactory.getLog(ConnectionPoolSpecificFactory.class);

    private static Map<String, Class<? extends ConnectionPoolInfo>> registeredPools = new HashMap<>();
    private static Map<String, ConnectionPoolInfo> registeredPoolsCache = new HashMap<>();
    protected static final ConnectionPoolInfo emptyPoolInfo = new ConnectionPoolInfo() {
    };
    ;
    protected static volatile boolean dbConnPoolNotFound;

    static {
        registerConnectionPool("COMMONS", CommonsConnectionPoolInfo.class);
        registerConnectionPool("TOMCAT", TomcatConnectionPoolInfo.class);
        registerConnectionPool("HIKARI", HikariConnectionPoolInfo.class);
    }

    public static void registerConnectionPool(String name, Class<? extends ConnectionPoolInfo> poolClass) {
        registeredPools.put(name, poolClass);
    }

    public static Class unRegisterConnectionPool(String name) {
        return registeredPools.remove(name);
    }

    public static boolean unRegisterConnectionPool(String name, Class<? extends ConnectionPoolInfo> poolClass) {
        return registeredPools.remove(name, poolClass);
    }

    public static ConnectionPoolInfo getConnectionPoolInfo(String poolName) {
        if (dbConnPoolNotFound) {
            return emptyPoolInfo;
        }

        if (registeredPoolsCache.containsKey(poolName)) {
            return registeredPoolsCache.get(poolName);
        }

        if (!registeredPools.containsKey(poolName) || registeredPools.get(poolName) == null) {
            log.warn(String.format("Connection pool %s is unsupported.", poolName));
            dbConnPoolNotFound = true;
            return emptyPoolInfo;
        }

        ConnectionPoolInfo connectionPoolInfo;

        try {
            connectionPoolInfo = registeredPools.get(poolName).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.warn(String.format("Can't instantiate new instance of %s", poolName), e);
            dbConnPoolNotFound = true;
            return emptyPoolInfo;
        }

        if (connectionPoolInfo.getRegisteredMBeanName() == null) {
            log.warn(String.format("No one connection pool was found for %s type!", poolName));
            dbConnPoolNotFound = true;
            return emptyPoolInfo;
        }
        registeredPoolsCache.put(poolName, connectionPoolInfo);
        return connectionPoolInfo;
    }
}
