#!/bin/bash
# Script to run the application from the packaged JAR

# The JAR file is created by the 'mvn package' command (in build.sh)
# The exact name might vary, so we look for it in the target directory.
JAR_FILE=$(find target -name "sistema-controle-acesso-*.jar" | head -n 1)

if [ -z "$JAR_FILE" ]; then
    echo "Erro: Nenhum arquivo JAR encontrado no diretório 'target'."
    echo "Por favor, execute o script de compilação primeiro: ./build.sh"
    exit 1
fi

echo "Executando o arquivo JAR: $JAR_FILE"
java -jar "$JAR_FILE"