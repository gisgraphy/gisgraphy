#!/bin/sh -e
gisgraphy_dir=`dirname ${BASH_SOURCE[0]}`

function check_root_password {
if [ `id -u` != 0 ]; then
        read -p "this will install gisgraphy as a daemon. It will start Gisgraphy at startup. It has been tested on Ubuntu. No waranty are given. You must run this script as root.!!! Please configure GISGRAPHY_DIR in the startupscript file!!!! Press CTRL+C to abort and run the script as root or enter the root password : " rootpassword
fi
}



check_root_password
if [[ -e /usr/local/gisgraphy ]]
then
	echo "gisgraphy is already install in /usr/local/gisgraphy, we can not make a symbolic links. please remove the /usr/local/gisgraphy directory if you want to start this version at startup"
fi
cd /usr/local/
ln -s $gisgraphy_dir .
cd -


echo "giving rights to scripts"
chmod a+rx ./startupscript
chmod a+rx ./launch.sh
chmod a+rx ./stop.sh
echo "copying script"
sudo cp ./startupscript /etc/init.d/gisgraphy
echo "removing old startup script if necessary"
sudo update-rc.d -f gisgraphy remove
echo "adding startup script"
sudo update-rc.d gisgraphy defaults
