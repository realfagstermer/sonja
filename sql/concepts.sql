DROP TABLE IF EXISTS groups ;
DROP TABLE IF EXISTS group_memberships ;
DROP TABLE IF EXISTS terms;
DROP TABLE IF EXISTS strings;
DROP TABLE IF EXISTS relationships;
DROP TABLE IF EXISTS concepts;
DROP TABLE IF EXISTS vocabularies;

create table vocabularies
( vocab_id		char(10)		primary key
, marc_value	char(10)		unique
);

insert into vocabularies  values ('REAL', 'noubomn');
insert into vocabularies  values ('UJUR', 'noubjur');

create table concepts 
( concept_id 		serial			primary key
, vocab_id			char(10)		not null	references vocabularies(vocab_id)
, external_id		serial			not null
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
, replaced_by		int				references concepts(concept_id)
, used_by_libs      varchar(200)
, constraint  		valid_type check(concept_type in ('general','form','time','place'))
, unique			(vocab_id,external_id)
);

create table relationships
( relation_id		serial			primary key
, concept1			int				not null	references concepts(concept_id)
, concept2			int				not null	references concepts(concept_id)
, rel_type			char(10)		not null	check(rel_type in ('related','broader','equivalent'))
, created			timestamp(0)	not null	default CURRENT_TIMESTAMP
, modified			timestamp(0)
, created_by		varchar(100)
, modified_by		varchar(100)
, unique			(concept1, concept2)
);

create table strings
( string_id		serial			primary key
, vocab_id		char(10)		not null	references vocabularies(vocab_id)
, created		timestamp(0)	not null	default CURRENT_TIMESTAMP
, modified		timestamp(0)
, created_by	varchar(100)
, modified_by	varchar(100)
, topic			int				not null	references concepts(concept_id)
, subtopic		int							references concepts(concept_id)
, form			int							references concepts(concept_id)
, temporal		int							references concepts(concept_id)
, geographic	int							references concepts(concept_id)
);