FROM maven:3.8.4-jdk-11

RUN echo "Se crea carpeta de compilacion" && \
    mkdir app

COPY . /app

WORKDIR /app

RUN echo "Se compila la aplicación" && \
    cd /app && \
    javac -classpath "libs/*" -sourcepath src/ -source 1.8 -target 1.8 -d target/classes src/program/toolsUdp.java && \
    jar cvfm udptools.jar src/resources/MANIFEST.MF -C target/classes . && \
    ls -l udptools.jar


CMD [ "/bin/sh", "./UDPTools.sh" ]
