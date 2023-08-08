# ShareIt - сервис для обмена вещами

#### ShareIt - это сервис, который позволяет пользователям делиться вещами и арендовать их на определенный период времени.

## Возможности сервиса:
1. Размещение вещей для шеринга: Пользователи могут добавлять свои вещи, которыми они готовы поделиться с другими пользователями.
2. Поиск и аренда вещей: Пользователи могут искать вещи, которые им интересны, и бронировать их на определенные даты
3. Бронирование и блокирование доступа: Сервис позволяет бронировать вещь на определенные даты и автоматически блокировать доступ к ней от других пользователей на это время.
4. Оставление запросов: Если нужной вещи нет в списке, пользователи могут оставить запрос на её наличие. Это позволит другим пользователям добавить новые вещи для шеринга.

## Описание контроллеров:
## `UserController`:
 - `GET /users`: Получить список всех пользователей.
 - `GET /users/{id}`: Получить информацию о пользователе по его ID.
 - `POST /users`: Зарегистрировать нового пользователя.
 - `PATCH /users/{id}`: Обновить информацию о пользователе по его ID.
 - `DELETE /users/{id}`: Удалить пользователя
## `ItemController`:
 - `GET /items/all`: Получить список всех доступных предметов для шеринга.
 - `GET /items`: Получить список вещей, добавленных текущим пользователем.
 - `GET /items/{id}`: Получить информацию о вещи по её ID.
 - `GET /items/search`: Поиск вещей по текстовому запросу.
 - `POST /items`: Добавить новый предмет для шеринга. Требуется указать заголовок запроса X-Sharer-User-Id, содержащий ID текущего пользователя.
 - `PATCH /items/{itemId}`: Обновить информацию о предмете. Требуется указать заголовок запроса X-Sharer-User-Id, содержащий ID текущего пользователя.
 - `DELETE /items/{id}`: Удалить предмет из списка доступных для шеринга.

## Технологии:
- Java
- Spring Boot
- Lombok
- Maven

Автор: [Провоторова Алина Игоревна](https://t.me/alinamalina998)