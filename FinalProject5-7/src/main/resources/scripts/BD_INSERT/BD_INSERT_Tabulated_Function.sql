INSERT INTO TabulatedFunctions (func_id, xVals, yVals) VALUES (?, ?, ?)
 ON CONFLICT (func_id) DO NOTHING;