# Relatório do Projeto: Sistema de Controle de Acesso

## 1. Visão Geral do Projeto

O projeto "Sistema de Controle de Acesso" é uma aplicação de desktop desenvolvida em Java para gerenciar e controlar o acesso de usuários a um determinado ambiente. A aplicação possui uma interface gráfica para interação com o usuário e um banco de dados para armazenar informações sobre usuários e registros de acesso.

## 2. Funcionalidades

O sistema permite:

*   **Autenticação de Usuários:** A tela inicial do sistema é uma tela de autenticação, o que indica que o acesso ao sistema é restrito. A autenticação pode ser realizada por meio de login e senha, e também por biometria (impressão digital), como sugerido pela estrutura do banco de dados e pelo nome da branch (`digital-persona-ze`).
*   **Gerenciamento de Usuários:** O sistema permite o cadastro, edição e remoção de usuários. As informações dos usuários incluem dados pessoais, credenciais de acesso e tipo de usuário (ex: Administrador).
*   **Registro de Acessos:** Todas as tentativas de acesso, bem-sucedidas ou não, são registradas no banco de dados. Os registros incluem informações sobre o usuário, data, hora, status do acesso e a origem da tentativa de acesso.
*   **Exportação de Dados:** O sistema possui a funcionalidade de exportar dados para o formato CSV, o que pode ser útil para gerar relatórios.

## 3. Arquitetura e Tecnologias

*   **Linguagem:** Java
*   **Interface Gráfica:** Java Swing
*   **Banco de Dados:** SQLite
*   **Gerenciador de Dependências:** Maven
*   **Arquitetura:** O projeto segue uma arquitetura que se assemelha ao padrão Model-View-Controller (MVC):
    *   **Model:** Representado pelas classes no pacote `model`, que definem os objetos de domínio (ex: `Usuario`, `RegistroAcesso`).
    *   **View:** As classes no pacote `view` são responsáveis pela interface do usuário (ex: `TelaAutenticacao`).
    *   **Controller:** O pacote `controller` contém a lógica de controle, intermediando as interações entre a View and o Model.
    *   **DAO (Data Access Object):** O pacote `dao` é responsável pela comunicação com o banco de dados.
*   **Dependências:**
    *   `sqlite-jdbc`: Para conectar-se ao banco de dados SQLite.
    *   `opencsv`: Para manipulação de arquivos CSV.

## 4. Esquema do Banco de Dados

O banco de dados é composto por duas tabelas principais:

*   **`Usuario`**: Armazena as informações dos usuários do sistema.
    *   `id`: Identificador único do usuário.
    *   `nome`: Nome do usuário.
    *   `cpf`: CPF do usuário (identificador único).
    *   `email`: Email do usuário.
    *   `digitalTemplate`: Template da impressão digital do usuário (armazenado como dados binários).
    *   `ativo`: Status do usuário (ativo ou inativo).
    *   `tipoUsuario`: Tipo de usuário (ex: "Administrador").
    *   `cargo`: Cargo do usuário.
    *   `login`: Login do usuário para acesso ao sistema.
    *   `senhaHash`: Hash da senha do usuário.

*   **`RegistroAcesso`**: Armazena os registros de tentativas de acesso.
    *   `id`: Identificador único do registro.
    *   `dataHora`: Data e hora da tentativa de acesso.
    *   `usuarioId`: Chave estrangeira para a tabela `Usuario`.
    *   `status`: Status da tentativa (ex: "permitido", "negado").
    *   `origem`: Origem da tentativa de acesso (ex: "terminal 1").

## 5. Principais Características

*   **Segurança:** O uso de hash para senhas e a possibilidade de autenticação biométrica aumentam a segurança do sistema.
*   **Auditoria:** O registro detalhado de acessos permite uma auditoria completa das atividades no sistema.
*   **Portabilidade:** Por ser uma aplicação Java com um banco de dados embarcado (SQLite), o sistema é relativamente fácil de ser distribuído e executado em diferentes plataformas.
*   **Manutenibilidade:** A separação de responsabilidades (MVC) facilita a manutenção e a evolução do código.
