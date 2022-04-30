# EnjoyMadrid

<p align="justify">
  Enjoy Madrid is a mobile and desktop application developed in Ionic and Spring Boot, created to recommend sustainable routes in the city of Madrid, is characterized by   including factors of air pollution in the creation of routes, in addition to taking into account the tourist preferences of users.
</p>
 
 ![Home page EnjoyMadrid](readme-resources/home_page.PNG)

<hr>

## List of Contents
1. [Introduction](README.md#introduction)
2. [App Walkthrough](README.md#app-walkthrough)
3. [Setting Up](README.md#setting-up)

## Introduction

- User can create, modify or delete an account.
- When creating a route the user sets the name, the origin and destination, the tourist preferences, the walking distance between transports, and the transports he/she wants to use.
- Routes are associated to the user if he/she is logged in, otherwise routes are stored in local storage.
- User can explore places in Madrid according to category.

## App Walkthrough

#### 1. User sign up: 
<p align="center">
  <img height="350px" src="https://github.com/Jos3lu/EnjoyMadrid/blob/main/readme-resources/sign_up.gif" alt="Sign up">
</p>
<br>

#### 2. User sign in: 
<p align="center">
  <img height="350px" src="https://github.com/Jos3lu/EnjoyMadrid/blob/main/readme-resources/sign_in.gif" alt="Sign up">
</p>
<br>

#### 3. User update profile:
<p align="center">
  <img height="350px" src="https://github.com/Jos3lu/EnjoyMadrid/blob/main/readme-resources/update_user.gif" alt="Sign up">
</p>
<br>

#### 4. User delete account:
<p align="center">
  <img height="350px" src="https://github.com/Jos3lu/EnjoyMadrid/blob/main/readme-resources/delete_user.gif" alt="Sign up">
</p>
<br>

#### 5. Explore places in Madrid:
<p align="center">
  <img height="350px" src="https://github.com/Jos3lu/EnjoyMadrid/blob/main/readme-resources/find_places.gif" alt="Sign up">
</p>
<br>

#### 6. Find information about the place you want to visit:
<p align="center">
  <img height="350px" src="https://github.com/Jos3lu/EnjoyMadrid/blob/main/readme-resources/select_places.gif" alt="Sign up">
</p>
<br>

#### 7. Create route:
<p align="center">
  <img height="350px" src="https://github.com/Jos3lu/EnjoyMadrid/blob/main/readme-resources/create_route.gif" alt="Sign up">
</p>
<br>

#### 8. View the route itinerary:
<p align="center">
  <img height="350px" src="https://github.com/Jos3lu/EnjoyMadrid/blob/main/readme-resources/display_route.gif" alt="Sign up">
</p>
<br>

#### 9. Retrieve store routes:
<p align="center">
  <img height="350px" src="https://github.com/Jos3lu/EnjoyMadrid/blob/main/readme-resources/routes_user.gif" alt="Sign up">
</p>
<br>

#### 10. Delete the route you no longer need:
<p align="center">
  <img height="350px" src="https://github.com/Jos3lu/EnjoyMadrid/blob/main/readme-resources/delete_route.gif" alt="Sign up">
</p>
<br>

## Setting Up 

To run the server (is implemented as a REST API) download and run the jar file as follows (the jar file is located in Releases).\
`java -jar enjoymadrid-backend-0.0.1-SNAPSHOT.jar`\
Where enjoymadrid-backend-0.0.1-SNAPSHOT.jar is the path to the jar file on your file system.
Server needs a MariaDB database running. When you create the database call it enjoy_madrid.
```
Database access credentials:
	Username: root
	Password: 1234
```

Finally, download the openrouteservice-master folder, navigate to the docker directory and execute the following command:\
`docker-compose up`

Page can be found here: [https://enjoy-madrid-d18ed.web.app/](https://enjoy-madrid-d18ed.web.app/)
