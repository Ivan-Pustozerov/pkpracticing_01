UPDATE TabulatedFunctions
 SET
 xVals = COALESCE($2, xVals),
 yVals = COALESCE($3, yVals)
 WHERE id = $1;