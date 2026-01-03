Update UserStatistic
 SET func_count = (SELECT COUNT(*) FROM Users WHERE name = ?)
 WHERE id = (SELECT id FROM Users WHERE name = ?);