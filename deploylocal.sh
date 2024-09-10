#!/bin/bash

# ========================
# 参数设置
# ========================
IMAGE_NAME="sda"           # Docker镜像名称
CONTAINER_NAME="sda"       # Docker容器名称
HOST_PORT=82               # 主机上的端口
CONTAINER_PORT=8080        # 容器内部的端口
# ========================

# 检查当前是否有正在运行的容器
if docker ps | grep -q "$CONTAINER_NAME"; then
  echo "Stopping and removing existing $CONTAINER_NAME container..."
  docker stop "$CONTAINER_NAME"
  docker rm "$CONTAINER_NAME"
fi

# 检查主机端口是否被其他容器占用
if docker ps --filter "publish=$HOST_PORT" | grep -q "0.0.0.0:$HOST_PORT"; then
  echo "Port $HOST_PORT is currently being used by another container."

  # 获取占用端口的容器ID
  container_id=$(docker ps --filter "publish=$HOST_PORT" --format "{{.ID}}")
  echo "Container with ID $container_id is using port $HOST_PORT."

  # 提示用户是否要停止该容器
  read -p "Do you want to stop this container? (y/n): " choice
  if [ "$choice" = "y" ]; then
    echo "Stopping container $container_id..."
    docker stop "$container_id"
    echo "Container $container_id has been stopped."
  else
    echo "Keeping the container running. Exiting script."
    exit 1
  fi
fi

# 构建新的镜像
echo "Building the $IMAGE_NAME docker image..."
docker build -t "$IMAGE_NAME" .

# 启动新的容器
echo "Starting the new $CONTAINER_NAME container..."
docker run -d --name "$CONTAINER_NAME" -p "$HOST_PORT:$CONTAINER_PORT" "$IMAGE_NAME"

echo "$CONTAINER_NAME deployment successful!"
