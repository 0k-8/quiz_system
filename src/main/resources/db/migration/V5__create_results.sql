CREATE TABLE results (
                         id UUID DEFAULT random_uuid() PRIMARY KEY, -- унікальний id результату
                         team_id UUID, -- ідентифікатор команди яка пройшла тестування
                         quiz_id UUID, -- індутифікатор тесту який проходила команда
                         score INT NOT NULL, -- кількість балів яка набрала команда
                         time_spent_seconds INT NOT NULL, -- час який команда витратила на проходження тесту
                         completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- дата та час завершення тестування

                         CONSTRAINT fk_result_team FOREIGN KEY(team_id) REFERENCES teams(id) ON DELETE SET NULL, -- при видаленні комадни встановити team_id в null
                         CONSTRAINT fk_result_quiz FOREIGN KEY(quiz_id) REFERENCES quizzes(id) ON DELETE SET NULL -- при видаленні тесту встановити quiz_id в null
);
CREATE INDEX idx_results_score ON results(score);