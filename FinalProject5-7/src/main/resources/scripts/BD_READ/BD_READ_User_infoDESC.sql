SELECT * FROM Users WHERE id = ANY(?::BIGINT[]) OR name = ANY(?::VARCHAR(50)[])
 ORDER BY
 CASE ?
         WHEN 'id_desc' THEN id
         WHEN 'is_admin_desc' THEN is_admin::int
     END DESC,
 name DESC;