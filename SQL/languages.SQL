create table languages
( lang_id 		char(3)			primary key
, label_nb		varchar(100)	not null
, label_nn		varchar(100)	not null
, label_en		varchar(100)	not null
);

insert into languages values ('nb','Bokmål','Bokmål','Norwegian');
insert into languages values ('nn','Nynorsk','Nynorsk','Norwegian Nynorsk');
insert into languages values ('en','Engelsk','Engelsk','English');
insert into languages values ('la','Latin','Latin','Latin');
