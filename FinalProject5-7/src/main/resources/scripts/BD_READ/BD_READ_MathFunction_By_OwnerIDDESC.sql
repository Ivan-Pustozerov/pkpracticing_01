SELECT * FROM MathFunctions WHERE owner_id = ?
 ORDER BY
 CASE ?
         WHEN 'name_desc' THEN name
         WHEN 'type_desc' THEN type
     END DESC,
 id DESC;