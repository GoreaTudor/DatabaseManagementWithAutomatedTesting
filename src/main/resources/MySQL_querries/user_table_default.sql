DROP PROCEDURE IF EXISTS newUser;
DROP VIEW IF EXISTS users;
DROP VIEW IF EXISTS admins;
DROP TABLE IF EXISTS user_table;

CREATE TABLE user_table (
	username VARCHAR(20) NOT NULL,
    password INT NOT NULL,
    isAdmin TINYINT NOT NULL,
    PRIMARY KEY (username)
);

CREATE VIEW users AS
SELECT username, password
FROM user_table
WHERE isAdmin = false;

CREATE VIEW admins AS
SELECT username, password
FROM user_table
WHERE isAdmin = true;

-- DELIMITER $$ resets the delimiter ';' to a new custom delimiter
DELIMITER $proc_delim$
CREATE PROCEDURE newUser (IN username VARCHAR(20), IN pwd INT, IN isAdmin TINYINT)
BEGIN
	INSERT INTO user_table VALUES (username, pwd, isAdmin);
END $proc_delim$

-- resets the delimiter back to ';'
DELIMITER ;

CALL newUser ('admin1', 2432963, true);  -- password = 1234 
CALL newUser ('admin2', 2463747, true);  -- password = 2345
CALL newUser ('user1', 2431937, false);  -- password = 1111
CALL newUser ('user2', 2462721, false);  -- password = 2222
CALL newUser ('user3', 2493505, false);  -- password = 3333
CALL newUser ('user4', 2524289, false);  -- password = 4444
CALL newUser ('user5', 2555073, false);  -- password = 5555

SELECT * FROM user_table;
