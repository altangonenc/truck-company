# truck-company
The tech stack: 
* Spring Boot
* Spring Data JPA 
* Spring Security 
* MariaDB
* Iyzipay (for ingame sales)


SIGN UP API (POST): 
* You can sign up if there is no email or username identical as yours. 
* You can sign up if your all credentials are present(not empty).
* curl --location 'localhost:8080/users/register' \
--header 'Content-Type: application/json' \
--data-raw '{
    "email" : "mock@gmail.com",
    "userName" : "mock16",
    "firstName" : "mock",
    "lastName" : "mock",
    "password" : "mockPassword",
    "recoveryQuestionId" : 1,
    "recoveryAnswer" : "mockAnswer"  
}'


LOGIN API (POST): 
* This API stands for the token provider for users. 
* You should use LOGIN API if you want to play game. 
* When you used this API take the token in response header. And then you can use all the other API's with giving this token to them. 
* curl --location 'localhost:8080/users/login' \
--header 'Content-Type: application/json' \
--data '{
    "username" : "mock16",
    "password" : "mockPassword"
}'


PROFILE API (GET): 
* This API stands for the check User's information. 
* Only takes Bearer token which produced with the specified user.
* curl --location 'localhost:8080/users/profile' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZWtzYXNsaWFsdGFuIiwiZXhwIjoxNjg2NDg4NTY0fQ.45PD9_QcVkJw642VegumeYqymi0v82VGh_BVghKOKTqDLYFLzIPR4INquatLE_Zl-hcYE4QaYdIcMSIxPyjoXg' \
--data ''


CHANGE PASSWORD API (POST):
* This API changes the password of user. 
* It takes all the information of user and a new password.
* Encoded "recoveryAnswer"  stores in DB just like password.
* If you want to use this API, your recoveryAnswer should match with your very beginning answer.
* Do you want to see what the question is, you should send GET RECOVERY QUESTION REQUEST. 
* curl --location 'localhost:8080/users/change-password' \
  --header 'Content-Type: application/json' \
  --data-raw '{
  "email" : "mock@gmail.com",
  "userName" : "mock",
  "firstName" : "mock",
  "lastName" : "mock",
  "password" : "mock16",
  "recoveryQuestionId" : 1,
  "recoveryAnswer" : "akin"
  }' 


GET RECOVERY QUESTION/QUESTIONS (GET):
* This API has 2 modes. 
* If you use it with a valid token of a user, you will get the chosen recovery question of user
* If you use it without any token or with an invalid token, you will get all the questions. (For client integration) 
*OPTION1:
  curl --location 'localhost:8080/users/recovery-question' \
  --header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZWtzYXNsaWFsdGFuIiwiZXhwIjoxNjg2Njc0OTI1fQ.jK-JXGYtcjlk-mlasjBhFtqkXOwIghpdEwgRZ53sqPsjdaGboRPyYIzQk9RB8Yzkiss2n0jGFDlXNJN0F-ewuw'
*OPTION2:
  curl --location 'localhost:8080/users/recovery-question' 
