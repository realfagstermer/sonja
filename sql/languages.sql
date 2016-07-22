--DROP TABLE IF EXISTS group_memberships ;
--DROP TABLE IF EXISTS groups ;
--DROP TABLE IF EXISTS mappings ;
--DROP TABLE IF EXISTS external_vocabularies ;
--DROP TABLE IF EXISTS terms;
--DROP TABLE IF EXISTS strings;
--DROP TABLE IF EXISTS relationships;
--DROP TABLE IF EXISTS concepts;
--DROP TABLE IF EXISTS vocabularies;
--DROP TABLE IF EXISTS languages;

create table languages
( lang_id 		VARCHAR(3)	    primary key
, label_nb		varchar(100)	not null
, label_nn		varchar(100)	not null
, label_en		varchar(100)	not null
);

insert into languages values ('nb','Bokmål','Bokmål','Norwegian Bokmål');
insert into languages values ('nn','Nynorsk','Nynorsk','Norwegian Nynorsk');
insert into languages values ('en','Engelsk','Engelsk','English');
insert into languages values ('la','Latin','Latin','Latin');
