create table T_WEBHOOK ( WHK_ID_C varchar(36) not null, WHK_EVENT_C varchar(50) not null, WHK_URL_C varchar(1024) not null,  WHK_CREATEDATE_D datetime not null, WHK_DELETEDATE_D datetime, primary key (WHK_ID_C) );
create table guest_user ( ID varchar(36) not null, ACCESS_TOKEN varchar(100) not null, CLIENT_IP varchar(45) not null, CREATED_DATE timestamp not null, STATUS varchar(20) not null, primary key (ID) );

update T_CONFIG set CFG_VALUE_C = '20' where CFG_ID_C = 'DB_VERSION';
