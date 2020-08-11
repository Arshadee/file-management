# SpringBoot-File-Management
## For QDB Assessment

## Clone the application

https://github.com/Arshadee/file-management.git


## Build / Compile and Run the application using my scripts
In the main project folder, **/file-management/** run the following: on macos / unix operating systems
**chmod +rx buildRun.sh**
**./buildRun.sh**

## Run the application only using my scripts
In the main project folder, **/file-management/**, run the following: on mac-os / unix operating systems
**chmod +rx run.sh**
.**/run.sh**

#Build## Build / Compile the application using maven (manually)
In command-Line / term navigate to the project folder  
**/file-management/**
and run : **mvn clean install**  


## Run the application  (manually) 
In command-Line / term navigate to the project folder  
**/file-management/**
and run : **java -jar target/file-management-0.0.1-SNAPSHOT.jar**  

The application will start running at    
http://localhost:8080

## Database Info
**H2 In Mem Database**
To access the database while running the API
use the following url:
http://localhost:8080/h2

**Connection Details:**
**Driver Class:** orge.h2.Driver
**JDBC URL:** jdbc:h2:mem:usrdocdb
**User Name:** sa
**Password:**{blank}


## Explore REST API

Create / update a User and upload a document:
POST http://localhost:8080/upload/{username}

Getting a file by username and document name:
GET http://localhost:8080/file/{username}/{documentName}

Getting a List of all files / documents uploaded by a User:
GET http://localhost:8080/files/{username}

Deleting a file need to specify username and filename:
DELETE http://localhost:8080/upload/{username}/{fileName}

Deleting all file by a user:
DELETE http://localhost:8080/upload/{username}

Adding / creating a post against a document and user (document owner)
POST 
http://localhost:8080/username/{username}/document/{documentName}/posts

Getting a Post against a document usig the document and username.
GET 
http://localhost:8080/username/{username}/document/{documentName}/posts

Posting Comment(s) against Post. 
POST http://localhost:8080/document/posts/{postId}/comments

Getting all Comments for a specific Post
GET http://localhost:8080/document/posts/{postId}/comments

Getting a specific Comment 
GET http://localhost:8080/document/posts/{postId}/comment/{commentId}



