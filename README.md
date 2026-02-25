# Sistema de Controle de Acesso por Biometria com TV Box

![Status](https://img.shields.io/badge/status-em_desenvolvimento-yellow)
![Java](https://img.shields.io/badge/Java-SE%2021-blue)
![Database](https://img.shields.io/badge/Database-SQL-orange)
![UI](https://img.shields.io/badge/UI-Java%20Swing-blueviolet)

Projeto Integrador do Curso Técnico Integrado em Informática do **Instituto Federal de Santa Catarina (IFSC) - Câmpus Gaspar**.

---

## Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Objetivo](#objetivo)
- [Principais Funcionalidades](#principais-funcionalidades)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Arquitetura do Software](#arquitetura-do-software)
- [Configuração do Ambiente](#configuração-do-ambiente)
- [Status do Projeto](#status-do-projeto)
- [Telas](#telas)
- [Autores](#autores)
- [Orientadores](#orientadores)

---

## Sobre o Projeto

Este projeto consiste no desenvolvimento de um sistema de controle de acesso de baixo custo, utilizando a biometria como forma de autenticação. A principal inovação é o reaproveitamento de dispositivos **TV Box** com sistema Android, transformando-os em minicomputadores capazes de gerenciar a leitura de impressões digitais, processar a autenticação e registrar os acessos em um banco de dados central.

## Objetivo

Desenvolver uma solução de controle de acesso segura, eficiente e acessível, demonstrando a viabilidade técnica de utilizar hardware de baixo custo (TV Box) para aplicações de segurança e gerenciamento de identidade.

## Principais Funcionalidades

- **(RF01)** Cadastro de usuários (funcionários e administradores) com captura de impressão digital.
- **(RF02)** Autenticação de usuários via leitor biométrico (identificação 1 para N).
- **(RF03)** Registro e log de todas as tentativas de acesso (permitidas e negadas) para fins de auditoria.
- **(RF04)** Autenticação de administrador por login e senha para autorizar operações críticas (como novos cadastros).
- **(RF05)** Painel administrativo para gestão de usuários (ativar, desativar, editar).
- **(RF06)** Geração e visualização de relatórios de acesso por período ou por usuário.

## Tecnologias Utilizadas

- **Linguagem:** Java SE 21
- **Interface Gráfica (UI):** Java Swing (com WindowBuilder)
- **Banco de Dados:** Qualquer banco de dados SQL (ex: MySQL, PostgreSQL, SQLite).
- **Hardware:**
    - TV Box com sistema Linux/Android.
    - Leitor Biométrico Nitgen Hamster DX.
- **Bibliotecas e SDKs:**
    - SDK `NBioBSPJNI` da Nitgen para controle do leitor biométrico.
    - Driver JDBC para conexão com o banco de dados.

## Arquitetura do Software

O sistema é projetado seguindo uma arquitetura em camadas para garantir a separação de responsabilidades, manutenibilidade e escalabilidade.

- `src/model`: Contém as classes de entidade (`Usuario`, `Funcionario`, `Admin`, `RegistroAcesso`).
- `src/dao`: Camada de Acesso a Dados, responsável pela comunicação com o banco de dados (`UsuarioDAO`, `RegistroAcessoDAO`).
- `src/service`: Camada de Lógica de Negócio, onde as regras do sistema são aplicadas (`SistemaAutenticacao`, `GerenciadorUsuarios`).
- `src/controller`: Faz a ponte entre a interface gráfica e a camada de serviço (`TerminalController`).
- `src/view`: Contém todas as telas da aplicação feitas em Java Swing (`TelaAutenticacao`, `TelaCadastroUsuario`, etc.).
- `src/util`: Classes utilitárias, como a de conexão com o banco de dados (`ConexaoBancoDados`).

## Configuração do Ambiente

Siga os passos abaixo para configurar e executar o projeto localmente.

### 1. Pré-requisitos
- **JDK 21** ou superior.
- Uma **IDE Java** (ex: Eclipse, IntelliJ IDEA).
- Um **servidor de banco de dados SQL** (ex: MySQL) instalado e em execução.
- O **Leitor Biométrico Hamster DX** conectado ao computador.

### 2. Instalação do SDK Biométrico
- Instale os **drivers do Hamster DX** para o seu sistema operacional.
- Obtenha o SDK **NBioBSPJNI** da Nitgen.
- Adicione o arquivo `NBioBSPJNI.jar` às bibliotecas do seu projeto na IDE.
- Coloque a biblioteca nativa (`.dll` para Windows, `.so` para Linux) na raiz do projeto ou configure o `java.library.path` para apontar para a pasta que contém este arquivo.

### 3. Configuração do Banco de Dados
- Crie um banco de dados (schema) no seu servidor SQL.
- Execute o script SQL abaixo para criar as tabelas necessárias:

```sql
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

INSERT OR IGNORE INTO Usuario (nome, cpf, email, ativo, tipoUsuario, cargo, login, senhaHash, digitalTemplate)
VALUES
('Administrador Padrão', '00000000000', 'admin@sistema.local', 1, 'ADMIN', 'Administrador', 'admin', 'admin123', NULL);

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
```

### 4. Configuração do Projeto
- Clone este repositório: `git clone [URL_DO_SEU_REPOSITORIO]`
- Abra o projeto na sua IDE.
- Configure os dados de conexão com o banco de dados na classe `util/ConexaoBancoDados.java`.

### 5. Execução
- Execute a classe `Main.java` para iniciar a aplicação.

## Status do Projeto

**Fase Atual:** *Início da codificação: reconhecimento biométrico e cadastro de usuários* (De acordo com o cronograma de 16/07 a 15/08/2025).

- [X] Definição da Arquitetura e Blueprint do Sistema.
- [X] Implementação do Pacote `model`.
- [X] Implementação dos Pacotes `util` e `dao`.
- [X] Implementação do Pacote `service` para integração com o leitor.
- [X] Desenvolvimento das telas do sistema.
- [ ] Verificação de rentabilidade com o sistema ARM64.
- [ ] Implementação do Java no TVBox.
- [ ] Implementação das funcionalidades do banco de dados no TVBox.
- [ ] Simulação de execução no TVBox
- [ ] Teste de conexão remota.
- [ ] Release Final.

## Telas

## Autores

- **Alvaro Gabriel Goulart**
- **Camila de Assunção**
- **Gustavo Ribeiro Gonçalves Barbosa**
- **José Olavio da Silva**

## Orientadores

- Prof. Andrei Inácio de Souza
- Prof. Leonardo Ronald Perin Rauta
- Prof. Maykon Chagas
- Prof. Fernando Weber Albiero
