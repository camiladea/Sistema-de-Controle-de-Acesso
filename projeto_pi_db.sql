CREATE DATABASE IF NOT EXISTS projeto_pi_db 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE projeto_pi_db;

-- Tabela Usuario corrigida: sem 'matricula' e com 'digitalFIR'
CREATE TABLE IF NOT EXISTS Usuario (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    email VARCHAR(255),
    digitalFIR TEXT, -- CORRIGIDO: O nome da coluna agora é 'digitalFIR'
    ativo BOOLEAN DEFAULT TRUE,
    tipoUsuario VARCHAR(15) NOT NULL COMMENT 'Define se é Funcionario ou Administrador',

    cargo VARCHAR(100),
    -- REMOVIDO: A coluna 'matricula' foi retirada para alinhar com o código Java

    login VARCHAR(50) UNIQUE,
    senhaHash VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS RegistroAcesso (
    id INT PRIMARY KEY AUTO_INCREMENT,
    dataHora DATETIME NOT NULL,
    usuarioId INT, 
    status VARCHAR(50) NOT NULL,
    origem VARCHAR(100) NOT NULL,
    
    CONSTRAINT fk_usuario_acesso
    FOREIGN KEY (usuarioId) REFERENCES Usuario(id) 
    ON DELETE SET NULL
) ENGINE=InnoDB;

-- Inserção do usuário administrador (senha ainda em texto plano para fins de simulação)
INSERT INTO Usuario (nome, cpf, email, tipoUsuario, ativo, login, senhaHash) 
VALUES 
('Administrador Principal', '000.000.000-00', 'admin@sistema.com', 'Administrador', TRUE, 'admin', 'admin123')
ON DUPLICATE KEY UPDATE 
    nome = VALUES(nome);
