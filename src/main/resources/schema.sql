--
create TABLE IF NOT EXISTS users
(
    id    int GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  varchar(50)                          NOT NULL,
    email varchar(50)                          NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL unique (email)
);

create TABLE IF NOT EXISTS requests
(
    id            int GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description   varchar(250)                         NOT NULL,
    requester_id  int REFERENCES users (id) ON delete CASCADE,
    creation_date timestamp WITHOUT TIME ZONE          NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id)
);

create TABLE IF NOT EXISTS items
(
    id           int GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name         varchar(50)                          NOT NULL,
    description  varchar(250)                         NOT NULL,
    is_available boolean                              NOT NULL,
    owner_id     int REFERENCES users (id) ON delete RESTRICT,
    request_id   int REFERENCES requests (id) ON delete CASCADE,
    CONSTRAINT pk_items PRIMARY KEY (id)
);

create TABLE IF NOT EXISTS bookings
(
    id         int GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date timestamp WITHOUT TIME ZONE          NOT NULL,
    end_date   timestamp WITHOUT TIME ZONE          NOT NULL,
    item_id    int REFERENCES items (id) ON delete RESTRICT,
    booker_id  int REFERENCES users (id) ON delete RESTRICT,
    status     varchar(100),
    CONSTRAINT pk_booking PRIMARY KEY (id)
);

create TABLE IF NOT EXISTS comments
(
    id           int GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text         varchar(1000)                        NOT NULL,
    item_id      int REFERENCES items (id) ON delete RESTRICT,
    author_id    int REFERENCES users (id) ON delete RESTRICT,
    created_time timestamp WITHOUT TIME ZONE          not null,
    CONSTRAINT pk_comment PRIMARY KEY (id)
);