##Run App
`sbt -jvm-debug 5000 "run 2900"`

##Build Docker File
1. `sbt dist` to build the executable
2. `docker -t bullish_breakouts`

##Run in Docker
1. `docker run --publish 2900:9000 --env-file test.env --name bb bullish_breakouts`