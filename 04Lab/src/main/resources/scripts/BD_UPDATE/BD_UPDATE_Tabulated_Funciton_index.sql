UPDATE TabulatedFunctions
SET
xVals[$2] = COALESCE($3, xVals[$2]),
yVals[$4] = COALESCE($5, yVals[$4])
WHERE id = $1;