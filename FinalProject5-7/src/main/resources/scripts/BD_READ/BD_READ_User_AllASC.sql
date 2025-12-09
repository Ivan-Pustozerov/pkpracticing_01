SELECT * FROM Users
ORDER BY
 CASE ?
    WHEN 'id_asc' THEN id
    WHEN 'is_admin_asc' THEN is_admin::int
   END ASC,
 name ASC;