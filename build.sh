echo "Creating the executable..."
sbt dist

echo "Building the docker image..."
docker build -t bullish_breakouts .