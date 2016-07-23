--DROP TABLE IF EXISTS users;

CREATE TABLE users
( user_id               SERIAL          PRIMARY KEY
, created               TIMESTAMP(0)    NOT NULL      DEFAULT CURRENT_TIMESTAMP
, created_by            INT                           REFERENCES users(user_id)
, username              VARCHAR(20)     NOT NULL
, domain                VARCHAR(100)    NOT NULL
, name                  VARCHAR(100)
, UNIQUE(username, domain)
);