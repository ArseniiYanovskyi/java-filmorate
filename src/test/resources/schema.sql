CREATE TABLE IF NOT EXISTS films
(
    film_id
                integer
        generated
            by
            default as
            identity
        primary
            key,
    name
                varchar(50),
    description varchar(200),
    releaseDate timestamp,
    duration    integer,
    RATE        integer,
    MPA         integer
);
CREATE TABLE IF NOT EXISTS mpa_ratings
(
    mpa_id
                integer
        generated
            by
            default as
            identity
        primary
            key,
    name
                varchar(10),
    description varchar(100)
);
CREATE TABLE IF NOT EXISTS films_ratings
(
    film_id integer,
    mpa_id  integer
);
CREATE TABLE IF NOT EXISTS genres
(
    genre_id
        integer
        generated
            by
            default as
            identity
        primary
            key,
    name
        varchar(15)
);
CREATE TABLE IF NOT EXISTS films_genres
(
    film_id  integer,
    genre_id integer
);
CREATE TABLE IF NOT EXISTS users
(
    user_id
             integer
        generated
            by
            default as
            identity
        primary
            key,
    email
             varchar(30),
    login    varchar(30),
    name     varchar(30),
    birthday timestamp
);
CREATE TABLE IF NOT EXISTS friends
(
    user_id   integer,
    friend_id integer
);
CREATE TABLE IF NOT EXISTS likes
(
    film_id integer,
    user_id integer
);
