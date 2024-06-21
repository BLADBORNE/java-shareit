DROP TABLE IF EXISTS
    users;

CREATE TABLE IF NOT EXISTS users
(
    id    INTEGER GENERATED AlWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    name  VARCHAR(255)                                     NOT NULL,
    email VARCHAR(512)                                     NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS items_requests
(
    id           INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description  VARCHAR(512)                NOT NULL CHECK (description <> ''),
    requestor_id INTEGER                     NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS items
(
    id           INTEGER GENERATED AlWAYS AS IDENTITY PRIMARY KEY,
    name         VARCHAR(255) NOT NULL CHECK (name <> ''),
    description  VARCHAR(512) NOT NULL CHECK (name <> ''),
    is_available BOOLEAN      NOT NULL,
    owner_id     INTEGER      NOT NULL REFERENCES users (id) ON DELETE CASCADE
    -- request_id   INTEGER      NOT NULL REFERENCES items_requests (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id    INTEGER                     NOT NULL REFERENCES items (id) ON DELETE CASCADE,
    booker_id  INTEGER                     NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    status     VARCHAR(30)                 NOT NULL CHECK (status IN ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED'))
);

CREATE TABLE IF NOT EXISTS comments
(
    id        INTEGER GENERATED AlWAYS AS IDENTITY PRIMARY KEY,
    author_id INTEGER                     NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    item_id   INTEGER                     NOT NULL REFERENCES items (id) ON DELETE CASCADE,
    text      VARCHAR(512)                NOT NULL CHECK (text <> ''),
    created   TIMESTAMP WITHOUT TIME ZONE NOT NULL
);