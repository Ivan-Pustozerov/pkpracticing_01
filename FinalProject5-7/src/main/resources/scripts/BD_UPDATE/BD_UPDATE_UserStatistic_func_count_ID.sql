Update UserStatistic
 SET func_count = (SELECT COUNT(*) FROM Users WHERE id = ?)
 WHERE id = ?;