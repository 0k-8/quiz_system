-- 3NF -  Таблиця для збереження інформації про команди
CREATE TABLE teams (
    id UUID PRIMARY KEY DEFAULT random_uuid(),-- Унікальний ідентифікатор команди
    team_name VARCHAR(100) NOT NULL UNIQUE, -- Назва команди
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Дата та час створення запису
);
-- 3NF - Таблиця для збереження інформації про гравців
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT random_uuid(), -- Унікальний ідентифікатор гравця
    full_name VARCHAR(100) NOT NULL, -- Повне ім'я гравця
    email VARCHAR(100) NOT NULL UNIQUE, -- Електронна пошта гравця
    password_hash VARCHAR(255) NOT NULL, -- Хеш пароля гравця
    role VARCHAR(10) DEFAULT 'user', -- Роль гравця (user або admin)
    team_id UUID, -- Ідентифікатор команди, до якої належить гравець
    CONSTRAINT fk_user_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE SET NULL -- При видаленні команди, встановити team_id в NULL
);
-- 3NF - Таблиця для збереження інформації про тести
CREATE TABLE quizzes(
    id UUID PRIMARY KEY DEFAULT random_uuid(), --унікальний id тесту
    title VARCHAR(100) NOT NULL, --Назва тесту
    time_limit_minutes INT NOT NULL CHECK (time_limit_minutes > 0), --Час на виконання тесту в хвилинах
    created_by UUID, --Ідентифікатор користувача, який створив тест
    CONSTRAINT fk_quiz_admin FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL -- При видаленні користувача, встановити created_by в NULL
);
-- 3NF - Таблиця для збереження інформації про запитання
CREATE TABLE questions (
    id UUID PRIMARY KEY DEFAULT random_uuid(), -- унікальний id запитання
    quiz_id UUID, -- ідентифікатор тесту, до якого належить запитання
    content TEXT NOT NULL, -- текст запитання
    options_a VARCHAR(255) NOT NULL, -- варіант відповіді A
    options_b VARCHAR(255) NOT NULL, -- варіант відповіді B
    options_c VARCHAR(255) NOT NULL, -- варіант відповіді C
    options_d VARCHAR(255) NOT NULL, -- варіант відповіді D
    correct_option CHAR(1) NOT NULL CHECK (correct_option IN ('A', 'B', 'C', 'D')), -- правильний варіант відповіді
    CONSTRAINT fk_question_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE -- При видаленні тесту, видалити всі запитання, що належать цьому тесту
);
-- 3NF - Таблиця для збереження інформації про результати тестування
CREATE TABLE results (
    id UUID PRIMARY KEY DEFAULT random_uuid(), -- унікальний id результату
    team_id UUID, -- ідентифікатор команди яка пройшла тестування 
    quiz_id UUID, -- індутифікатор тесту який проходила команда 
    score INT NOT NULL, -- кількість балів яка набрала команда 
    time_spent_seconds INT NOT NULL, -- час який команда витратила на проходження тесту
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- дата та час завершення тестування 
    -- Звязки 
    CONSTRAINT fk_result_team FOREIGN KEY(team_id) REFERENCES teams(id) ON DELETE SET NULL, -- при видаленні комадни встановити team_id в null
    CONSTRAINT fk_result_quiz FOREIGN KEY(quiz_id) REFERENCES quizzes(id) ON DELETE SET NULL -- при видаленні тесту встановити quiz_id в null
);
Q -- Індекс для швидкого пошуку користувачів за електронною поштою
CREATE INDEX idx_quizzes_title ON quizzes(title); -- Індекс для швидкого пошуку тестів за назвою
CREATE INDEX idx_results_score ON results(score); -- Індекс для швидкого пошуку результатів за балами
COMMIT; -- Підтвердження транзакції для збереження змін у базі даних
