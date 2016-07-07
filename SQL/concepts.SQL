drop table terms;
drop table strings;
drop table relationships;
drop table concepts;
drop table vocabularies;

create table vocabularies
( vocab_ID		char(10)		primary key
, marc_value	char(10)		unique
);

insert into vocabularies  values ('REAL', 'noubomn');
insert into vocabularies  values ('UJUR', 'noubjur');

create table concepts 
( concept_ID 		serial			primary key
, vocab_ID			char(10)		not null	references vocabularies(vocab_ID)
, external_ID		serial			not null
, concept_type 		char(10)		not null
, note				text
, editorial_note	text
, scope_note		text
, created			timestamp(0)	not null	default CURRENT_TIMESTAMP
, modified			timestamp(0)
, deprecated		timestamp(0)
, created_by		varchar(100)
, modified_by		varchar(100)
, deprecated_by		varchar(100)
, definition		text
, replaced_by		int				references concepts(concept_ID)
, used_by_libs      varchar(200)
, constraint  		valid_type check(concept_type in ('general','form','time','place'))
, unique			(vocab_ID,external_ID)
);

create table relationships
( relation_ID		serial			primary key
, concept1			int				not null	references concepts(concept_ID)
, concept2			int				not null	references concepts(concept_ID)
, rel_type			char(10)		not null	check(rel_type in ('related','broader','equivalent'))
, created			timestamp(0)	not null	default CURRENT_TIMESTAMP
, modified			timestamp(0)
, created_by		varchar(100)
, modified_by		varchar(100)
, unique			(concept1, concept2)
);

create table strings
( string_ID		serial			primary key
, vocab_ID		char(10)		not null	references vocabularies(vocab_ID)
, created		timestamp(0)	not null	default CURRENT_TIMESTAMP
, modified		timestamp(0)
, created_by	varchar(100)
, modified_by	varchar(100)
, topic			int				not null	references concepts(concept_ID)
, subtopic		int							references concepts(concept_ID)
, form			int							references concepts(concept_ID)
, temporal		int							references concepts(concept_ID)
, geographic	int							references concepts(concept_ID)
);