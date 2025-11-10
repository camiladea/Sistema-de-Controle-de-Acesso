PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS Usuario (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    cpf TEXT UNIQUE NOT NULL,
    email TEXT,
    digitalTemplate BLOB,              -- Template da primeira digital
    digitalTemplate1 BLOB,             -- Template da segunda digital
    digitalTemplate2 BLOB,             -- Template da terceira digital
    ativo INTEGER DEFAULT 1,
    tipoUsuario TEXT NOT NULL,         -- Ex: "ADMIN", "FUNCIONARIO"
    cargo TEXT,
    login TEXT UNIQUE,
    senhaHash TEXT                     -- Hash da senha (usar BCrypt em produção)
);

CREATE TABLE IF NOT EXISTS RegistroAcesso (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    dataHora TEXT NOT NULL,            -- Armazena LocalDateTime como string ISO
    usuarioId INTEGER,                 -- FK para Usuario
    status TEXT NOT NULL,              -- Ex: "Acesso Permitido", "Acesso Negado"
    origem TEXT,                       -- Ex: "Terminal Principal", "Admin Panel"
    FOREIGN KEY (usuarioId) REFERENCES Usuario (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS ConfiguracaoSistema (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    chave TEXT UNIQUE NOT NULL,
    valor TEXT
);

INSERT OR IGNORE INTO Usuario (nome, cpf, email, ativo, tipoUsuario, cargo, login, senhaHash)
VALUES
('Administrador Padrão', '00000000000', 'admin@sistema.local', 1, 'ADMIN', 'Administrador', 'admin', 'admin123');

INSERT OR IGNORE INTO ConfiguracaoSistema (chave, valor)
VALUES ('versao_banco', '1.1'),
       ('modo_debug', 'false');

CREATE VIEW IF NOT EXISTS vw_relatorio_acessos AS
SELECT 
    RA.id AS idRegistro,
    RA.dataHora,
    U.nome AS nomeUsuario,
    U.cpf,
    U.cargo,
    RA.status,
    RA.origem
FROM RegistroAcesso RA
LEFT JOIN Usuario U ON RA.usuarioId = U.id
ORDER BY RA.dataHora DESC;
