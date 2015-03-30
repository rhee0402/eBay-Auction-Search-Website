# eBay Auction Search Website: Database Index
Database Index
provide two indexes:
1. Lucene index on name, categories, and description all concavinated together.
2. SQL index on longitude,latitude of item for the spatial search.

Overview of Important files:

Indexer.java: create Lucene index on concavinated string of name, categories, and description of items.

buildSQLIndex.sql: create SQL index on longitude,latitude of item.

dropSQLIndex.sql: delete SQL index created by above file.
