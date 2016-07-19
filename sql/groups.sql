DROP TABLE IF EXISTS group_memberships ;
DROP TABLE IF EXISTS groups ;

CREATE TABLE groups
( group_id             SERIAL          PRIMARY KEY
, vocab_id             CHAR(10)        NOT NULL      REFERENCES vocabularies(vocab_id)
, created              TIMESTAMP(0)    NOT NULL      DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE group_memberships
( group_membership_id  SERIAL          PRIMARY KEY
, group_id             INT             NOT NULL      REFERENCES groups(group_id)
, concept_id           INT             NOT NULL      REFERENCES concepts(concept_id) ON DELETE CASCADE
, created              TIMESTAMP(0)    NOT NULL      DEFAULT CURRENT_TIMESTAMP
, UNIQUE(group_id, concept_id)
);
