CREATE TABLE quizzes(
                        id UUID DEFAULT random_uuid() PRIMARY KEY,  --унікальний id тесту
                        title VARCHAR(100) NOT NULL, --Назва тесту
                        time_limit_minutes INT NOT NULL CHECK (time_limit_minutes > 0), --Час на виконання тесту в хвилинах
                        created_by UUID, --Ідентифікатор користувача, який створив тест
                        CONSTRAINT fk_quiz_admin FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL -- При видаленні користувача, встановити created_by в NULL
);

CREATE INDEX idx_quizzes_title ON quizzes(title);