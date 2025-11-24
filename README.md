# Sistema de Controle de Acesso

Este é um sistema de controle de acesso biométrico desenvolvido em Java.

## Pré-requisitos

- Java 11 ou superior
- `fprintd` instalado e configurado no sistema. Este software é usado para interagir com o leitor de impressão digital.

Na maioria das distribuições Linux baseadas em Debian (como Ubuntu ou Armbian), você pode instalar o `fprintd` com o seguinte comando:

```bash
sudo apt-get update
sudo apt-get install fprintd
```

## Como Compilar e Executar

O projeto pode ser compilado e executado usando os scripts fornecidos.

### Compilar

Para compilar o projeto, execute o script `build.sh`:

```bash
./build.sh
```

Este comando irá compilar todas as classes Java e colocá-las no diretório `bin`.

### Executar

Para executar a aplicação, use o script `run.sh`:

```bash
./run.sh
```

Isso iniciará a interface gráfica do sistema de controle de acesso.

## Banco de Dados

O sistema utiliza um banco de dados SQLite (`sistema_acesso.db`) que é criado e inicializado automaticamente na primeira vez que a aplicação é executada.

O usuário administrador padrão é:
- **Login:** admin
- **Senha:** admin123
