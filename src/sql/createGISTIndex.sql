
-- usage : psql -UYOURUSER -h 127.0.0.1 -d gisgraphy -f /path/to/file/createGISTIndex.sql


\connect gisgraphy
\echo will create all the index needed by gisgraphy to improve performance, this make take a while depends on how many data are in database
\echo will create Geonames Index
CREATE INDEX locationIndexAdm ON adm USING GIST (location);
CREATE INDEX locationIndexAirport ON airport USING GIST (location);
CREATE INDEX locationIndexAmusePark ON amusePark USING GIST (location);
CREATE INDEX locationIndexAqueduc ON quay USING GIST (location);
CREATE INDEX locationIndexATM ON ATM USING GIST (location);
CREATE INDEX locationIndexBank ON bank USING GIST (location);
CREATE INDEX locationIndexBar ON bar USING GIST (location);
CREATE INDEX locationIndexBay ON bay USING GIST (location);
CREATE INDEX locationIndexBeach ON beach USING GIST (location);
CREATE INDEX locationIndexBridge ON bridge USING GIST (location);
CREATE INDEX locationIndexBuilding ON building USING GIST (location);
CREATE INDEX locationIndexBusStation ON busStation USING GIST (location);
CREATE INDEX locationIndexCamp ON camp USING GIST (location);
CREATE INDEX locationIndexCanyon ON canyon USING GIST (location);
CREATE INDEX locationIndexCasino ON casino USING GIST (location);
CREATE INDEX locationIndexCastle ON castle USING GIST (location);
CREATE INDEX locationIndexCemetery ON cemetery USING GIST (location);
CREATE INDEX locationIndexCirque ON cirque USING GIST (location);
CREATE INDEX locationIndexCity ON city USING GIST (location);
CREATE INDEX locationIndexCitySubdivision ON citySubdivision USING GIST (location);
CREATE INDEX locationIndexCliff ON cliff USING GIST (location);
CREATE INDEX locationIndexCoast ON coast USING GIST (location);
CREATE INDEX locationIndexContinent ON continent USING GIST (location);
CREATE INDEX locationIndexCountry ON country USING GIST (location); 
CREATE INDEX locationIndexCourtHouse ON courtHouse USING GIST (location);
CREATE INDEX locationIndexCustomsPost ON customsPost USING GIST (location);
CREATE INDEX locationIndexDam ON dam USING GIST (location);
CREATE INDEX locationIndexDesert ON desert USING GIST (location);
CREATE INDEX locationIndexFactory ON factory USING GIST (location);
CREATE INDEX locationIndexFalls ON falls USING GIST (location);
CREATE INDEX locationIndexFarm ON farm USING GIST (location);
CREATE INDEX locationIndexField ON field USING GIST (location);
CREATE INDEX locationIndexFishingArea ON fishingArea USING GIST (location);
CREATE INDEX locationIndexFjord ON fjord USING GIST (location);
CREATE INDEX locationIndexForest ON forest USING GIST (location);
CREATE INDEX locationIndexGarden ON garden USING GIST (location);
CREATE INDEX locationIndexGisFeature ON gisFeature USING GIST (location);
CREATE INDEX locationIndexGolf ON golf USING GIST (location);
CREATE INDEX locationIndexGorge ON gorge USING GIST (location);
CREATE INDEX locationIndexGrassLand ON grassLand USING GIST (location);
CREATE INDEX locationIndexGulf ON gulf USING GIST (location);
CREATE INDEX locationIndexHill ON hill USING GIST (location);
CREATE INDEX locationIndexHospital ON hospital USING GIST (location);
CREATE INDEX locationIndexHotel ON hotel USING GIST (location);
CREATE INDEX locationIndexHouse ON house USING GIST (location);
CREATE INDEX locationIndexIce ON ice USING GIST (location);
CREATE INDEX locationIndexIsland ON island USING GIST (location);
CREATE INDEX locationIndexLake ON lake USING GIST (location);
CREATE INDEX locationIndexLibrary ON library USING GIST (location);
CREATE INDEX locationIndexLightHouse ON lightHouse USING GIST (location);
CREATE INDEX locationIndexMall ON mall USING GIST (location);
CREATE INDEX locationIndexMarsh ON marsh USING GIST (location);
CREATE INDEX locationIndexMetroStation ON metroStation USING GIST (location);
CREATE INDEX locationIndexMilitary ON military USING GIST (location);
CREATE INDEX locationIndexMill ON mill USING GIST (location);
CREATE INDEX locationIndexMine ON mine USING GIST (location);
CREATE INDEX locationIndexMole ON mole USING GIST (location);
CREATE INDEX locationIndexMonument ON monument USING GIST (location);
CREATE INDEX locationIndexMound ON mound USING GIST (location);
CREATE INDEX locationIndexMountain ON mountain USING GIST (location);
CREATE INDEX locationIndexMuseum ON museum USING GIST (location);
CREATE INDEX locationIndexOasis ON oasis USING GIST (location);
CREATE INDEX locationIndexObservatoryPoint ON observatoryPoint USING GIST (location);
CREATE INDEX locationIndexOcean ON ocean USING GIST (location);
CREATE INDEX locationIndexOperaHouse ON operaHouse USING GIST (location);
CREATE INDEX locationIndexPark ON park USING GIST (location);
CREATE INDEX locationIndexParking ON parking USING GIST (location);
CREATE INDEX locationIndexPlantation ON plantation USING GIST (location);
CREATE INDEX locationIndexPolicePost ON policePost USING GIST (location);
CREATE INDEX locationIndexPoliticalEntity ON politicalEntity USING GIST (location);
CREATE INDEX locationIndexPond ON pond USING GIST (location);
CREATE INDEX locationIndexPort ON port USING GIST (location);
CREATE INDEX locationIndexPostOffice ON postOffice USING GIST (location);
CREATE INDEX locationIndexPrison ON prison USING GIST (location);
CREATE INDEX locationIndexPyramid ON pyramid USING GIST (location);
CREATE INDEX locationIndexQuay ON quay USING GIST (location);
CREATE INDEX locationIndexRail ON rail USING GIST (location);
CREATE INDEX locationIndexRailRoadStation ON railRoadStation USING GIST (location);
CREATE INDEX locationIndexRanch ON ranch USING GIST (location);
CREATE INDEX locationIndexRavin ON ravin USING GIST (location);
CREATE INDEX locationIndexReef ON reef USING GIST (location);
CREATE INDEX locationIndexReligious ON religious USING GIST (location);
CREATE INDEX locationIndexReserve ON reserve USING GIST (location);
CREATE INDEX locationIndexRestaurant ON restaurant USING GIST (location);
CREATE INDEX locationIndexRoad ON road USING GIST (location);
CREATE INDEX locationIndexSchool ON school USING GIST (location);
CREATE INDEX locationIndexSea ON sea USING GIST (location);
CREATE INDEX locationIndexSpring ON spring USING GIST (location);
CREATE INDEX locationIndexStadium ON stadium USING GIST (location);
CREATE INDEX locationIndexStrait ON strait USING GIST (location);
CREATE INDEX locationIndexStream ON stream USING GIST (location);
CREATE INDEX locationIndexStreet ON street USING GIST (location);
CREATE INDEX locationIndexTheater ON theater USING GIST (location);
CREATE INDEX locationIndexTower ON tower USING GIST (location);
CREATE INDEX locationIndexTree ON tree USING GIST (location);
CREATE INDEX locationIndexTunnel ON tunnel USING GIST (location);
CREATE INDEX locationIndexUnderSea ON underSea USING GIST (location);
CREATE INDEX locationIndexVineyard ON vineyard USING GIST (location);
CREATE INDEX locationIndexVolcano ON volcano USING GIST (location);
CREATE INDEX locationIndexWaterBody ON waterBody USING GIST (location);
CREATE INDEX locationIndexZoo ON zoo USING GIST (location);

CREATE INDEX locationIndexAdmBuilding ON AdmBuilding USING GIST (location);
CREATE INDEX locationIndexBench ON Bench USING GIST (location);
CREATE INDEX locationIndexCinema ON Cinema USING GIST (location);
CREATE INDEX locationIndexDentist ON Dentist USING GIST (location);
CREATE INDEX locationIndexDoctor ON Doctor USING GIST (location);
CREATE INDEX locationIndexEmergencyPhone ON EmergencyPhone USING GIST (location);
CREATE INDEX locationIndexFerryTerminal ON FerryTerminal USING GIST (location);
CREATE INDEX locationIndexFireStation ON FireStation USING GIST (location);
CREATE INDEX locationIndexFountain ON Fountain USING GIST (location);
CREATE INDEX locationIndexFuel ON Fuel USING GIST (location);
CREATE INDEX locationIndexNightClub ON NightClub USING GIST (location);
CREATE INDEX locationIndexPharmacy ON Pharmacy USING GIST (location);
CREATE INDEX locationIndexRental ON Rental USING GIST (location);
CREATE INDEX locationIndexShop ON Shop USING GIST (location);
CREATE INDEX locationIndexSwimmingPool ON SwimmingPool USING GIST (location);
CREATE INDEX locationIndexTaxi ON Taxi USING GIST (location);
CREATE INDEX locationIndexTelephone ON Telephone USING GIST (location);
CREATE INDEX locationIndexToilet ON Toilet USING GIST (location);
CREATE INDEX locationIndexVendingMachine ON VendingMachine USING GIST (location);
CREATE INDEX locationIndexVeterinary ON Veterinary USING GIST (location);
CREATE INDEX locationIndexAmbulance ON Ambulance USING GIST (location);
CREATE INDEX locationIndexCamping ON Camping USING GIST (location);
CREATE INDEX locationIndexCave ON Cave USING GIST (location);
CREATE INDEX locationIndexCityHall ON CityHall USING GIST (location);
CREATE INDEX locationIndexCraft ON Craft USING GIST (location);
CREATE INDEX locationIndexPicnic ON Picnic USING GIST (location);
CREATE INDEX locationIndexRestArea ON RestArea USING GIST (location);
CREATE INDEX locationIndexSport ON Sport USING GIST (location);
CREATE INDEX locationIndexTourism ON Tourism USING GIST (location);
CREATE INDEX locationIndexTourismInfo ON TourismInfo USING GIST (location);




\echo will create Openstreetmap index

CREATE INDEX locationindexopenstreetmap ON openstreetmap USING GIST (location);
CREATE INDEX shapeindexopenstreetmap ON openstreetmap USING GIST (shape);
CREATE INDEX shapeindexcity ON city USING GIST (shape);

VACUUM FULL ANALYZE;
