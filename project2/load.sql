LOAD DATA LOCAL INFILE 'parsed/Users.dat' INTO TABLE Users FIELDS TERMINATED BY '<?>';
LOAD DATA LOCAL INFILE 'parsed/Bids.dat' INTO TABLE Bids FIELDS TERMINATED BY '<?>';

LOAD DATA LOCAL INFILE 'parsed/Items.dat' INTO TABLE Items 
FIELDS TERMINATED BY '<?>'
(ItemID, SellerID, Name, Currently, @Buy_Price, First_Bid, Location, @Longitude, @Latitude, Country, Started, Ends, Description) 
SET Longitude = IF(@Longitude='null', NULL, @Longitude), 
    Latitude = IF(@Latitude='null', NULL, @Latitude), 
    Buy_Price= IF(@Buy_Price='null', NULL, @Buy_Price
);

LOAD DATA LOCAL INFILE 'parsed/ItemCategory.dat' INTO TABLE ItemCategory FIELDS TERMINATED BY '<?>';
LOAD DATA LOCAL INFILE 'parsed/SellerRating.dat' INTO TABLE SellerRating FIELDS TERMINATED BY '<?>';
