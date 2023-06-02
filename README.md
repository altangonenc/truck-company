# truck-company
The tech stack: 
* Spring Boot
* Spring Data JPA 
* Spring Security 
* MariaDB
* Iyzipay (for ingame sales)


SIGN UP API: 
* You can sign up if there is no email or username identical as yours. 
* You can sign up if your all credentials are present(not empty).
* curl --location 'localhost:8080/users/register' \
--header 'Content-Type: application/json' \
--data-raw '{
    "email" : "mock@gmail.com",
    "userName" : "mock16",
    "firstName" : "mock",
    "lastName" : "mock",
    "password" : "mockPassword"
}'


LOGIN API: 
* This API stands for the token provider for users. 
* You should use LOGIN API if you want to play game. 
* When you used this API take the token in response header. And then you can use all the other API's with giving this token to them. 
* curl --location 'localhost:8080/users/login' \
--header 'Content-Type: application/json' \
--data '{
    "username" : "mock16",
    "password" : "mockPassword"
}'


PROFILE API: 
* This API stands for the check User's information. 
* Only takes Bearer token which produced with the specified user.
* curl --location 'localhost:8080/users/profile' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZWtzYXNsaWFsdGFuIiwiZXhwIjoxNjg2NDg4NTY0fQ.45PD9_QcVkJw642VegumeYqymi0v82VGh_BVghKOKTqDLYFLzIPR4INquatLE_Zl-hcYE4QaYdIcMSIxPyjoXg' \
--data ''
