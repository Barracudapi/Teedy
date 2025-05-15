-- DBUPDATE-031-0.SQL

-- Insert a new setting for OCR recognition
insert into T_CONFIG (CFG_ID_C, CFG_VALUE_C) values ('OCR_ENABLED', 'true');

-- Update the database version
update T_CONFIG set CFG_VALUE_C = '31' where CFG_ID_C = 'DB_VERSION';

create table USER_ACTIVITY (ID varchar(36) not null, USER_ID varchar(36) not null, ENTITY_ID varchar(36), PROGRESS int not null default 0, DEADLINE timestamp, COMPLETED_DATE timestamp, CREATE_DATE timestamp not null, DELETE_DATE timestamp, primary key (ID));

alter table USER_ACTIVITY add constraint FK_USER_ID foreign key (USER_ID) references T_USER (USE_ID_C) on delete restrict on update restrict;

create index IDX_USER_ID on USER_ACTIVITY (USER_ID);
create index IDX_CREATE_DATE on USER_ACTIVITY (CREATE_DATE);
