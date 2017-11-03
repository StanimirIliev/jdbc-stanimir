CREATE TABLE Users(
    Id INT NOT NULL AUTO_INCREMENT,
    FirstName VARCHAR(255) NOT NULL,
    LastName VARCHAR(255) NOT NULL,
    Age INT NOT NULL,
    Phone VARCHAR(255) NOT NULL,
    PRIMARY KEY(Id),
    UNIQUE KEY NameAndPhone (FirstName, LastName, Phone)
)