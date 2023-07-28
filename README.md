# java-filmorate
#### Сервис, работающий с фильмами и пользователями. Можно просмотреть информацию о фильмах, оставить свой лайк, увидеть топ-фильмов, выбранных пользователями, а также добавить другого пользователя в друзья. Также присутсвует функция отзывов, которым можно поставить лайк или дизлайк, и лента событий. 

## Схема базы данных:

![Структура БД 11 ТЗ.png](src%2Fmain%2Fresources%2FDataBase.png)
<details> 
<summary><h2> Функции приложения filmorate</h2></summary>

- >Добавление, удаление, редактирование фильмов;
- >«Рекомендации». Реализует простую рекомендательную систему для фильмов;
- >«Отзывы». Отзывы имеют рейтинг и несколько дополнительных характеристик;
- >«Фильмы по режиссёрам»;
- >«Поиск».  Поиск по названию фильмов и по режиссёру;
- >«Популярные фильмы». Выводит топ-N фильмов по количеству лайков. Фильтрация осуществляется по жанру и за указанный год;
- >«Общие фильмы». Вывод общих с другом фильмов с сортировкой по их популярности;
- >«Лента событий».  Возможность просмотра последних событий на платформе — добавление в друзья, удаление из друзей, лайки и отзывы, которые оставили друзья пользователя.
</details> 

<details> 
<summary><h2> Эндпоинты </h2></summary>
<table>
    <thead>
        <tr>
            <th>GET</th>
            <th>POST</th>
            <th>PUT</th>
            <th>DELETE</th>
        </tr>
    </thead>
    <tbody>
        <tr> 
            <td>  /users</td>
            <td>  /users</td>
            <td>  /users</td>
            <td>  /users/{id}</td>
        </tr>
        <tr> 
            <td>  /users/{id} </td>
            <td>  /films</td>
            <td>  /users/{id}/friends/{friendId}</td>
            <td>  /users/{id}/friends/{friendId}</td>
        </tr>
        <tr> 
            <td>  /users/{id}/friends</td>
            <td>  /reviews</td>
            <td>  /films</td>
            <td>  /films/{id}</td>
        </tr>
        <tr> 
            <td>  /users/{id}/friends/common/{otherId}</td>
            <td>  /directors</td>
            <td>  /films/{id}/like/{userId}</td>
            <td>  /films/{id}/like/{userId}</td>
        </tr>
        <tr> 
            <td>  /users/{id}/recommendations</td>
            <td>  </td>
            <td>  /reviews</td>
            <td>  /reviews/{id}</td>
        </tr>
        <tr> 
            <td>  /users/{id}/feed</td>
            <td>  </td>
            <td>  /reviews/{id}/like/{userId}</td>
            <td>  /reviews/{id}/like/{userId}</td>
        </tr>
        <tr> 
            <td> /films</td>
            <td>  </td>
            <td>  /reviews/{id}/dislike/{userId}</td>
            <td>  /reviews/{id}/dislike/{userId}</td>
        </tr>
        <tr> 
            <td>  /films/popular?count={count} </td>
            <td>  </td>
            <td>  /directors</td>
            <td>  /directors/{id}</td>
        </tr>
        <tr> 
            <td>   /films/{id}</td>
        </tr>
        <tr> 
            <td>  /films/common?userId={userId}&friendId={friendId}</td>
        </tr>
        <tr> 
            <td>  /films/director/{directorId}</td>
        </tr>
        <tr> 
            <td> /genres</td>
        </tr>
        <tr> 
            <td> /genres/{id}</td>
        </tr>
        <tr> 
            <td> /mpa</td>
        </tr>
        <tr> 
            <td> /mpa/{id}</td>
        </tr>
        <tr> 
            <td> /reviews?filmId={filmId}&count={count}</td>
        </tr>
        <tr> 
            <td> /reviews/{id}</td>
        </tr>
        <tr> 
            <td> /directors</td>
        </tr>
        <tr> 
            <td> /directors/{id}</td>
        </tr>
    </tbody>
</table>
</details>
