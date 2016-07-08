DROP TABLE IF EXISTS groups ;
DROP TABLE IF EXISTS group_memberships ;

CREATE TABLE groups 
( group_id             SERIAL          PRIMARY KEY
, vocab_id             CHAR(10)        NOT NULL      REFERENCES vocabularies(vocab_id)
, created              TIMESTAMP(0)    NOT NULL      DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE group_memberships 
( group_membership_id  SERIAL          PRIMARY KEY
, group_id             INT             NOT NULL      REFERENCES groups(concept_group_id)
, concept_id           INT             NOT NULL      REFERENCES concepts(concept_id)
, created              TIMESTAMP(0)    NOT NULL      DEFAULT CURRENT_TIMESTAMP
, UNIQUE(group_id, concept_id)
);
