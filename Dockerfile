FROM openjdk:8

COPY /target/universal/bullishbreakouts-1.0.zip .

RUN unzip bullishbreakouts-1.0.zip

WORKDIR bullishbreakouts-1.0

EXPOSE 2900

CMD ["bin/bullishbreakouts"]
