UPDATE MathFunctions
 SET
 name = COALESCE(?, name)
 WHERE id = ? AND owner_id = ?;