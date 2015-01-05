-- edit user/password/database if needed. The postgis path depends on your linux distribution and postgres/postgis version and should be probably modified
 
psql -U postgres  -h 127.0.0.1 -c  "CREATE DATABASE gisgraphy ENCODING = 'UTF8';"
createlang -U postgres -h 127.0.0.1 plpgsql gisgraphy 

-- for postgis 1.5
psql -U postgres  -h 127.0.0.1 -d gisgraphy -f /usr/share/postgresql/9.1/contrib/postgis-1.5/postgis.sql
psql -U postgres  -h 127.0.0.1 -d gisgraphy -f /usr/share/postgresql/9.1/contrib/postgis-1.5/spatial_ref_sys.sql

-- For postgis 2
--psql -U postgres  -h 127.0.0.1 -d gisgraphy -f /usr/share/postgresql/9.3/contrib/postgis-2.1/postgis.sql
--psql -U postgres  -h 127.0.0.1 -d gisgraphy -f /usr/share/postgresql/9.3/contrib/postgis-2.1/spatial_ref_sys.sql 
