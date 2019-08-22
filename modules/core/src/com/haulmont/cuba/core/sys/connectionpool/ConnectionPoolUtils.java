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

import com.google.common.base.Preconditions;
import com.haulmont.cuba.core.sys.AppContext;

import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Set;
import java.util.regex.Pattern;

public class ConnectionPoolUtils {
    public static ObjectName getPoolObjectName(Pattern regexPattern) {
        if(regexPattern == null) {
            return null;
        }

        Set<ObjectName> names = ManagementFactory.getPlatformMBeanServer().queryNames(null, null);
        for (ObjectName name : names) {
            if (regexPattern.matcher(name.toString()).matches()) {
                return name;
            }
        }
        return null;
    }

    protected static String getMainDatasourceName() {
        String jndiName = AppContext.getProperty("cuba.dataSourceJndiName");
        Preconditions.checkNotNull(jndiName);

        // mentioned as constant in tomcat docs
        String tomcatPrefix = "java:comp/env/";
        if (jndiName.startsWith(tomcatPrefix)) {
            jndiName = jndiName.substring(tomcatPrefix.length());
        }
        return jndiName;
    }
}
