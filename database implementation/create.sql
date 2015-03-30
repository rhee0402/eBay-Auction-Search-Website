CREATE TABLE Sellers(
	userId VARCHAR(100),
        rating INT,
	PRIMARY KEY (userId)
        );

CREATE TABLE Bidders(
	userId VARCHAR(100),
	rating INT,
	location VARCHAR(100),
        country VARCHAR(100),
	PRIMARY KEY (userId)
	);

CREATE TABLE Items(
	itemId BIGINT,
	name VARCHAR(100),
	sellerId VARCHAR(100),
	currently DECIMAL(8,2),
	buy_price DECIMAL(8,2),
	first_bid DECIMAL(8,2),
	number_of_bids INT,
	location VARCHAR(100),
	country VARCHAR(100),
	latitude INT,
	longitude INT,
	started TIMESTAMP,
	ends TIMESTAMP,
	description VARCHAR(4000),
	PRIMARY KEY(itemId),
	FOREIGN KEY(sellerId) REFERENCES Sellers(userId)
	);

CREATE TABLE Bids(
	itemId BIGINT,
	bidderId VARCHAR(100),
	time TIMESTAMP,
	amount DECIMAL(8,2),
	PRIMARY KEY(itemId, bidderId, time),
	FOREIGN KEY(itemId) REFERENCES Items(itemId),
	FOREIGN KEY(bidderId) REFERENCES Bidders(userId)
	);

CREATE TABLE Categories(
	itemId BIGINT,
	category VARCHAR(100),
	PRIMARY KEY(itemId, category),
	FOREIGN KEY(itemId) REFERENCES Items(itemId)
	);

