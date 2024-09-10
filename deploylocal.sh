#!/bin/bash

# 检查当前是否有正在运行的sda容器
if docker ps | grep -q "sda"; then
  echo "Stopping and removing existing sda container..."
  docker stop sda
  docker rm sda
fi

# 构建新的sda镜像
echo "Building the sda docker image..."
docker build -t sda .

# 启动新的sda容器
echo "Starting the new sda container..."
docker run -d --name sda -p 82:8080 sda

echo "SDA deployment successful!"
