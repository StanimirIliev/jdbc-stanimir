CREATE TABLE ArticlesHistory(
    Id INT NOT NULL AUTO_INCREMENT,
    ArticleId INT NOT NULL,
    Operation ENUM('MODIFIED', 'DELETED'),
    Description VARCHAR(255) NOT NULL,
    Unit ENUM('KILOGRAM', 'METER', 'PIECE', 'PACKET') NOT NULL,
    Price FLOAT NOT NULL,
    Availability FLOAT NOT NULL,
    PRIMARY KEY(Id)
)