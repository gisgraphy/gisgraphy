#!/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
curDir=`pwd`
if [[ -e ../extra/delete.sh ]]
then
	cd ../extra/
	./delete.sh
	cd $curDir
fi

mvn clean  compile  war:war  assembly:assembly -Dmaven.test.skip  -Dresetdb=false -Pprod
