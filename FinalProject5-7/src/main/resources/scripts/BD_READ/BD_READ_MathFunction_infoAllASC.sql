SELECT * FROM MathFunctions
ORDER BY
 CASE ?
         WHEN 'name_asc' THEN name
         WHEN 'type_asc' THEN type
     END ASC,
 id ASC;