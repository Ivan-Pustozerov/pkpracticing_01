INSERT INTO AnalyticFunctions (func_id, function_expression) VALUES (?, ?)
 ON CONFLICT (func_id) DO NOTHING;