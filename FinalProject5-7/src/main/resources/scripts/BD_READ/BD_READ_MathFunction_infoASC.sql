SELECT * FROM MathFunctions WHERE id = ANY(?::BIGINT[])
 ORDER BY
 CASE ?
         WHEN 'name_asc' THEN name
         WHEN 'type_asc' THEN type
     END ASC,
 id ASC;