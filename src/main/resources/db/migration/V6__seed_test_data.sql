-- Додавання трьох команд
INSERT INTO teams (team_name)
VALUES ('Команда А'), ('Команда Б'), ('Команда В');

-- Додавання адміністратора
INSERT INTO users (first_name, last_name, email, password_hash, role)
VALUES ('Денис', 'Кізляк', 'kizljakdenja@gmail.com', 'hashed_password', 'admin');

-- Додавання гравця до команди А
INSERT INTO users (first_name, last_name, email, password_hash, role, team_id)
VALUES (
           'Гравець',
           '1',
           'player1@gmail.com',
           'hashed_password',
           'user',
           (SELECT id FROM teams WHERE team_name = 'Команда А')
       );

-- Додавання тесту
INSERT INTO quizzes (title, time_limit_minutes, created_by)
VALUES (
           'Основи Java',
           30,
           (SELECT id FROM users WHERE email = 'kizljakdenja@gmail.com')
       );

-- Додавання питання
INSERT INTO questions (quiz_id, content, options_a, options_b, options_c, options_d, correct_option)
VALUES (
           (SELECT id FROM quizzes WHERE title = 'Основи Java'),
           'Який тип даних використовується для цілих чисел у Java?',
           'int',
           'String',
           'boolean',
           'double',
           'A'
       );

-- Додавання результату
INSERT INTO results (team_id, quiz_id, score, time_spent_seconds)
VALUES (
           (SELECT id FROM teams WHERE team_name = 'Команда А'),
           (SELECT id FROM quizzes WHERE title = 'Основи Java'),
           75,
           1200
       );