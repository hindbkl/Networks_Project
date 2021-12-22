# Communication Networks project : Secure messaging application

This application allows a user to create a new account, he can then connect to the application using his login and password. Once he connected, he can select a contact (any other user that
has made an account) to see all the previous messages they send to each other, write a new message and send it. On the messages page, the user can use the search bar to find a particular contact too. 

## Configuration 

Java version : 
- Java 1.8.0_292 (Amazon Corretto 8) (project language level : Java 8)

Librairies : 
- postgresql-42.2.20 (already in project files)

IDE used :
- IntelliJ IDEA 2021.3

Database system :
- PostgreSQL 14

## Launch

- Create a new database on PostgreSQL
- Add in "Utils" the file "postgresql-config.txt" and write inside :
  - Name of the database (first line)
  - Name of the user (second line)
  - Password (third line) 
- Run the server class once to instantiate the server
- Run the main class everytime you want to connect with a user (different users can be connected at the same time)

## Using the application

- Log in page :
The first page that displays when you launch the code is the log in page, where you can enter your already existing username and the password that corresponds. By clicking on the log in button, if the username and password exist and correspond
the page changes to the messages display. If the user is not registered yet, he can click on register to create a new account. 

![](https://github.com/hindbkl/Networks_Project/blob/master/images/login2.png)

- Register page :
On this page, the user has to enter a username, a password and confirm the password. By clicking on the register button, the program will verify if the informations he entered respect the rules
for the password and username (at least 8 characters, the two passwords should be the same, etc...). If everything is verified, the new user will be created and he wil be sent back to the log in page. If the informations are
not correct, the program will display an error message which tells what kind of error is encountered.

![](https://github.com/hindbkl/Networks_Project/blob/master/images/reg.png)

- Messages page : 
On the left of this page, the list of contacts is displayed, with a search bar above it. By writing any string in the search bar and clicking on the search button, the program displays a new list 
of contacts whose name containes the string. By not writing anything in the search bar and clicking on the button, the program displays all the contacts. When you click on a contact, the program will display
all the messages between the user and the contact (if they already talked to each other) on the right side of the page. After selecting a contact, the user can write any message on the message bar, and by clicking on the send button,
the message is added to the list of messages above. The user can choose to click on the log out button too, which will disconnect the user and send him back to the log in page.

![](https://github.com/hindbkl/Networks_Project/blob/master/images/msg.png)

## Previous functional version 

- Issues : 
When adding encryption and undirect communication between client and server, unsolvable errors were raised. Before that, a version with direct access from client to server was implemented. This version can be found in commit e39ae3a (e39ae3a1b35f0aa67e368aa424a0a3c0484af40e - "Merge remote-tracking branch 'origin/master' into master").

- Direct client/server communication :
In the version cited above, clients can register, login, and communicate from the same laptop. Here is the message view that it would give :

![](https://github.com/hindbkl/Networks_Project/blob/master/images/msg.png)
