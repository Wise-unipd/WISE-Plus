CREATE TYPE transaction_type AS ENUM (
    'rent',
    'extension',
    'refund'
    );

CREATE TABLE AppUser
(
    id          SERIAL PRIMARY KEY,
    password    varchar,
    email       varchar NOT NULL UNIQUE,
    first_name  varchar,
    last_name   varchar,
    role        int,
    created     timestamp,
    last_update timestamp,
    is_active   boolean NOT NULL default true
);

CREATE TABLE MagicLinks
(
    id      serial PRIMARY KEY,
    user_id int,
    token   varchar UNIQUE,
    created timestamp,
    exp     timestamp
);

CREATE TABLE Likes
(
    user_id  int,
    movie_id int,
    PRIMARY KEY (user_id, movie_id)
);

CREATE TABLE Wishlist
(
    user_id  int,
    movie_id int,
    added_at timestamp DEFAULT now(),
    PRIMARY KEY (user_id, movie_id)
);

CREATE TABLE UserRole
(
    id          serial PRIMARY KEY,
    description varchar,
    created     timestamp,
    last_update timestamp,
    is_active   boolean NOT NULL DEFAULT true
);

CREATE TABLE Rent
(
    id        serial PRIMARY KEY,
    user_id   int,
    movie_id  int,
    checkout  timestamp,
    is_active boolean NOT NULL DEFAULT true
);

CREATE TABLE Rent_Expiration
(
    id         serial PRIMARY KEY,
    rent       int,
    expiration timestamp,
    created    timestamp
);

CREATE TABLE Transactions
(
    id      serial PRIMARY KEY,
    rent    int,
    amount  real,
    type    transaction_type,
    created timestamp
);

-- DRAFT FOR DB CHECK!!!!!
CREATE TABLE DB_CHECKS
(
    id        SERIAL PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Movie
(
    movie_id       SERIAL PRIMARY KEY,
    title          VARCHAR(200)   NOT NULL,
    synopsis       TEXT           NOT NULL,
    release_year   INT            NOT NULL,
    running_time   INT            NOT NULL,
    rating         INT            NOT NULL,
    base_price_eur DECIMAL(10, 2) NOT NULL,
    poster_pic     VARCHAR(150)   NOT NULL,
    trailer        VARCHAR(150)   NOT NULL,
    is_active      BOOLEAN        NOT NULL DEFAULT false
);

CREATE TABLE Genre
(
    genre_id SERIAL PRIMARY KEY,
    name     VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE HasGenre
(
    movie_id INT,
    genre_id INT,
    PRIMARY KEY (movie_id, genre_id)
);

CREATE TABLE AgeRating
(
    rating_id   SERIAL PRIMARY KEY,
    name        VARCHAR(50) UNIQUE NOT NULL,
    description TEXT               NOT NULL,
    age         INT                NOT NULL
);

CREATE TABLE Language
(
    lang_id SERIAL PRIMARY KEY,
    name    VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE Audio
(
    movie_id INT,
    lang     INT,
    PRIMARY KEY (movie_id, lang)
);

CREATE TABLE Subtitle
(
    movie_id INT,
    lang     INT,
    PRIMARY KEY (movie_id, lang)
);

CREATE TABLE Worker
(
    worker_id SERIAL PRIMARY KEY,
    name      VARCHAR(50) NOT NULL,
    surname   VARCHAR(50) NOT NULL,
    CONSTRAINT worker_name_surname_unique UNIQUE (name, surname)
);

CREATE TABLE MovieRole
(
    role_id SERIAL PRIMARY KEY,
    name    VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE Credit
(
    movie_id  INT,
    worker_id INT,
    role_id   INT,
    PRIMARY KEY (movie_id, worker_id, role_id)
);

CREATE TABLE Category
(
    category_id SERIAL PRIMARY KEY,
    name        VARCHAR(100) UNIQUE NOT NULL,
    description TEXT                NOT NULL,
    is_active   BOOLEAN             NOT NULL DEFAULT false
);

CREATE TABLE HasCategory
(
    movie_id    INT,
    category_id INT,
    PRIMARY KEY (movie_id, category_id)
);

-- VIEWS
CREATE VIEW movie_with_rating AS
SELECT m.*,
       ar.name        AS rating_name,
       ar.description AS rating_description,
       ar.age         AS rating_age
FROM Movie AS m
         INNER JOIN Agerating AS ar ON m.rating = ar.rating_id;

ALTER TABLE AppUser
    ADD FOREIGN KEY (role) REFERENCES UserRole (id) DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE Likes
    ADD FOREIGN KEY (user_id) REFERENCES AppUser (id) DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE Likes
    ADD FOREIGN KEY (movie_id) REFERENCES Movie (movie_id) DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE Wishlist
    ADD FOREIGN KEY (user_id) REFERENCES AppUser (id) DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE Wishlist
    ADD FOREIGN KEY (movie_id) REFERENCES Movie (movie_id) DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE Rent
    ADD FOREIGN KEY (user_id) REFERENCES AppUser (id) DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE Rent
    ADD FOREIGN KEY (movie_id) REFERENCES Movie (movie_id) DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE Rent_Expiration
    ADD FOREIGN KEY (rent) REFERENCES Rent (id) DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE Transactions
    ADD FOREIGN KEY (rent) REFERENCES Rent (id) DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE Credit
    ADD FOREIGN KEY (role_id) REFERENCES MovieRole (role_id) DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE HasCategory
    ADD FOREIGN KEY (movie_id) REFERENCES Movie (movie_id) DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE Movie
    ADD FOREIGN KEY (rating) REFERENCES AgeRating (rating_id) DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE HasGenre
    ADD FOREIGN KEY (movie_id) REFERENCES Movie (movie_id) DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE HasGenre
    ADD FOREIGN KEY (genre_id) REFERENCES Genre (genre_id) DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE Credit
    ADD FOREIGN KEY (movie_id) REFERENCES Movie (movie_id) DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE Credit
    ADD FOREIGN KEY (worker_id) REFERENCES Worker (worker_id) DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE Credit
    ADD FOREIGN KEY (role_id) REFERENCES MovieRole (role_id) DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE HasCategory
    ADD FOREIGN KEY (movie_id) REFERENCES Movie (movie_id) DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE HasCategory
    ADD FOREIGN KEY (category_id) REFERENCES Category (category_id) DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE Audio
    ADD FOREIGN KEY (movie_id) REFERENCES Movie (movie_id) DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE Audio
    ADD FOREIGN KEY (lang) REFERENCES Language (lang_id) DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE Subtitle
    ADD FOREIGN KEY (movie_id) REFERENCES Movie (movie_id) DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE Subtitle
    ADD FOREIGN KEY (lang) REFERENCES Language (lang_id) DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE MagicLinks
    ADD FOREIGN KEY (user_id) REFERENCES AppUser (id) DEFERRABLE INITIALLY IMMEDIATE;
