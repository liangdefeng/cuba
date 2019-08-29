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

package com.haulmont.cuba.core.sys.connectionpool.poolinfo;

import javax.management.*;
import java.util.regex.Pattern;

public interface ConnectionPoolInfo {

    /**
     * @return connection pool name
     */
    default String getPoolName() {
        return "Empty Connection Pool";
    }

    /**
     * @return registered bean name for connection pool
     */
    default ObjectName getRegisteredMBeanName() {
        return null;
    }

    /**
     * @return return regex pattern to find connection pool ObjectName
     */
    default Pattern getRegexPattern() {
        return null;
    }

    /**
     * @return current count of active connections
     */
    default int getActiveConnectionsCount() throws AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        return 0;
    }

    /**
     * @return current count of idle connections
     */
    default int getIdleConnectionsCount() throws AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        return 0;
    }

    /**
     * @return count of total available connections (active and idle)
     */
    default int getTotalConnectionsCount() throws AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        return 0;
    }
}
