SELECT
(SELECT COUNT(*) FROM Bidders)+
(SELECT COUNT(*) FROM Sellers)-
(SELECT COUNT(S.userId) FROM Bidders B, Sellers S WHERE B.userId = S.userId)
AS UserCount;

SELECT COUNT(itemId) FROM Items WHERE location LIKE BINARY '"New York"';

SELECT COUNT(*) FROM (SELECT itemId FROM Categories GROUP BY itemId HAVING COUNT(category) = 4) targets;

SELECT itemId FROM Items WHERE number_of_bids > 0 AND currently = (SELECT MAX(currently) FROM Items WHERE number_of_bids > 0 AND ends > '2001-12-20 00:00:01');

SELECT COUNT(*) FROM Sellers S WHERE S.rating > 1000;

SELECT COUNT(Sellers.userId) FROM Sellers INNER JOIN Bidders ON Sellers.userId = Bidders.userId;

SELECT COUNT(*) FROM (SELECT DISTINCT Category FROM Categories WHERE itemId in (SELECT DISTINCT itemId FROM Bids WHERE amount > 100.00)) targets;

