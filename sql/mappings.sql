DROP TABLE IF EXISTS mappings ;
DROP TABLE IF EXISTS external_vocabularies ;


CREATE TABLE external_vocabularies
( external_vocabulary_id  CHAR(10)      PRIMARY KEY
, created                 TIMESTAMP(0)  NOT NULL      DEFAULT CURRENT_TIMESTAMP
, name                    VARCHAR(100)  NOT NULL      UNIQUE
, concept_uri_pattern     VARCHAR(255)  NOT NULL
, graph_uri               VARCHAR(255)
, concept_scheme_uri      VARCHAR(255)
, sparql_endpoint         VARCHAR(255)
);

INSERT INTO external_vocabularies (external_vocabulary_id, name, concept_uri_pattern) VALUES ('ddc','Dewey Decimal Classification','http://dewey.info/class/{notation}/e23/');
INSERT INTO external_vocabularies (external_vocabulary_id, name, concept_uri_pattern) VALUES ('msc','Mathematics Subject Classification','');

CREATE TABLE mappings 
( mapping_id              SERIAL        PRIMARY KEY
, created                 TIMESTAMP(0)  NOT NULL      DEFAULT CURRENT_TIMESTAMP
, created_by              INT           NOT NULL      REFERENCES users(user_id)
, verified                TIMESTAMP(0)
, verified_by             INT                         REFERENCES users(user_id)
, source_concept_id       INT           NOT NULL      REFERENCES concepts(concept_id)
, target_concept_id       VARCHAR(50)   NOT NULL
, target_vocabulary_id    CHAR(10)      NOT NULL      REFERENCES external_vocabularies(external_vocabulary_id)
, mapping_relation        CHAR(10)      NOT NULL
, CONSTRAINT VALID_TYPE CHECK(mapping_relation in ('exact','close','broader','narrower','related'))
, UNIQUE (source_concept_id, target_concept_id, target_vocabulary_id, mapping_relation)
);
