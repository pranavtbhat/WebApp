############################
# PROJECT 2
# 
# Members:
#  Pranav Thulasiram Bhat (704741684)
#  Sachin Krishna Bhat    (304759727)
############################

###
# 1. List of all relations
###

Users{
    *UserID*, 
    Location, 
    Country,
    Rating
}

Bids{ 
    *ItemID* (FK INTO Items), 
    *UserID* (FK INTO Users), 
    *Time*, 
    Amount
}

Items{
    *ItemID*,
    SellerID (FK INTO Users),
    Name,
    Currently,
    Buy_Price,
    First_Bid,
    Number_Of_Bids,
    Location,
    Longitude,
    Latitude,
    Country,
    Started,
    Ends,
    Description
}

ItemCategory{
    *ItemID* (FK INTO Items),
    *Category*
}

SellerRating{
    *UserID* (FK INTO Users),
    Rating
}


###
# 2. Functional Dependencies
###

Users:
    *UserID* -> Rating, Location, Country

Bids:
   *UserID*, *ItemID*, *Time* -> Amount

Items:
    *ItemID* -> Name, SellerID, Currently, Buy_Price, First_Bid, Number_Of_Bids, Location, Longitude, Latitute, Country, Started, Ends, Description

SellerRating:
    *UserID* -> Rating


###
# 3. All relations are already in Boyce-Codd Normal Form (BCNF)
###


###
# 4. All relations are already in Fourth Normal Form (4NF).
###
