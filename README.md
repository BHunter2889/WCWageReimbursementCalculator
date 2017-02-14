# WCWageReimbursementCalculator
gradle build

***This project was designed, built, and tested in Eclipse IDE. To run the application, simply use the .jar located at: "/build/libs/WCWageReimbursementCalculator-1.0.jar".
Also, the distribution set can be found under at "/build/distributions/WCWageReimbursementCalculator-1.0.zip/lib/WCWageReimbursementCalculator-1.0.jar".
To run in Eclipse, execute the Gradle command "run" on the /build/libs/ .jar file after loading the project into eclipse via pull request. 

***Since this project is run via embedded connection to the DB and uses local System attributes, it should run on most platforms.
***When the application is first initialized, it creates the database in the System.user.home directory. For example, on my system it creates the directory "DerbyDB"
at "C:\Users\Brandon\DerbyDB". If you wish to clean up the DB and remove it from your files after running the program, you can simply delete the "DerbyDB" directory.
