/*
 * Copyright (c) 2008-2016 Haulmont.
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
 *
 */

package com.haulmont.cuba.core.jmx;

import com.haulmont.cuba.core.app.MiddlewareStatisticsAccumulator;
import com.haulmont.cuba.core.global.GlobalConfig;
import com.haulmont.cuba.core.sys.connectionpool.poolinfo.ConnectionPoolInfo;
import com.haulmont.cuba.core.sys.connectionpool.ConnectionPoolSpecificFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component("cuba_StatisticsCounterMBean")
public class StatisticsCounter implements StatisticsCounterMBean {

    private static final Logger log = LoggerFactory.getLogger(StatisticsCounter.class);

    @Inject
    protected MiddlewareStatisticsAccumulator accumulator;

    @Inject
    protected GlobalConfig globalConfig;

    private volatile ConnectionPoolInfo connectionPoolInfo;

    @Override
    public Long getActiveTransactionsCount() {
        return accumulator.getActiveTransactionsCount();
    }

    @Override
    public double getStartedTransactionsCount() {
        return accumulator.getStartedTransactionsCount();
    }

    @Override
    public Long getCommittedTransactionsCount() {
        return accumulator.getCommittedTransactionsCount();
    }

    @Override
    public Long getRolledBackTransactionsCount() {
        return accumulator.getRolledBackTransactionsCount();
    }

    @Override
    public double getTransactionsPerSecond() {
        return accumulator.getTransactionsPerSecond();
    }

    @Override
    public Long getMiddlewareRequestsCount() {
        return accumulator.getMiddlewareRequestsCount();
    }

    @Override
    public double getMiddlewareRequestsPerSecond() {
        return accumulator.getMiddlewareRequestsPerSecond();
    }

    @Override
    public Long getCubaScheduledTasksCount() {
        return accumulator.getCubaScheduledTasksCount();
    }

    @Override
    public double getCubaScheduledTasksPerSecond() {
        return accumulator.getCubaScheduledTasksPerSecond();
    }

    @Override
    public Long getSpringScheduledTasksCount() {
        return accumulator.getSpringScheduledTasksCount();
    }

    @Override
    public double getSpringScheduledTasksPerSecond() {
        return accumulator.getSpringScheduledTasksPerSecond();
    }

    @Override
    public Long getImplicitFlushCount() {
        return accumulator.getImplicitFlushCount();
    }

    @Override
    public int getDbConnectionPoolNumActive() {
        connectionPoolInfo = getConnectionPoolInfo();
        try {
            return connectionPoolInfo.getActiveConnectionsCount();
        } catch (Exception e) {
            log.warn(String.format("Can't get number of active connections from the %s!", connectionPoolInfo.getPoolName()));
        }
        return 0;
    }

    @Override
    public int getDbConnectionPoolNumIdle() {
        connectionPoolInfo = getConnectionPoolInfo();
        try {
            return connectionPoolInfo.getIdleConnectionsCount();
        } catch (Exception e) {
            log.warn(String.format("Can't get number of idle connections from the %s!", connectionPoolInfo.getPoolName()));
        }
        return 0;
    }

    @Override
    public int getDbConnectionPoolMaxTotal() {
        connectionPoolInfo = getConnectionPoolInfo();
        try {
            return connectionPoolInfo.getTotalConnectionsCount();
        } catch (Exception e) {
            log.warn(String.format("Can't get number of total connections from the %s!", connectionPoolInfo.getPoolName()));
        }
        return 0;
    }

    protected ConnectionPoolInfo getConnectionPoolInfo() {
        return ConnectionPoolSpecificFactory.getConnectionPoolInfo(globalConfig.getConnectionPoolName());
    }

    @Override
    public double getAvgDbConnectionPoolNumActive() {
        return accumulator.getAvgDbConnectionPoolNumActive();
    }

    @Override
    public double getAvgDbConnectionPoolNumIdle() {
        return accumulator.getAvgDbConnectionPoolNumIdle();
    }

    @Override
    public double getAvgActiveTransactions() {
        return accumulator.getAvgActiveTransactionsCount();
    }

    @Override
    public double getAvgUserSessions() {
        return accumulator.getAvgUserSessions();
    }

    @Override
    public double getAvgHeapMemoryUsage() {
        return accumulator.getAvgHeapMemoryUsage();
    }

    @Override
    public double getAvgNonHeapMemoryUsage() {
        return accumulator.getAvgNonHeapMemoryUsage();
    }

    @Override
    public double getAvgFreePhysicalMemorySize() {
        return accumulator.getAvgFreePhysicalMemorySize();
    }

    @Override
    public double getAvgFreeSwapSpaceSize() {
        return accumulator.getAvgFreeSwapSpaceSize();
    }

    @Override
    public double getAvgSystemCpuLoad() {
        return accumulator.getAvgSystemCpuLoad();
    }

    @Override
    public double getAvgProcessCpuLoad() {
        return accumulator.getAvgProcessCpuLoad();
    }

    @Override
    public double getAvgThreadCount() {
        return accumulator.getAvgThreadCount();
    }
}