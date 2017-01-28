SELECT COUNT(DISTINCT UserID) FROM ((SELECT DISTINCT UserID FROM Users) UNION (SELECT DISTINCT UserID FROM SellerRating))T;

SELECT COUNT(DISTINCT ItemID) FROM Items WHERE BINARY Location = "New York";

SELECT COUNT(DISTINCT ItemID) FROM (SELECT * FROM ItemCategory GROUP BY ItemID HAVING COUNT(Category) = 4) as temp;

SELECT DISTINCT ItemID FROM Bids WHERE Amount = (SELECT MAX(Amount) FROM (select ItemID FROM Items WHERE Ends > timestamp('2001-12-20')) as temp NATURAL JOIN Bids);

SELECT COUNT(DISTINCT UserID) FROM SellerRating WHERE Rating > 1000; 

SELECT COUNT(DISTINCT UserID) FROM (SELECT DISTINCT UserID FROM SellerRating) as temp NATURAL JOIN Users;

SELECT COUNT(DISTINCT Category) FROM (SELECT DISTINCT ItemID FROM Bids WHERE Amount > 100) as temp NATURAL JOIN ItemCategory;