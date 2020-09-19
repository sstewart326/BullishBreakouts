##Run App
`sbt -jvm-debug 5000 -Dconfig.resource=dev.conf "run 2900"`

##Build Docker File
1. build the executable: `sbt dist`
2. build the image: `docker build -t bullish-breakouts .`

##Run in Docker
1. `docker run -d --publish 2900:9000 --env-file test.env --name bullish-breakouts-container bullish-breakouts`