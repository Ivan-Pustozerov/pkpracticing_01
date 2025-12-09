SELECT * FROM Users
ORDER BY
 CASE ?
    WHEN 'id_desc' THEN id
    WHEN 'is_admin_desc' THEN is_admin::int
   END DESC,
 name DESC;