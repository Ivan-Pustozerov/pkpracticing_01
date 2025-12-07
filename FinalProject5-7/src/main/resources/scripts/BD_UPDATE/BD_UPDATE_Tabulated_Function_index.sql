UPDATE TabulatedFunctions
 SET
 yVals[?] = COALESCE(NULLIF(?::float8,'NaN'::float8), yVals[?])
 WHERE func_id = ?;