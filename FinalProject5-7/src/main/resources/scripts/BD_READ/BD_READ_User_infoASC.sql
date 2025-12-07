SELECT * FROM Users WHERE id = ANY(?::BIGINT[]) OR name = ANY(?::VARCHAR(50)[])
 ORDER BY
 CASE ?
         WHEN 'id_asc' THEN id
         WHEN 'is_admin_asc' THEN is_admin::int
     END ASC,
 name ASC;