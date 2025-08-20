-- 1. Garante a criação do banco de dados com o conjunto de caracteres correto (utf8mb4),
--    que é o padrão moderno para suportar todos os tipos de caracteres.
CREATE DATABASE IF NOT EXISTS projeto_pi_db 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. Seleciona o banco de dados para garantir que as tabelas sejam criadas no lugar certo.
USE projeto_pi_db;

-- 3. Cria a tabela 'Usuario', que armazenará tanto Funcionários quanto Administradores.
--    'IF NOT EXISTS' previne um erro se a tabela já existir.
CREATE TABLE IF NOT EXISTS Usuario (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    email VARCHAR(255),
    digitalHash TEXT,
    ativo BOOLEAN DEFAULT TRUE,
    tipoUsuario VARCHAR(15) NOT NULL COMMENT 'Define se é Funcionario ou Administrador',

    -- Atributos específicos de Funcionario (serão nulos para um admin)
    cargo VARCHAR(100),
    matricula VARCHAR(50),

    -- Atributos específicos de Administrador (serão nulos para um funcionário)
    login VARCHAR(50) UNIQUE,
    senhaHash VARCHAR(255)
) ENGINE=InnoDB;

-- 4. Cria a tabela 'RegistroAcesso' para o log de eventos.
CREATE TABLE IF NOT EXISTS RegistroAcesso (
    id INT PRIMARY KEY AUTO_INCREMENT,
    dataHora DATETIME NOT NULL,
    usuarioId INT, -- Pode ser nulo se o acesso for de um usuário não identificado.
    status VARCHAR(50) NOT NULL,
    origem VARCHAR(100) NOT NULL,
    
    -- Cria a chave estrangeira que liga este registro a um usuário na tabela Usuario.
    -- ON DELETE SET NULL: Preserva o histórico de acesso mesmo que o usuário seja deletado.
    CONSTRAINT fk_usuario_acesso
    FOREIGN KEY (usuarioId) REFERENCES Usuario(id) 
    ON DELETE SET NULL
) ENGINE=InnoDB;

-- 5. Insere o administrador primordial do sistema, essencial para o primeiro uso.
--    'ON DUPLICATE KEY UPDATE' é uma cláusula de segurança: se um usuário com o
--    CPF '000.000.000-00' já existir, ele apenas atualiza o nome, evitando um erro
--    e garantindo que o admin sempre exista.
INSERT INTO Usuario (nome, cpf, email, tipoUsuario, ativo, login, senhaHash) 
VALUES 
('Administrador Principal', '000.000.000-00', 'admin@sistema.com', 'Administrador', TRUE, 'admin', 'admin123')
ON DUPLICATE KEY UPDATE 
    nome = VALUES(nome);

-- Fim do script.