--CREATE EXTENSION pgcrypto; -- Do before that :  sudo -u postgres psql postgres and after ALTER ROLE <user_name> SUPERUSER;


CREATE USER aaron;
CREATE DATABASE GSAnProcess;
GRANT ALL PRIVILEGES ON DATABASE GSAnProcess TO aaron;
ALTER ROLE aaron WITH PASSWORD '9y7YTA5O';
CONNECT TO GSAnProcess;
CREATE EXTENSION pgcrypto;

DROP TABLE IF EXISTS "task";

CREATE TABLE "task" (
  "task_id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  "hash" varchar(250) unique not null,
  "is_finished" boolean DEFAULT false,
  "errors" boolean DEFAULT false,
  "msg_code" int DEFAULT 0,
  "date" timestamp NOT NULL,
  "email" varchar(250)
);
