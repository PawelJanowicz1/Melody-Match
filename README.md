## :bookmark_tabs: About This Project

MelodyMatch is an application that fetches 5 songs from the Spotify API based on the user's mood. By providing a mood query, the app returns a playlist tailored to fit the emotional tone, using the power of Spotify's vast music library to match the requested mood.

## :hammer_and_wrench: Used Technologies

* Java
* Spring Boot
* Spring Data JPA / Hibernate
* MySql
* Maven
* Lombok

## :camera: Screenshots

Happy json response      |  Happy json response (pretty)
:------------------------:|:-------------------------:
![Happy json response](src/main/resources/static/images/json_response_happy_from_spotify_api.png)  |  ![Happy json response (pretty)](src/main/resources/static/images/json_response_happy_from_spotify_api_pretty.png)

Sad json response      |  Sad json response (pretty)
:------------------------:|:-------------------------:
![Sad json response](src/main/resources/static/images/json_response_sad_from_spotify_api.png)  |  ![Sad json response (pretty)](src/main/resources/static/images/json_response_sad_from_spotify_api_pretty.png)

Register user      |  Login
:------------------------:|:-------------------------:
![Register user](src/main/resources/static/images/register.png)  |  ![Login](src/main/resources/static/images/login.png)

Login success      |  Login failed
:------------------------:|:-------------------------:
![Login success](src/main/resources/static/images/success_login.png)  |  ![Login failed](src/main/resources/static/images/failed_login.png)

:memo: TODO

* Optimize song fetching based on more mood parameters.
*  Add support for saving and managing favorite songs.
*   Add user authentication for personalized playlists.
