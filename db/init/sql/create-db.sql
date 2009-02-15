------------------------------------------------------------------------------------------------------------

create table SYS_SERVER (
    ID varchar(36),
    CREATE_TS timestamp,
    CREATED_BY varchar(20),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(20),
    DELETE_TS timestamp,
    DELETED_BY varchar(20),
    NAME varchar(255),
    ADDRESS varchar(255),
    IS_RUNNING smallint,
    primary key (ID)
);

------------------------------------------------------------------------------------------------------------

create table SYS_CONFIG (
    ID varchar(36),
    CREATE_TS timestamp,
    CREATED_BY varchar(20),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(20),
    NAME varchar(255),
    VALUE varchar(500),
    primary key (ID)
);

alter table SYS_CONFIG add constraint SYS_CONFIG_UNIQ_NAME unique (NAME);

------------------------------------------------------------------------------------------------------------

create table SEC_ROLE (
    ID varchar(36),
    CREATE_TS timestamp,
    CREATED_BY varchar(20),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(20),
    DELETE_TS timestamp,
    DELETED_BY varchar(20),
    NAME varchar(255),
    IS_SUPER smallint,
    primary key (ID)
);

alter table SEC_ROLE add constraint SEC_ROLE_UNIQ_NAME unique (NAME, DELETE_TS);

------------------------------------------------------------------------------------------------------------

create table SEC_GROUP (
    ID varchar(36),
    CREATE_TS timestamp,
    CREATED_BY varchar(20),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(20),
    DELETE_TS timestamp,
    DELETED_BY varchar(20),
    NAME varchar(255),
    PARENT_ID varchar(36),
    primary key (ID)
);

alter table SEC_GROUP add constraint SEC_GROUP_PARENT foreign key (PARENT_ID) references SEC_GROUP(ID);

------------------------------------------------------------------------------------------------------------

create table SEC_GROUP_HIERARCHY (
    ID varchar(36),
    CREATE_TS timestamp,
    CREATED_BY varchar(20),
    GROUP_ID varchar(36),
    PARENT_ID varchar(36),
    LEVEL integer,
    primary key (ID)
);

alter table SEC_GROUP_HIERARCHY add constraint SEC_GROUP_HIERARCHY_GROUP foreign key (GROUP_ID) references SEC_GROUP(ID);

alter table SEC_GROUP_HIERARCHY add constraint SEC_GROUP_HIERARCHY_PARENT foreign key (PARENT_ID) references SEC_GROUP(ID);

------------------------------------------------------------------------------------------------------------

create table SEC_USER (
    ID varchar(36),
    CREATE_TS timestamp,
    CREATED_BY varchar(20),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(20),
    DELETE_TS timestamp,
    DELETED_BY varchar(20),
    LOGIN varchar(20),
    PASSWORD varchar(32),
    NAME varchar(255),
    AD_USER varchar(100),
    GROUP_ID varchar(36),
    primary key (ID)
);

alter table SEC_USER add constraint SEC_USER_UNIQ_LOGIN unique (LOGIN, DELETE_TS);

alter table SEC_USER add constraint SEC_USER_GROUP foreign key (GROUP_ID) references SEC_GROUP(ID);

------------------------------------------------------------------------------------------------------------

create table SEC_USER_ROLE (
    ID varchar(36),
    CREATE_TS timestamp,
    CREATED_BY varchar(20),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(20),
    DELETE_TS timestamp,
    DELETED_BY varchar(20),
    USER_ID varchar(36),
    ROLE_ID varchar(36),
    primary key (ID)
);

alter table SEC_USER_ROLE add constraint SEC_USER_ROLE_PROFILE foreign key (USER_ID) references SEC_USER(ID);

alter table SEC_USER_ROLE add constraint SEC_USER_ROLE_ROLE foreign key (ROLE_ID) references SEC_ROLE(ID);

alter table SEC_USER_ROLE add constraint SEC_USER_ROLE_UNIQ_ROLE unique (USER_ID, ROLE_ID, DELETE_TS);

------------------------------------------------------------------------------------------------------------

create table SEC_PERMISSION (
    ID varchar(36),
    CREATE_TS timestamp,
    CREATED_BY varchar(20),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(20),
    DELETE_TS timestamp,
    DELETED_BY varchar(20),
    TYPE integer,
    TARGET varchar(100),
    VALUE integer,
    ROLE_ID varchar(36),
    primary key (ID)
);

alter table SEC_PERMISSION add constraint SEC_PERMISSION_ROLE foreign key (ROLE_ID) references SEC_ROLE(ID);

alter table SEC_PERMISSION add constraint SEC_PERMISSION_UNIQUE unique (ROLE_ID, TYPE, TARGET, DELETE_TS);

------------------------------------------------------------------------------------------------------------

create table SEC_CONSTRAINT (
    ID varchar(36),
    CREATE_TS timestamp,
    CREATED_BY varchar(20),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(20),
    DELETE_TS timestamp,
    DELETED_BY varchar(20),
    ENTITY_NAME varchar(50),
    WHERE_CLAUSE varchar(500),
    GROUP_ID varchar(36),
    primary key (ID)
);

alter table SEC_CONSTRAINT add constraint SEC_CONSTRAINT_GROUP foreign key (GROUP_ID) references SEC_GROUP(ID);

------------------------------------------------------------------------------------------------------------

insert into SEC_GROUP (ID, CREATE_TS, VERSION, NAME, PARENT_ID)
values ('0fa2b1a5-1d68-4d69-9fbd-dff348347f93', current_timestamp, 0, 'Company', null);

insert into SEC_USER (ID, CREATE_TS, VERSION, LOGIN, PASSWORD, NAME, GROUP_ID)
values ('60885987-1b61-4247-94c7-dff348347f93', current_timestamp, 0, 'admin', '21232f297a57a5a743894a0e4a801fc3', 'Administrator', '0fa2b1a5-1d68-4d69-9fbd-dff348347f93');

insert into SEC_ROLE (ID, CREATE_TS, VERSION, NAME, IS_SUPER)
values ('0c018061-b26f-4de2-a5be-dff348347f93', current_timestamp, 0, 'Administrators', 1);

insert into SEC_USER_ROLE (ID, CREATE_TS, VERSION, USER_ID, ROLE_ID)
values ('c838be0a-96d0-4ef4-a7c0-dff348347f93', current_timestamp, 0, '60885987-1b61-4247-94c7-dff348347f93', '0c018061-b26f-4de2-a5be-dff348347f93');
