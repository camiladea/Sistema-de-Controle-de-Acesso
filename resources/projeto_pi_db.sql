-- Script de criação das tabelas para o banco de dados SQLite

DROP TABLE IF EXISTS RegistroAcesso;
DROP TABLE IF EXISTS Usuario;

-- Tabela Usuario adaptada para SQLite
CREATE TABLE IF NOT EXISTS Usuario (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    cpf TEXT UNIQUE NOT NULL,
    email TEXT,
    digitalTemplate BLOB, -- Tipo correto para dados binários
    ativo INTEGER DEFAULT 1, -- Em SQLite, BOOLEAN é representado por INTEGER (0 ou 1)
    tipoUsuario TEXT NOT NULL,
    cargo TEXT,
    login TEXT UNIQUE,
    senhaHash TEXT
);

-- Tabela RegistroAcesso adaptada para SQLite
CREATE TABLE IF NOT EXISTS RegistroAcesso (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    dataHora TEXT NOT NULL, -- DATETIME é armazenado como TEXT no SQLite
    usuarioId INTEGER, 
    status TEXT NOT NULL,
    origem TEXT NOT NULL,
    FOREIGN KEY (usuarioId) REFERENCES Usuario(id) ON DELETE SET NULL
);