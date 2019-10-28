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

package com.haulmont.cuba.core.sys;

import com.haulmont.cuba.core.global.Stores;
import com.haulmont.cuba.core.sys.jdbc.ProxyDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import javax.sql.DataSource;
import java.sql.SQLException;

public class CubaDataSourceLookup {

    private static final Logger log = LoggerFactory.getLogger(CubaDataSourceLookup.class);

    protected static final String DATASOURCE_PROVIDER_PROPERTY_NAME = "cuba.dataSourceProvider";
    protected static final String DATASOURCE_JNDI_NAME_PROPERTY_NAME = "cuba.dataSourceJndiName";
    protected static final String MAIN_DATASOURCE_DEFAULT_JNDI_NAME = "jdbc/CubaDS";
    protected static final String APPLICATION = "application";
    protected static final String JNDI = "jndi";

    public DataSource getDataSource(String storeName) {
        String dataSourceProvider = getDataSourceProvider(storeName);
        String dsJndiName = getDataSourceJndiName(storeName);

        if (dataSourceProvider == null || JNDI.equals(dataSourceProvider)) {
            DataSourceLookup lookup = new JndiDataSourceLookup();
            return lookup.getDataSource(dsJndiName);
        }
        if (APPLICATION.equals(dataSourceProvider)) {
            ApplicationDataSourceInitialization appDataSourceInit = new ApplicationDataSourceInitialization();
            return appDataSourceInit.getApplicationDataSource(storeName);
        }
        throw new RuntimeException(String.format("DataSource provider '%s' is unsupported! Available: 'jndi', 'application'", dataSourceProvider));
    }

    public void closeApplicationDataSource(String storeName, DataSource dataSource) {
        String dataSourceProvider = getDataSourceProvider(storeName);
        try {
            if (APPLICATION.equals(dataSourceProvider) && dataSource != null &&
                    ProxyDataSource.class.isAssignableFrom(dataSource.getClass()) && dataSource.isWrapperFor(HikariDataSource.class)) {
                dataSource.unwrap(HikariDataSource.class).close();
            }
        } catch (SQLException | ClassCastException e) {
            log.error("Can't close sanity application data source.", e);
        }
    }

    protected String getDataSourceProvider(String storeName) {
        if (Stores.MAIN.equals(storeName)) {
            return AppContext.getProperty(DATASOURCE_PROVIDER_PROPERTY_NAME);
        }
        return AppContext.getProperty(DATASOURCE_PROVIDER_PROPERTY_NAME + "_" + storeName);
    }

    protected String getDataSourceJndiName(String storeName) {
        if (Stores.MAIN.equals(storeName)) {
            String mainDsJndiName = AppContext.getProperty(DATASOURCE_JNDI_NAME_PROPERTY_NAME);
            return mainDsJndiName == null ? MAIN_DATASOURCE_DEFAULT_JNDI_NAME : mainDsJndiName;
        }
        return AppContext.getProperty(DATASOURCE_JNDI_NAME_PROPERTY_NAME + "_" + storeName);
    }
}
