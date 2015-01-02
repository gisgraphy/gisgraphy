Welcome to the dump page.

You can order recent dump on http://www.gisgraphy.com/premium. 

 If you are not interested in all the countries, 
 or, only Openstreetmap dataset, it will takes less times to inject those dump than
 run Gisgraphy importers

The SolR dump must be extracted in the solr/data/ directory
it is required to use the fulltext webservices 

Two postgres dumps are provided :
-> one for openstreetmap data (for street webservice)
-> one for geonames one (for find nearby webservices)

run the folowing command to inject a dump :

psql -U postgres -h 127.0.0.1 -d gisgraphy -f ./THE_FILE_EXTRACTED

Dumps will only inject datas, it will not create the database and tables AND THE SPATIAL indexes (files are provided in the 'sql' directory of the distribution) to get GOOD performances (see installation guide and readme).
Gisgraphy users and role will be added, ignore warning if the users are already sets.

On unix you can inject the zipped dump directly : 
unzip -c dump_localhost_geonames.sql.zip | psql -U postgres -d gisgraphy -W -h127.0.0.1

Hope it helps!
