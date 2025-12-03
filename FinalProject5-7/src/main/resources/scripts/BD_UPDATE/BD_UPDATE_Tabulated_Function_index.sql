UPDATE TabulatedFunctions
 SET
 xVals[$2] = COALESCE(NULLIF($3,'NaN'::float8[]), xVals[$2]),
 yVals[$4] = COALESCE(NULLIF($5,'NaN'::float8[]), yVals[$4])
 WHERE id = $1;