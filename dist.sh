#!/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
curDir=`pwd`
if [[ -e ../extra/delete.sh ]]
then
	cd ../extra/
	./delete.sh
	cd $curDir
fi

cd ../tools
./installall.sh
cd -

mvn clean compile javadoc:javadoc war:war hibernate3:hbm2ddl assembly:assembly -Dmaven.test.skip  -Dresetdb=false -Pprod
