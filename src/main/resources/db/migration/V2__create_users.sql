CREATE TABLE users (
                       id UUID  DEFAULT random_uuid() PRIMARY KEY, -- Унікальний ідентифікатор гравця
                       full_name VARCHAR(100) NOT NULL, -- Повне ім'я гравця
                       email VARCHAR(100) NOT NULL UNIQUE, -- Електронна пошта гравця
                       password_hash VARCHAR(255) NOT NULL, -- Хеш пароля гравця
                       role VARCHAR(10) DEFAULT 'user', -- Роль гравця (user або admin)
                       team_id UUID, -- Ідентифікатор команди, до якої належить гравець
                       CONSTRAINT fk_user_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE SET NULL -- При видаленні команди, встановити team_id в NULL
);

CREATE INDEX idx_users_email ON users(email);