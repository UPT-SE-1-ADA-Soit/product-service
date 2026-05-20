CREATE TABLE category (
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE attribute (
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE attribute_value (
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE product (
    id           SERIAL PRIMARY KEY,
    name         VARCHAR(255)   NOT NULL,
    category_id  INT            NOT NULL REFERENCES category(id),
    price        NUMERIC(10, 2) NOT NULL,
    description  TEXT,
    added_date   DATE           NOT NULL DEFAULT CURRENT_DATE,
    region       VARCHAR(100)   NOT NULL
);

CREATE TABLE attribute_value_pair (
    id                 SERIAL PRIMARY KEY,
    attribute_id       INT NOT NULL REFERENCES attribute(id),
    attribute_value_id INT NOT NULL REFERENCES attribute_value(id),
    product_id         INT NOT NULL REFERENCES product(id)
);

-- "user" is a reserved word in PostgreSQL, so it is quoted
CREATE TABLE "user" (
    id            SERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    phone         VARCHAR(50),
    -- Store a bcrypt hash here, never a plain-text password
    password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE listed_products (
    id         SERIAL PRIMARY KEY,
    user_id    INT NOT NULL REFERENCES "user"(id),
    product_id INT NOT NULL REFERENCES product(id)
);

CREATE TABLE history (
    id         SERIAL PRIMARY KEY,
    user_id    INT NOT NULL REFERENCES "user"(id),
    product_id INT NOT NULL REFERENCES product(id)
);

CREATE TABLE message (
    id       SERIAL PRIMARY KEY,
    user1_id INT  NOT NULL REFERENCES "user"(id),
    user2_id INT  NOT NULL REFERENCES "user"(id),
    message  TEXT NOT NULL
);
