Update UserStatistic
 SET all_time = all_time + ?
 WHERE id = (SELECT id FROM Users WHERE name = ?);
