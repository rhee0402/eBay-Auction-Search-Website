# eBay Auction Search Website: Database Implementation
Database Implementation

Design of Database:
    R1 - Bidders: {UserId*, Rating_Bidder, Location, Country}
    R2 - Sellers: {UserId*, Rating_Seller}
    R3 - Bids: {ItemId*, UserId, Time*, Amount}
    R4 - Categories: {ItemId*, Category*};
    R5 - Items: {ItemId*, Name, UserId, Currently, Buy_Price, First_Bid,Number_of_Bids, Location, Country, 
Latitude, Longitude, Started, Ends, Description}

star(*) represent key for each relation(table).

Overview of important files:
MyParser.java: take XML files and generate five csv files that contain same information as given XML files.

create.sql: create tables inside the SQL database. 

drop.sql: delete tables created by the create.sql inside the SQL database.

load.sql: load information in five csv files onto the tables created by the create.sql.

ebay-data directory: contain xml files for past eBay Auctions.