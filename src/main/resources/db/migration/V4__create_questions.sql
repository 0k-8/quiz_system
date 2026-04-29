CREATE TABLE questions (
                           id UUID DEFAULT random_uuid() PRIMARY KEY, -- унікальний id запитання
                           quiz_id UUID, -- ідентифікатор тесту, до якого належить запитання
                           content TEXT NOT NULL, -- текст запитання
                           options_a VARCHAR(255) NOT NULL, -- варіант відповіді A
                           options_b VARCHAR(255) NOT NULL, -- варіант відповіді B
                           options_c VARCHAR(255) NOT NULL, -- варіант відповіді C
                           options_d VARCHAR(255) NOT NULL, -- варіант відповіді D
                           correct_option CHAR(1) NOT NULL CHECK (correct_option IN ('A', 'B', 'C', 'D')), -- правильний варіант відповіді
                           CONSTRAINT fk_question_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE -- При видаленні тесту, видалити всі запитання, що належать цьому тесту
);