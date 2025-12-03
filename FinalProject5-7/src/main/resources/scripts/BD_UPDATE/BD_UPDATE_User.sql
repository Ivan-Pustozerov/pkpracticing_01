UPDATE FROM Users
 SET
 is_admin = COALESCE($2, is_admin),
 name = COALESCE($3, name),
 email = COALESCE($4, email),
 password = COALESCE($5, password)
 WHERE id = $1;