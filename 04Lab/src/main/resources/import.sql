
DELETE FROM tabulatedfunctions;
DELETE FROM analyticfunctions;
DELETE FROM mathfunctions;
DELETE FROM users;

INSERT INTO users (name, email, password, is_admin)
VALUES ('Admin', 'admin@test.com', '$2a$10$6D2MrlUh8gHjVWQ2xolYeuxXJ7oBm8Y6q9VnWYkLpZfQwN1mKj5bC', true);