## :bookmark_tabs: About This Project

Melody Match is an application that fetches 4 songs from the Spotify API based on the user's mood. By providing a mood query, the app returns a playlist tailored to fit the emotional tone, using the power of Spotify's vast music library to match the requested mood.

## :hammer_and_wrench: Used Technologies

* Java 17
* Spring Boot 3.4
* Spring Data JPA / Hibernate
* Spring Security (JWT, role-based authentication)
* WebClient
* MySQL
* Lombok
* Maven

## :camera: Screenshots

Happy json response (Unregistered)     |  Happy json response (Unregistered) (pretty)
:------------------------:|:-------------------------:
![Happy json response (Unregistered)](src/main/resources/static/images/json_response_happy_from_spotify_api.png)  |  ![Happy json response (Unregistered) (pretty)](src/main/resources/static/images/json_response_happy_from_spotify_api_pretty.png)

Sad json response (Unregistered)     |  Sad json response (Unregistered) (pretty)
:------------------------:|:-------------------------:
![Sad json response (Unregistered)](src/main/resources/static/images/json_response_sad_from_spotify_api.png)  |  ![Sad json response (Unregistered) (pretty)](src/main/resources/static/images/json_response_sad_from_spotify_api_pretty.png)

Register user      |  Login
:------------------------:|:-------------------------:
![Register user](src/main/resources/static/images/register.png)  |  ![Login](src/main/resources/static/images/login.png)

Login success      |  Login failed
:------------------------:|:-------------------------:
![Login success](src/main/resources/static/images/success_login.png)  |  ![Login failed](src/main/resources/static/images/failed_login.png)

Find your song by your mood and save it to your account      |  Saved songs
:------------------------:|:-------------------------:
![Find and save song](src/main/resources/static/images/find_song_by_your_mood_and_save_it_on_your_account.png)  |  ![My saved songs](src/main/resources/static/images/my_saved_songs.png)

Delete song from your list      |  List of songs after deleting
:------------------------:|:-------------------------:
![Delete song](src/main/resources/static/images/delete_song_from_my_list.png)  |  ![List after deleting](src/main/resources/static/images/my_songs_after_deleting.png)

