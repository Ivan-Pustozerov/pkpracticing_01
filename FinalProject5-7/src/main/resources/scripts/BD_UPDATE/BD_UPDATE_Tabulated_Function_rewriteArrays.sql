UPDATE TabulatedFunctions
 SET
 xVals = COALESCE(?, xVals),
 yVals = COALESCE(?, yVals)
 WHERE func_id = ?;