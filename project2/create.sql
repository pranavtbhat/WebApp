CREATE TABLE IF NOT EXISTS Users(
    UserID varchar(100) NOT NULL,
    Location varchar(200),
    Country varchar(15),
    Rating INT,
    PRIMARY KEY(UserID)
);

CREATE TABLE IF NOT EXISTS Bids( 
    ItemID INT NOT NULL,
    UserID varchar(100) NOT NULL,
    Time TIMESTAMP NOT NULL,
    Amount DECIMAL(8,2) NOT NULL,
    PRIMARY KEY(ItemID, UserID, Time)
);

CREATE TABLE IF NOT EXISTS Items(
    ItemID INT NOT NULL,
    SellerID varchar(100) NOT NULL,
    Name varchar(100) NOT NULL,
    Currently DECIMAL(8,2) NOT NULL,
    Buy_Price DECIMAL(8,2),
    First_Bid DECIMAL(8,2) NOT NULL,
    Location varchar(200) NOT NULL,
    Longitude FLOAT(9, 6),
    Latitude FLOAT(9, 6),
    Country varchar(20) NOT NULL,
    Started TIMESTAMP NOT NULL,
    Ends TIMESTAMP NOT NULL,
    Description varchar(4000) NOT NULL,
    PRIMARY KEY(ItemID)
);

CREATE TABLE IF NOT EXISTS ItemCategory(
    ItemID INT NOT NULL,
    Category varchar(100) NOT NULL,
    PRIMARY KEY(ItemID, Category)
);

CREATE TABLE IF NOT EXISTS SellerRating(
    UserID varchar(100) NOT NULL,
    Rating INT NOT NULL,
    PRIMARY KEY(UserID)
);

