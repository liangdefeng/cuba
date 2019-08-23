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
import javax.management.ObjectName;

@Component
public abstract class ConnectionPoolInfoImpl implements ConnectionPoolInfo {

    @Inject
    protected GlobalConfig globalConfig;
    protected ObjectName registeredPoolName;

    protected ConnectionPoolInfoImpl() {
        this.registeredPoolName = ConnectionPoolUtils.getPoolObjectName(getRegexPattern());
    }

    public ObjectName getRegisteredMBeanName() {
        return registeredPoolName;
    }
}
