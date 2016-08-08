SELECT pg_catalog.setval(pg_get_serial_sequence('concepts', 'external_id'), MAX(external_id)) FROM concepts;
SELECT pg_catalog.setval(pg_get_serial_sequence('concepts', 'concept_id'), MAX(concept_id)) FROM concepts;
--SELECT pg_catalog.setval(pg_get_serial_sequence('terms', 'term_id'), MAX(term_id)) FROM terms;