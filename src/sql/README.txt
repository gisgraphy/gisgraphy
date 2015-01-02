 If you want to set up the Gisgraphy database, you can consult the "setup your environnement" section at http://www.gisgraphy.com/documentation/installation/index.htm
 
 the script can be run by running the following command in a command window or a Linux shell :
 
  psql -U YOURUSER -h YOURIP -f PATHTOFILE
  
  example :
   psql -U postgres -h 127.0.0.1 -d gisgraphy  -f createGISTIndex.sql
 
 There is too different script to create the database : one for windows and one for Linux. There is a difference because the name of the template is different.
 
 the createGISTIndex.sql create the spatial index for the geometry columns, this script is provided but the spatial Gist indexes are now created by the importer.
 
 The resetdb.sql is the script you should run if the "reset import has failed" via the admin interface fail :
 login to the mainMenu.html page=>Administration=>Reset Import.
 Run the resetdb script only if there is some errors, the warnings can be ignored. 
 
 here is the command line you should run to manually reset the import :
 psql -U YOURUSER -h YOURIP -d gisgraphy  -f resetdb.sql
 psql -U YOURUSER -h YOURIP -d gisgraphy  -f create_tables.sql
 
 the insert_users.sql script is the one that insert the admin and a simple user. the password are the same as the login. YOU MUST CHANGE it the first time you login.
 via the "edit profile" menu. if you've check the remember me checkbox on the login page. you must logout and login to be abble to change the password.
 
 Still have questions or trouble ?
 Forum : http://www.gisgraphy.com/forum
 Mail : davidmasclet@gisgraphy.com
 Send feedbacks : http://www.gisgraphy.com/feedback/index.htm
    
 