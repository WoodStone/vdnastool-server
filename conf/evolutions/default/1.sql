# Directory SCHEMA

# --- !Ups

CREATE TABLE directory (
  id BIGSERIAL,
  hash VARCHAR(40) NOT NULL,
  path VARCHAR(512) NOT NULL,
  name VARCHAR(512) NOT NULL,
  PRIMARY KEY(id)
);

CREATE TABLE file (
  id BIGSERIAL,
  name VARCHAR(256) NOT NULL,
  mtime FLOAT NOT NULL,
  hash_name VARCHAR(40) NOT NULL,
  hash_mtime VARCHAR(40) NOT NULL,
  hash_combined VARCHAR(40) NOT NULL,
  md5 VARCHAR(32) NOT NULL,
  directory_id BIGINT NOT NULL,
  PRIMARY KEY(id),
  FOREIGN KEY(directory_id) REFERENCES directory(id) ON UPDATE CASCADE
);

# --- !Downs
DROP TABLE file;
DROP TABLE directory;

