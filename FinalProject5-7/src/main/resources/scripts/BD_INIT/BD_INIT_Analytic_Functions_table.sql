CREATE TABLE AnalyticFunctions
(func_id BIGINT PRIMARY KEY REFERENCES MathFunctions(id) ON DELETE CASCADE,
function_expression TEXT NOT NULL);