UPDATE AnalyticFunctions
 SET function_expression = COALESCE(?, function_expression)
 WHERE func_id = ?;