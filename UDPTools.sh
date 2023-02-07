#!/bin/bash

echo "[INFO] --------- Contenedor para compilar"
echo "[INFO] --------- Use el siguiente comando para ejecutar"
echo "[INFO] --------- java -Xmx512m -Xms256m -jar udpTools.jar" > info.log

echo "[INFO] --------- Compila programa java"
javac -classpath "libs/*" -sourcepath src/ -source 1.8 -target 1.8 -d target/classes src/program/toolsUdp.java

echo "[INFO] --------- Genera empaquetado jar"
jar cvfm udptools.jar src/resources/MANIFEST.MF -C target/classes .

tail -f info.log
