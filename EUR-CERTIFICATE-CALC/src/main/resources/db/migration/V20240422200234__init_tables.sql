CREATE TABLE IF NOT EXISTS users
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    username character varying COLLATE pg_catalog."default",
    password character varying COLLATE pg_catalog."default",
    CONSTRAINT users_pkey PRIMARY KEY (id)
    )
    TABLESPACE pg_default;

ALTER TABLE IF EXISTS users
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS attachments
(
    id        uuid
    constraint attachments_pk
    primary key,
    file_name varchar,
    file_type varchar,
    data      bigint
);

alter table attachments
    owner to postgres;

CREATE TABLE IF NOT EXISTS types(
                                    type_id bigint
                                    constraint types_pk
                                    primary key,
                                    typeName varchar
);

alter table types
    owner to postgres;


CREATE TABLE IF NOT EXISTS attachments_types
(
    attachment_id uuid,
    type_id bigint,
    CONSTRAINT attachments_types_type_id_fkey FOREIGN KEY (type_id)
    REFERENCES types (type_id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE CASCADE,
    CONSTRAINT attachments_types_attachment_id_fkey FOREIGN KEY (attachment_id)
    REFERENCES attachments (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE CASCADE
    )

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS attachments_types
    OWNER to postgres;
