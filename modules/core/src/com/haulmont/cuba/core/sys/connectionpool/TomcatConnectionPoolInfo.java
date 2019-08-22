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

import com.haulmont.cuba.core.global.GlobalConfig;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.regex.Pattern;

public class TomcatConnectionPoolInfo extends ConnectionPoolInfoImpl {
    @Override
    public String getPoolName() {
        return "Tomcat Connection Pool";
    }

    @Override
    public Pattern getRegexPattern() {
        String tomcatDsRegexp = String.format(
                "^tomcat\\.jdbc:name=\"%s\",context=/%s,engine=Catalina,type=ConnectionPool,host=[\\w\\d]+,class=[\\w\\d\\.]+$",
                globalConfig.getWebContextName(),
                ConnectionPoolUtils.getMainDatasourceName()
        );
        return Pattern.compile(tomcatDsRegexp);
    }

    @Override
    public int getActiveConnectionsCount() throws AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        return (Integer) ManagementFactory.getPlatformMBeanServer().getAttribute(registeredPoolName, "NumActive");
    }

    @Override
    public int getIdleConnectionsCount() throws AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        return (Integer) ManagementFactory.getPlatformMBeanServer().getAttribute(registeredPoolName, "NumIdle");
    }

    @Override
    public int getTotalConnectionsCount() throws AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        return (Integer) ManagementFactory.getPlatformMBeanServer().getAttribute(registeredPoolName, "MaxActive");
    }
}
