
SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = FALSE;
SET client_min_messages = warning;
SET row_security = off;

/*	SCHEMA	*/

--DROP SCHEMA IF EXISTS oauth2 CASCADE;
CREATE SCHEMA IF NOT EXISTS oauth2;
ALTER SCHEMA oauth2 OWNER TO dbo_admin;
CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;

SET search_path = oauth2, pg_catalog;
SET default_tablespace = '';
SET default_with_oids = FALSE;


/*	TABLES	*/

CREATE TABLE oauth2.users(
  user_id VARCHAR(42) PRIMARY KEY,
  active BOOLEAN NOT NULL DEFAULT TRUE,

  email VARCHAR(90) NOT NULL UNIQUE,
  username VARCHAR(50) NOT NULL UNIQUE,
  name VARCHAR(90) NOT NULL,
  last_name VARCHAR(90) DEFAULT '',
  password VARCHAR(60) NOT NULL,

  createDateTime TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (current_timestamp),
  updateDateTime TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (current_timestamp)
);
ALTER TABLE oauth2.users OWNER TO dbo_admin;


CREATE TABLE oauth2.roles(
  role_id INTEGER PRIMARY KEY,
  role VARCHAR(90) NOT NULL
);
ALTER TABLE oauth2.roles OWNER TO dbo_admin;

CREATE TABLE oauth2.user_role(
  user_id VARCHAR(42) NOT NULL REFERENCES  oauth2.users(user_id),
  role_id INTEGER NOT NULL REFERENCES  oauth2.roles(role_id)
);
ALTER TABLE oauth2.user_role OWNER TO dbo_admin;



/*	BASE ROWS	*/

INSERT INTO oauth2.roles VALUES (1, 'ROLE_ADMIN');
INSERT INTO oauth2.roles VALUES (2, 'ROLE_USER');

INSERT INTO oauth2.users
(user_id, active, email, username, name, last_name, password)
VALUES ('01e3d8d5-1119-4111-b3d0-be6562ca5913', true, 'admin@admin.com',
        'admin', 'Some User', 'Admin Admin',

         -- https://www.browserling.com/tools/bcrypt
         /*Hashed with BCCrypt 8 Rounds: some1234password*/
        '$2a$08$.SSWQCEIk1UaWsZhBisWdO/8Qvix8GT/aW9XHkNS6JEJS/pnAXOxO');

INSERT INTO oauth2.user_role VALUES ('01e3d8d5-1119-4111-b3d0-be6562ca5913', 1);
INSERT INTO oauth2.user_role VALUES ('01e3d8d5-1119-4111-b3d0-be6562ca5913', 2);
