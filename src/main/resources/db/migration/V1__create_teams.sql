CREATE TABLE teams (
                       id UUID DEFAULT random_uuid() PRIMARY KEY, -- Унікальний ідентифікатор команди
                       team_name VARCHAR(100) NOT NULL UNIQUE, -- Назва команди
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Дата та час створення запису
);
