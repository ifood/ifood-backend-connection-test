--liquibase formatted sql

--changeset jamilson-antunes:0

CREATE TABLE if NOT EXISTS restaurant
(
  id bigint NOT NULL,
  code character varying(50) NOT NULL,
  "name" character varying(255) NOT NULL,

  CONSTRAINT restaurant_pkey PRIMARY KEY (id),
  CONSTRAINT restaurante_name_uk UNIQUE ("name")
);

CREATE SEQUENCE restaurant_id_seq START 1;

ALTER TABLE restaurant ALTER COLUMN id SET DEFAULT nextval('restaurant_id_seq'::regclass);

CREATE TABLE if NOT EXISTS signal_history
(
  id BIGSERIAL PRIMARY KEY,
	restaurant_id bigint NOT NULL,
	received_signal timestamp NOT NULL,

  CONSTRAINT restaurant_signal_history_fk FOREIGN KEY (restaurant_id)
    REFERENCES restaurant (id)
);

CREATE TABLE if NOT EXISTS status_schedule
(
  id BIGSERIAL PRIMARY KEY,
	restaurant_id bigint NOT NULL,
	reason character varying(50) NOT NULL,
	start_datetime timestamp NOT NULL,
	end_datetime timestamp NOT NULL,
	schedule_code character varying(50) NOT NULL,

  CONSTRAINT restaurant_status_schedule_fk FOREIGN KEY (restaurant_id)
    REFERENCES restaurant (id)
);