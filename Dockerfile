FROM eclipse-temurin:21.0.4_7-jre-alpine

# Устанавливаем необходимые пакеты
RUN apk upgrade --no-cache \
    && apk add --no-cache tzdata curl \
    && cp /usr/share/zoneinfo/Europe/Moscow /etc/localtime \
    && echo "Europe/Moscow" > /etc/timezone \
    && apk del tzdata

# Создаем пользователя и группу
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Копируем jar файл
COPY --chown=appuser:appgroup build/libs/*.jar app.jar

# Создаем директории для данных
RUN mkdir -p /app/logs /app/data \
    && chown -R appuser:appgroup /app/logs /app/data

# Переключаемся на непривилегированного пользователя
USER appuser

# Открываем порт
EXPOSE 8080

# Настройки здоровья контейнера
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/api/monitoring/status || exit 1

# Запуск приложения
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+ExitOnOutOfMemoryError", \
    "-Duser.timezone=Europe/Moscow", \
    "-jar", "/app/app.jar"]