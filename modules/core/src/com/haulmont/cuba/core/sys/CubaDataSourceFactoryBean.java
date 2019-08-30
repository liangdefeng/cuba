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

import com.haulmont.cuba.core.sys.jdbc.ProxyDataSource;
import com.haulmont.cuba.core.sys.persistence.DbmsType;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jndi.JndiObjectFactoryBean;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.*;

public class CubaDataSourceFactoryBean extends CubaJndiObjectFactoryBean {
    protected static final String DATASOURCE_PROVIDER_PROPERTY_NAME = "cuba.dataSourceProvider";
    protected static final String HOST = "dataSource.cuba_host";
    protected static final String PORT = "dataSource.cuba_port";
    protected static final String DB_NAME = "dataSource.cuba_dbName";
    protected static final String CONNECTION_PARAMS = "dataSource.cuba_connectionParams";
    protected static final String JDBC_URL = "jdbcUrl";
    protected static final String CUBA = "cuba";
    protected static final String MS_SQL_2005 = "2005";
    public static final String MAIN = "_MAIN_";
    public static final String POSTGRES_DBMS = "postgres";
    public static final String MSSQL_DBMS = "mssql";
    public static final String ORACLE_DBMS = "oracle";
    public static final String MYSQL_DBMS = "mysql";
    public static final String HSQL_DBMS = "hsql";
    protected String dataSourceProvider;

    private String storeName;
    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    @Override
    public Class<DataSource> getObjectType() {
        return DataSource.class;
    }

    @Override
    public Object getObject() {
        dataSourceProvider = AppContext.getProperty(getDSProviderPropertyName());
        if ("jndi".equals(dataSourceProvider)) {
            return super.getObject();
        } else if (dataSourceProvider == null || "application".equals(dataSourceProvider)) {
            return getApplicationDataSource();
        }
        throw new RuntimeException("DataSource provider type is unsupported! Available: 'jndi', 'application'");
    }

    protected DataSource getApplicationDataSource() {
        if (storeName == null) {
            storeName = MAIN;
        }
        Properties hikariConfigProperties = getHikariConfigProperties();
        HikariConfig config = new HikariConfig(hikariConfigProperties);

        config.setRegisterMbeans(true);
        config.setPoolName("HikariPool-" + storeName);

        HikariDataSource ds = new HikariDataSource(config);
        return new ProxyDataSource(ds);
    }

    protected Properties getHikariConfigProperties() {
        Properties hikariConfigProperties = new Properties();
        String[] propertiesNames = AppContext.getPropertyNames();
        String filterParam = ".dataSource.";
        if (!MAIN.equals(storeName)) {
            filterParam = ".dataSource_" + storeName + ".";
        }
        String hikariConfigDSPrefix;
        String cubaConfigDSPrefix = CUBA + filterParam;
        String hikariPropertyName;

        for (String cubaPropertyName : propertiesNames) {
            if (!cubaPropertyName.contains(filterParam)) {
                continue;
            }
            String value = AppContext.getProperty(cubaPropertyName);
            if (value == null) {
                continue;
            }

            hikariPropertyName = cubaPropertyName.replace(cubaConfigDSPrefix, "");
            hikariConfigDSPrefix = "dataSource.";
            if (isHikariConfigField(hikariPropertyName)) {
                hikariConfigDSPrefix = "";
            }
            hikariConfigProperties.put(cubaPropertyName.replace(cubaConfigDSPrefix, hikariConfigDSPrefix), value);
        }
        if (hikariConfigProperties.getProperty(JDBC_URL) == null) {
            hikariConfigProperties.setProperty(JDBC_URL, getJdbcUrlFromParts(hikariConfigProperties));
        }
        return hikariConfigProperties;
    }

    protected String getJdbcUrlFromParts(Properties properties) {
        String urlPrefix = getUrlPrefix();
        String host = properties.getProperty(HOST);
        String port = properties.getProperty(PORT);
        String dbName = properties.getProperty(DB_NAME);
        if(host == null || port == null || dbName == null) {
            return null;
        }
        String jdbcUrl = urlPrefix + host + ":" + port + "/" + dbName;
        if (properties.get(CONNECTION_PARAMS) != null) {
            jdbcUrl = jdbcUrl + properties.get(CONNECTION_PARAMS);
        }
        return jdbcUrl;
    }

    protected String getUrlPrefix() {
        String dbmsType = DbmsType.getType(storeName);
        if (dbmsType == null) {
            throw new RuntimeException("dbmsType should be specified for each dataSource!");
        }
        switch (dbmsType) {
            case POSTGRES_DBMS:
                return "jdbc:postgresql://";
            case MSSQL_DBMS:
                if (MS_SQL_2005.equals(AppContext.getProperty(DbmsType.getVersion(storeName)))) {
                    return "jdbc:jtds:sqlserver://";
                }
                return "jdbc:sqlserver://";
            case ORACLE_DBMS:
                return "jdbc:oracle:thin:@//";
            case MYSQL_DBMS:
                return "jdbc:mysql://";
            case HSQL_DBMS:
                return "jdbc:hsqldb:hsql://";
            default:
                throw new RuntimeException("dbmsType is unsupported!");
        }
    }

    protected boolean isHikariConfigField(String propertyName) {
        Field[] fields = HikariConfig.class.getDeclaredFields();
        for (Field field : fields) {
            if (propertyName.equals(field.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected Object lookupWithFallback() throws NamingException {
        Object object = super.lookupWithFallback();
        if (object instanceof DataSource) {
            return new ProxyDataSource((DataSource) object);
        } else {
            return object;
        }
    }

    protected String getDSProviderPropertyName() {
        if (storeName != null) {
            return DATASOURCE_PROVIDER_PROPERTY_NAME + "_" + storeName;
        }
        return DATASOURCE_PROVIDER_PROPERTY_NAME;
    }
}
