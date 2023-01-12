CREATE TABLE IF NOT EXISTS stats
(
    id        bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    app       VARCHAR(255)                                    NOT NULL,
    uri       VARCHAR(255)                                    NOT NULL,
    ip        VARCHAR(255)                                    NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE                     NOT NULL
);