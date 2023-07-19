merge into MPA_RATINGS (mpa_id, name, description)
    values (1, 'G', 'у фильма нет возрастных ограничений.');
merge into MPA_RATINGS (mpa_id, name, description)
    values (2, 'PG', 'детям рекомендуется смотреть фильм с родителями.');
merge into MPA_RATINGS (mpa_id, name, description)
    values (3, 'PG-13', 'детям до 13 лет просмотр не желателен.');
merge into MPA_RATINGS (mpa_id, name, description)
    values (4, 'R', 'лицам до 17 лет просматривать фильм можно только в присутствии взрослого.');
merge into MPA_RATINGS (mpa_id, name, description)
    values (5, 'NC-17', 'лицам до 18 лет просмотр запрещён.');

merge into GENRES (genre_id, name)
    values (1, 'Комедия');
merge into GENRES (genre_id, name)
    values (2, 'Драма');
merge into GENRES (genre_id, name)
    values (3, 'Мультфильм');
merge into GENRES (genre_id, name)
    values (4, 'Триллер');
merge into GENRES (genre_id, name)
    values (5, 'Документальный');
merge into GENRES (genre_id, name)
    values (6, 'Боевик');
