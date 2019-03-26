
SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = FALSE;
SET client_min_messages = warning;
SET row_security = off;

/*	SCHEMA	*/

--DROP SCHEMA IF EXISTS accounts CASCADE;
CREATE SCHEMA IF NOT EXISTS accounts;
ALTER SCHEMA accounts OWNER TO dbo_admin;
CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;

SET search_path = accounts, pg_catalog;
SET default_tablespace = '';
SET default_with_oids = FALSE;


/*	TABLES	*/

CREATE TABLE accounts.accounts(
  account_id VARCHAR(42) PRIMARY KEY,

  full_name VARCHAR(90) NOT NULL,
  nick_name VARCHAR(90) DEFAULT '',
  description VARCHAR(512) DEFAULT '',
  genre VARCHAR(1) DEFAULT NULL,
  phone VARCHAR(16) DEFAULT '',
  website VARCHAR(256) DEFAULT '',

  address_line_1 VARCHAR (44) DEFAULT '',
  address_line_2 VARCHAR (28) DEFAULT '',
  address_city VARCHAR (34) DEFAULT '',
  address_zip_code VARCHAR (10) DEFAULT '',
  address_state VARCHAR (34) DEFAULT '',
  address_country VARCHAR (34) DEFAULT '',

  createDateTime TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (current_timestamp),
  updateDateTime TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (current_timestamp)
);
ALTER TABLE accounts.accounts OWNER TO dbo_admin;



/*	BASE ROWS	*/

INSERT INTO accounts.accounts
  (account_id, full_name, nick_name, description, genre, phone, website,
   address_line_1, address_line_2, address_city, address_zip_code,
   address_state, address_country)
   VALUES ( '01e3d8d5-1119-4111-b3d0-be6562ca5913',
           'Some User Admin Admin', 'theAdmin', DEFAULT, 'M',
           DEFAULT, DEFAULT, DEFAULT, DEFAULT,
           DEFAULT, DEFAULT, DEFAULT, DEFAULT);

