#!/bin/bash

mkdir -p bin
find . -name "*.java" -not -path "*/test/*" > sources.txt

javac -d bin @sources.txt

if [ $? -eq 0 ]; then
    echo "Compilation réussie, lancement du jeu..."
    java -cp bin Main
else
    echo "Erreur de compilation, le jeu n'a pas été lancé."
fi

rm sources.txt