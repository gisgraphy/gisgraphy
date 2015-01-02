# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

First edit the jdbc properties in /webapps/ROOT/WEB-INF/classes/jdbc.properties
Optionnaly setup some preferences in webapps/ROOT/WEB-INF/classes/env.properties

Some scripts are provided to manage gisgraphy, most of them are for unix / linux :
- launch : run gisgraphy and output logs to the console.
- start : starts Gisgraphy.
- stop : Shutdown gisgraphy (clean way).
- force-stop : kill gisgraphy (doesn't check the status).
- status : tell whether gisgraphy is started or not.
- logs : output logs file to the console.
- respawn : check gisgraphy status and re-launch it if it has gone.
- watch : run the respawner.
- unwatch : stop the respawner.
- startupscript : script to run gisgraphy as a daemon.
- setGisgrapgyAsService : intall the startup script (unix only).


Read the installation guide in the "docs" directory or online (last updates and corrections) : http://www.gisgraphy.com/documentation/installation/index.htm

Note : you may need to change script permission on Linux / Macintosch : chmod +x ./launch.sh 

Open http://localhost:8080/ to connect to Gisgraphy and http://localhost:8080/solr/admin/ in a browser to connect to SOLR admin page
Open http://localhost:8080/mainMenu.html to go to the admin page (import data / statistics...)

Read the documentation in the "docs" directory or online  : http://www.gisgraphy.com/documentation/index.htm

 Still have questions or trouble ?
 Forum : http://www.gisgraphy.com/forum
 Mail : davidmasclet@gisgraphy.com
 Send feedbacks : http://www.gisgraphy.com/feedback/index.htm


