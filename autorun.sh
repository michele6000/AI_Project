sudo chmod 777 autorun.sh

echo "|\_                  _"
echo " \ \               _/_|"
echo "  \ \_          __/ /"
echo "   \  \________/   /"
echo "    |   welcome!   |"
echo "    /              |"
echo "   |   0       0   |"
echo "   |       _       |"
echo "   |()    __    () |"
echo "    \    (__)      |"
echo " "
cd ./back-end
docker build -f Dockerfile -t back-end .
echo " "
echo "############## Backend builded! ##############"
echo " "
cd ..
cd ./front-end
docker build -f Dockerfile -t front-end .
echo " "
echo "############## Front builded! ##############"
echo " "
cd ..
echo " "
echo "############## Pulling MariaDB ##############"
echo " "
docker pull mariadb:latest
echo " "
echo "############## Starting all services ##############"
echo " "
docker-compose -f docker-compose-offline.yml up
echo " "
echo "############## All service stopped ##############"
