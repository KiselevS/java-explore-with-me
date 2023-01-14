CREATE TABLE IF NOT EXISTS stats
(
    id        bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    app       VARCHAR(32)                                     NOT NULL,
    uri       VARCHAR(2048)                                   NOT NULL,
    ip        VARCHAR(32)                                     NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE                     NOT NULL
);