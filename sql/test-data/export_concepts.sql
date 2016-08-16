TRUNCATE TABLE terms;
TRUNCATE TABLE relationships;
TRUNCATE TABLE mappings;
truncate table concepts cascade;
INSERT INTO concepts (external_id,vocab_id,concept_type,editorial_note, created, modified, deprecated, definition, used_by_libs) 
VALUES (2,'REAL','general',NULL,'2014-08-25T00:00:00Z',NULL,NULL,NULL,'nk');
INSERT INTO concepts (external_id,vocab_id,concept_type,editorial_note, created, modified, deprecated, definition, used_by_libs) 
VALUES (3,'REAL','general',NULL,'2014-08-25T00:00:00Z',NULL,NULL,NULL,'ni');
INSERT INTO concepts (external_id,vocab_id,concept_type,editorial_note, created, modified, deprecated, definition, used_by_libs) 
VALUES (4,'REAL','general',NULL,'2014-08-25T00:00:00Z',NULL,NULL,NULL,'nk');
INSERT INTO concepts (external_id,vocab_id,concept_type,editorial_note, created, modified, deprecated, definition, used_by_libs) 
VALUES (5,'REAL','general',NULL,'2014-08-25T00:00:00Z',NULL,NULL,NULL,'nm');
INSERT INTO concepts (external_id,vocab_id,concept_type,editorial_note, created, modified, deprecated, definition, used_by_libs) 
VALUES (6,'REAL','general',NULL,'2014-08-25T00:00:00Z',NULL,NULL,NULL,'nb nc');
INSERT INTO concepts (external_id,vocab_id,concept_type,editorial_note, created, modified, deprecated, definition, used_by_libs) 
VALUES (7,'REAL','general',NULL,'2014-08-25T00:00:00Z',NULL,NULL,NULL,'nm');
INSERT INTO concepts (external_id,vocab_id,concept_type,editorial_note, created, modified, deprecated, definition, used_by_libs) 
VALUES (8,'REAL','general',NULL,'2014-08-25T00:00:00Z',NULL,NULL,NULL,'nb nc ngh');
INSERT INTO concepts (external_id,vocab_id,concept_type,editorial_note, created, modified, deprecated, definition, used_by_libs) 
VALUES (10,'REAL','general',NULL,'2014-08-25T00:00:00Z',NULL,NULL,NULL,'nb');
