DROP TABLE IF EXISTS terms;

create table terms 
( term_id 				serial 			primary key
, term_created			timestamp(0)	not null	default CURRENT_TIMESTAMP
, term_modified			timestamp(0)
, term_created_by		INT                         REFERENCES users(user_id)
, term_modified_by		INT                         REFERENCES users(user_id)
, status				VARCHAR(10)		not null
, concept_id			int				not null	references concepts(concept_id) ON DELETE CASCADE
, lexical_value			varchar(500)    NOT NULL
, lang_id				VARCHAR(3)		not null	references languages(lang_id)
, constraint  			valid_status	check(status in ('preferred', 'non-pref', 'hidden'))
, unique (concept_id, lang_id, lexical_value)
);

create index lexical_index on terms (lexical_value);