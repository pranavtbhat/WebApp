USE TEST;
CREATE TABLE Actors ( Name Varchar(40), Movie Varchar(80), Year Integer, Role Varchar(40) );
LOAD DATA LOCAL INFILE '~/data/actors.csv' INTO TABLE Actors FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"';
SELECT Name FROM Actors WHERE Movie = "Die Another Day";
DROP TABLE Actors;
