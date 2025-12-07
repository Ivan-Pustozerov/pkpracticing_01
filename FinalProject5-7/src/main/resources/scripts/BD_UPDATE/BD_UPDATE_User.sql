UPDATE Users
 SET
 is_admin = COALESCE(?, is_admin),
 name = COALESCE(?, name),
 email = COALESCE(?, email),
 password = COALESCE(?, password)
 WHERE id = ? OR name = ?;