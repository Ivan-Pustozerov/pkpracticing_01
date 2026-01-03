Update UserStatistic
 SET avrg_time_day = ?
 WHERE id = (SELECT id FROM Users WHERE name = ?);
