CREATE TABLE IF NOT EXISTS ItemLocation(
    ItemID INT,
    Location POINT NOT NULL, 
    PRIMARY KEY(ItemID, Location)
) ENGINE=MyISAM;


INSERT INTO ItemLocation
SELECT ItemID, POINT(Latitude, Longitude)
FROM Items
WHERE Latitude <> 'Null' AND Longitude <> 'Null';

CREATE SPATIAL INDEX sp_index ON ItemLocation (Location);
