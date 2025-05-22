-- Add table to track user registration
create table T_REGISTRATION (REG_ID_C varchar(36) not null, REG_TOKEN_C varchar(100) not null, REG_IP_C varchar(45) not null, REG_CREATED_AT_D datetime not null, REG_USERNAME_C varchar(50) not null, REG_PASSWORD_C varchar(50) not null, REG_STATUS_C varchar(20) not null, primary key (REG_ID_C));

-- Add table to track user activities for Gantt chart
create table T_USER_ACTIVITY (UTA_ID_C varchar(36) not null, UTA_IDUSER_C varchar(36) not null, UTA_ENTITY_ID_C varchar(36), UTA_PROGRESS_N int not null default 0, UTA_PLANNED_DATE_D timestamp, UTA_COMPLETED_DATE_D timestamp, UTA_CREATEDATE_D timestamp not null, UTA_DELETEDATE_D timestamp, primary key (UTA_ID_C));
alter table T_USER_ACTIVITY add constraint FK_UTA_IDUSER_C foreign key (UTA_IDUSER_C) references T_USER (USE_ID_C) on delete restrict on update restrict;
create index IDX_UTA_IDUSER_C on T_USER_ACTIVITY (UTA_IDUSER_C);
create index IDX_UTA_CREATEDATE_D on T_USER_ACTIVITY (UTA_CREATEDATE_D);

-- Update database version
update T_CONFIG set CFG_VALUE_C = '32' where CFG_ID_C = 'DB_VERSION';
SELECT CFG_VALUE_C FROM T_CONFIG WHERE CFG_ID_C = 'DB_VERSION';
