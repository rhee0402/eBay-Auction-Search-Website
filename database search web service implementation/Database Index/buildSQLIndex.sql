CREATE TABLE ItemPosition (itemId BIGINT, position POINT NOT NULL) ENGINE = MYISAM;
INSERT INTO ItemPosition (itemId, position) SELECT itemId, POINT(latitude,longitude) FROM Items WHERE latitude != 0.0 OR longitude != 0.0 OR latitude != null OR longitude != null;
CREATE SPATIAL INDEX sp_index ON ItemPosition(position);


