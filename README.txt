To start Make sure you have Gradle installed

if you fail to connect to the database you will need to Delete the DNDStartup.txt file.

Download and create a mysql instance

Make sure before running the program you run the SQL code inside of the MYSQLStartup.txt using mysql

Then if you would like to have data inside of your database run the SQL commands from any of the sourcebook.txts ie (SRD Monsters)
You can also do this in program now!


Finally After setting up the sql Database you can use gradle encounter

Copy the address from mysql by right clicking your instance and copy jdbc instance to clippboard

enter your user by default this is root

enter your password for mysql

If you ever fail to connect to the database after initilization check your services and make sure mysql is running

Caution when adding monsters that have duplicate names if you do this the program will break it is a wip bug to be fixed
For now change the duplicate monster name to something else such as goblin-Multiverse

gradle run -launches all of the applications at the same time
gradle hp -launches the hp tacker application
gradle weather -launches the weather generation applicaiton
gradle encounter -launches the encounter building application