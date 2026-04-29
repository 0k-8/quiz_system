INSERT INTO teams (team_name) VALUES ('Команда А'), ('Команда Б'), ('Команда В'); -- Додавання трьох команд
INSERT INTO users (full_name, email, password_hash, role) VALUES ('Денис', 'kizljakdenja@gmail.com', 'hashed_password', 'admin'); -- Додавання адміністратора
INSERT INTO users (full_name, email, password_hash, role, team_id) VALUES ('Гравець 1', 'player1@gmail.com', 'hashed_password', 'user',
                                                                           (SELECT id FROM teams WHERE team_name = 'Команда А')); -- Додавання гравця до команди А
INSERT INTO quizzes (title, time_limit_minutes, created_by) VALUES ('Основи Java',  30,
                                                                    (SELECT id FROM users WHERE email = 'kizljakdenja@gmail.com')); -- Додавання тесту "Основи Java", створеного адміністратором

INSERT INTO questions (quiz_id, content, options_a, options_b, options_c, options_d, correct_option)
VALUES (
           (SELECT id FROM quizzes WHERE title = 'Основи Java'), -- Знаходимо ID тесту
           'Який тип даних використовується для цілих чисел у Java?', -- Текст питання
           'int',     -- Варіант A
           'String',  -- Варіант B
           'boolean', -- Варіант C
           'double',  -- Варіант D
           'A'        -- Правильна відповідь
       );
INSERT INTO results (team_id, quiz_id, score, time_spent_seconds) VALUES (
                                                                             (SELECT id FROM teams WHERE team_name = 'Команда А'), -- Знаходимо ID команди
                                                                             (SELECT id FROM quizzes WHERE title = 'Основи Java'), -- Знаходимо ID тесту
                                                                             75,  -- Кількість балів
                                                                             1200 -- Час у секундах (20 хвилин)
                                                                         );