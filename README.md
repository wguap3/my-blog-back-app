# Blog Backend — Спринт 4

Бэкенд приложения-блога на чистом **Spring Framework** (без Spring Boot).

## Описание

Бэкенд REST API для блога с возможностью:

- Создания, редактирования и удаления постов
- Добавления и управления комментариями
- Лайков постов
- Поиска постов по названию и тегам с пагинацией
- Загрузки и отображения изображений постов

## ️ Стек технологий

- **Java 21**
- **Spring Framework 6.1+** (без Spring Boot)
- **PostgreSQL** (или H2 для тестов)
- **Hibernate/JPA** для работы с БД
- **Maven** для сборки
- **Docker** для контейнеризации
- **JUnit 5** для тестирования

## Сборка и запуск

## Быстрый запуск (Docker)

Для запуска всей инфраструктуры убедитесь, что у вас установлен Docker и Docker Desktop запущен.

1. **Клонируйте репозиторий:**
   ```bash
   git clone [https://github.com/wguap3/my-blog-back-app.git]
   cd my-blog-back-app
2. **Соберите проект и запустите контейнеры:**
   ```bash
   ./gradlew clean bootJar -x test
   docker-compose up --build
3. **API будет доступно по адресу: http://localhost:8080/**

## Эндпоинты (API)

#### GET - /api/posts - Получить список всех постов

#### POST - /api/posts - Создать новый пост

#### GET - /api/posts/{id} - Получить пост по id

#### PUT - /api/posts/{id} - Обновление поста

#### DELETE - /api/posts/{id} - Удаление поста

#### POST - /api/posts/{id}/likes - Поставить лайк на пост

#### PUT - /api/posts/{id}/image - Загрузить фото к посту

#### GET - /api/posts/{id}/image - Получить фото поста

#### GET - api/posts/{postId}/comments - Получить комментарии поста

#### POST - api/posts/{postId}/comments - Создать новый комментарий

#### GET - api/posts/{postId}/comments/{commentId} - Получить комментарий

#### PUT - api/posts/{postId}/comments/{commentId} - Обновить комментарий

#### DELETE - api/posts/{postId}/comments/{commentId} - Удалить комментарий
