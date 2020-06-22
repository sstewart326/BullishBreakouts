FROM openjdk:8

COPY /target/universal/bullishbreakouts-1.0.zip .

RUN unzip bullishbreakouts-1.0.zip

WORKDIR bullishbreakouts-1.0

CMD ["bin/bullishbreakouts"]
