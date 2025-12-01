SELECT mf.*
FROM MathFunctions mf
JOIN Users us ON mf.owner_id = us.id
WHERE us.id = ?;