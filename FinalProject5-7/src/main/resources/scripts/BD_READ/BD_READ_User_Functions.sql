SELECT mf.*
 FROM MathFunctions mf
 JOIN Users us ON mf.owner_id = us.id
 WHERE us.id = ANY(?::BIGINT[]) OR us.id IN (SELECT id FROM Users u2 WHERE u2.name = ANY(?::VARCHAR(50)[]));