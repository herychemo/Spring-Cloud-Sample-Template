
/*	TABLES	*/

CREATE TABLE accounts(
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
