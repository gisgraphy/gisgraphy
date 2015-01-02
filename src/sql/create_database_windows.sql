-- edit user/password/database if needed
 psql -U postgres  -h 127.0.0.1 -c  "CREATE DATABASE gisgraphy WITH TEMPLATE = Template_postgis ENCODING = 'UTF8';"
