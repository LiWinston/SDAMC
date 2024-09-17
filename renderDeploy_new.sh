#!/bin/bash

# 设定 Docker Hub 用户名和镜像名称
DOCKER_USERNAME="yongchunl"  # 替换为你的 Docker Hub 用户名
IMAGE_NAME="sda"
TAG="latest"  # 可以根据需要修改标签，例如使用版本号

# 检查 Docker 是否安装
if ! [ -x "$(command -v docker)" ]; then
  echo 'Error: docker is not installed.' >&2
  exit 1
fi

# 登录 Docker Hub
echo "Logging in to Docker Hub..."
docker login
if [ $? -ne 0 ]; then
  echo "Docker login failed!"
  exit 1
fi

# 构建 Docker 镜像
echo "Building Docker image..."
docker build -t "$DOCKER_USERNAME/$IMAGE_NAME:$TAG" .
if [ $? -ne 0 ]; then
  echo "Docker image build failed!"
  exit 1
fi

# 推送 Docker 镜像到 Docker Hub
echo "Pushing Docker image to Docker Hub..."
docker push "$DOCKER_USERNAME/$IMAGE_NAME:$TAG"
if [ $? -ne 0 ]; then
  echo "Docker image push failed!"
  exit 1
fi

# 调用 Render 部署钩子
echo "Triggering Render deployment..."
#curl -X POST "https://api.render.com/deploy/srv-cqt2qcogph6c73co8ocg?key=VjXYO92eYcU"
curl -X POST "https://api.render.com/deploy/srv-crji1klds78s73ebcnl0?key=lFXaM23cu5Y"
if [ $? -ne 0 ]; then
  echo "Render deployment trigger failed!"
  exit 1
fi

# 完成
echo "Docker image pushed and Render deployment triggered successfully!"
