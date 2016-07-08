DROP TABLE IF EXISTS terms;

create table terms 
( term_id 				serial 			primary key
, term_created			timestamp(0)	not null	default CURRENT_TIMESTAMP
, term_modified			timestamp(0)
, term_created_by		varchar(100)
, term_modified_by		varchar(100)
, status				char(10)		not null
, concept_id			int				not null	references concepts(concept_id)
, lexical_value			varchar(500)
, lang_id				char(3)			not null	references languages(lang_id)
, constraint  			valid_status	check(status in ('preferred', 'non-pref', 'hidden'))
);

create index lexical_index on terms (lexical_value);