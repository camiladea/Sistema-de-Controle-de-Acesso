CREATE DATABASE IF NOT EXISTS projeto_pi_db 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE projeto_pi_db;

CREATE TABLE IF NOT EXISTS Usuario (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    email VARCHAR(255),
    digitalHash TEXT,
    ativo BOOLEAN DEFAULT TRUE,
    tipoUsuario VARCHAR(15) NOT NULL COMMENT 'Define se é Funcionario ou Administrador',

    cargo VARCHAR(100),
    matricula VARCHAR(50),

    login VARCHAR(50) UNIQUE,
    senhaHash VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS RegistroAcesso (
    id INT PRIMARY KEY AUTO_INCREMENT,
    dataHora DATETIME NOT NULL,
    usuarioId INT, -- Pode ser nulo se o acesso for de um usuário não identificado.
    status VARCHAR(50) NOT NULL,
    origem VARCHAR(100) NOT NULL,
    
    CONSTRAINT fk_usuario_acesso
    FOREIGN KEY (usuarioId) REFERENCES Usuario(id) 
    ON DELETE SET NULL
) ENGINE=InnoDB;

INSERT INTO Usuario (nome, cpf, email, tipoUsuario, ativo, login, senhaHash) 
VALUES 
('Administrador Principal', '000.000.000-00', 'admin@sistema.com', 'Administrador', TRUE, 'admin', 'admin123')
ON DUPLICATE KEY UPDATE 
    nome = VALUES(nome);