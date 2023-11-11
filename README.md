# truck-company

The backend of an online game called Truck-company.

**_The tech stack_:**
* _Spring Boot_
* _Spring Data JPA_
* _Spring Security_
* _MariaDB_
* _Iyzipay (for ingame sales)_)(not for sure)(stripe is still an option)


In order to be understandable, APIs are presented under 2 headings.
1) User Management Api's 
2) Gameplay Api's

## USEFUL SCRIPTS AND .EXE FILES 
* _Freight terminals can be observed by running the script "scripts/countryGraphScript/countryGraph.py" with the "py" command._
* _If you want to run the application in a simple way and you have docker installed on your local, you can run the "automate.exe" file to stand up the application and database. To stop 2 containers, just run "docker-compose down". Also this process gets the latest build of application._
* _"gui.exe" has 2 options. You can start the application or update jar file._

## USER MANAGEMENT API'S

### SIGN UP API (POST):
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


### *LOGIN API (POST):
* This API stands for the token provider for users.
* You should use LOGIN API if you want to play game.
* When you used this API take the token in response header. And then you can use all the other API's with giving this token to them.
* curl --location 'localhost:8080/users/login' \
  --header 'Content-Type: application/json' \
  --data '{
  "username" : "mock16",
  "password" : "mockPassword"
  }'


### PROFILE API (GET):
* This API stands for the check User's information.
* Only takes Bearer token which produced with the specified user.
* curl --location 'localhost:8080/users/profile' \
  --header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZWtzYXNsaWFsdGFuIiwiZXhwIjoxNjg2NDg4NTY0fQ.45PD9_QcVkJw642VegumeYqymi0v82VGh_BVghKOKTqDLYFLzIPR4INquatLE_Zl-hcYE4QaYdIcMSIxPyjoXg' \
  --data ''


### CHANGE PASSWORD API (POST):
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


### GET RECOVERY QUESTION/QUESTIONS (GET):
* This API has 2 modes.
* If you use it with a valid token of a user, you will get the chosen recovery question of user
* If you use it without any token or with an invalid token, you will get all the questions. (For client integration)

* OPTION1:
  curl --location 'localhost:8080/users/recovery-question' \
  --header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZWtzYXNsaWFsdGFuIiwiZXhwIjoxNjg2Njc0OTI1fQ.jK-JXGYtcjlk-mlasjBhFtqkXOwIghpdEwgRZ53sqPsjdaGboRPyYIzQk9RB8Yzkiss2n0jGFDlXNJN0F-ewuw'

* OPTION2:
  curl --location 'localhost:8080/users/recovery-question'

## GAMEPLAY API'S

### GET ALL TRUCK MODELS API (GET):
* This API provides all the truck models to user.
* curl --location 'localhost:8080/api/v1/game/trucks' \
  --header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZWtzYXNsaWFsdGFuIiwiZXhwIjoxNjkxNTc2MDQ4fQ.9XuZ6RvkOP5Z-z_xlR0vc_Y-GE-HwxWtp6iIONM1iGVvkZtzTLTmeeeojUjML9AA4-CHkbdZoXk9IV_FyJpgMg'

### GET TRUCK ATTRIBUTES API (GET):
* This API provides the specified trucks attributes. 
* curl --location 'localhost:8080/api/v1/game/truck/attributes/DAF_XF' \
  --header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZWtzYXNsaWFsdGFuIiwiZXhwIjoxNjkxNTc2MDQ4fQ.9XuZ6RvkOP5Z-z_xlR0vc_Y-GE-HwxWtp6iIONM1iGVvkZtzTLTmeeeojUjML9AA4-CHkbdZoXk9IV_FyJpgMg' 

### BUY TRUCK API (POST):
* You can buy a truck using this API. But you need to be careful where you want to buy it. In the example curl request, user prefers germany. 
* curl --location --request POST 'localhost:8080/api/v1/game/truck/buy/DAF_XF/germany' \
  --header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZWtzYXNsaWFsdGFuIiwiZXhwIjoxNjkxNTc2MDQ4fQ.9XuZ6RvkOP5Z-z_xlR0vc_Y-GE-HwxWtp6iIONM1iGVvkZtzTLTmeeeojUjML9AA4-CHkbdZoXk9IV_FyJpgMg' 

### GET ALL JOBS IN TERMINAL API (GET)
* You can get all the jobs in a terminal using this API. 
* curl --location 'localhost:8080/api/v1/game/jobs/germany' \
  --header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZWtzYXNsaWFsdGFuIiwiZXhwIjoxNjkxNTc2MDQ4fQ.9XuZ6RvkOP5Z-z_xlR0vc_Y-GE-HwxWtp6iIONM1iGVvkZtzTLTmeeeojUjML9AA4-CHkbdZoXk9IV_FyJpgMg'
* For example. This curl request gives you all the jobs in Germany location. 

### GET ALL JOBS API (GET)
* You can get all the jobs in the whole game with using this API.
* curl --location 'localhost:8080/api/v1/game/jobs' \
  --header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZWtzYXNsaWFsdGFuIiwiZXhwIjoxNjkxMDk5OTk2fQ.oxv4s5w8XiYPjI7CsNkgmYauH4C-Bu75ohNigKWJm4qakP_Pa3hiyIrWxfcqYsyvfiSZsevBUnZzOeY_hUgByQ' 

### TAKE JOB API (POST)
* You can take a job and start it with using this API.
* You can specify your route, but this is only useful if you are traveling by ship. The rest of the time the algorithm will take you by the shortest highway.  
* If you specify route: 
* curl --location 'localhost:8080/api/v1/game/job/191/take' \
  --header 'Content-Type: application/json' \
  --header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZWtzYXNsaWFsdGFuIiwiZXhwIjoxNjkxNTc2MDQ4fQ.9XuZ6RvkOP5Z-z_xlR0vc_Y-GE-HwxWtp6iIONM1iGVvkZtzTLTmeeeojUjML9AA4-CHkbdZoXk9IV_FyJpgMg' \
  --data '{
  "route" : ["GERMANY", "TURKEY", "ITALY"],
  "truckId" : 1
  }'
* If you don't specify your route:
* curl --location 'localhost:8080/api/v1/game/job/191/take' \
  --header 'Content-Type: application/json' \
  --header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZWtzYXNsaWFsdGFuIiwiZXhwIjoxNjkxNTc2MDQ4fQ.9XuZ6RvkOP5Z-z_xlR0vc_Y-GE-HwxWtp6iIONM1iGVvkZtzTLTmeeeojUjML9AA4-CHkbdZoXk9IV_FyJpgMg' \
  --data '{
  "truckId" : 1
  }' 

### FINISH JOB API (POST)
* When the completion time of your job comes. You can use this API, for getting reward of job. 
* curl --location --request POST 'localhost:8080/api/v1/game/job/191/finish' \
  --header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZWtzYXNsaWFsdGFuIiwiZXhwIjoxNjkxNTc2MDQ4fQ.9XuZ6RvkOP5Z-z_xlR0vc_Y-GE-HwxWtp6iIONM1iGVvkZtzTLTmeeeojUjML9AA4-CHkbdZoXk9IV_FyJpgMg' \
  --data ''

### GET ALL TRUCKS OF USER API (GET)
* This API will return all trucks owned by the user.  
* curl --location 'localhost:8080/api/v1/game/truck/get/all' \
  --header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZWtzYXNsaWFsdGFuIiwiZXhwIjoxNjkyNDgwNjU3fQ.wZP-nqq8JVbiyzvYVW7R4BVqOSUtgL3hM8_IDgHfPiX6AgliliVFGREJphKLk2ByG2V6uc9Avy82jww7T0ptOQ' 

### SELL ITEM API (POST)
* With this API, truck sales transactions are made using Price and truckId values.
* curl --location 'localhost:8080/api/v1/game/item/sell' \
  --header 'Content-Type: application/json' \
  --header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZWtzYXNsaWFsdGFuIiwiZXhwIjoxNjkzNTk2Mjk4fQ.v39qn--kgW0v1vuRP5bFOG8HtDWZB38oGwV-6_AXv24GyOUJkK0hr3OlGCMGFgLZ3rU4vTdtj_-bWIqChruICQ' \
  --data '{
  "price" : 10565464,
  "itemId" : 1
  }'