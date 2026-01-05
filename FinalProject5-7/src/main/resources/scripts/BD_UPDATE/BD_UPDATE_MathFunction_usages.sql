UPDATE MathFunctions
 SET
 usages = usages + 1
 WHERE id = ? AND owner_id = ?;