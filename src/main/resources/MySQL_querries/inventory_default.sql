DROP PROCEDURE IF EXISTS newInv;
DROP PROCEDURE IF EXISTS showInfo;
DROP FUNCTION IF EXISTS countItemIds;
DROP FUNCTION IF EXISTS countAllItems;
DROP VIEW IF EXISTS inventory_view;
DROP TABLE IF EXISTS inventory;


CREATE TABLE inventory (
	username VARCHAR(20) NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL,
    PRIMARY KEY(username, item_id)
);


CREATE VIEW inventory_view AS
SELECT user_table.username, user_table.isAdmin, item_table.item_name, inventory.quantity, item_table.item_description
FROM user_table
LEFT JOIN inventory ON user_table.username = inventory.username
LEFT JOIN item_table ON inventory.item_id = item_table.item_id;


DELIMITER $

CREATE PROCEDURE newInv (IN new_username VARCHAR(20), IN new_item_id INT, IN new_quantity INT)
BEGIN
	INSERT INTO inventory VALUES (new_username, new_item_id, new_quantity);
END$

CREATE PROCEDURE showInfo (IN u VARCHAR(20))
BEGIN
	SELECT item_name, quantity, item_description FROM inventory_view WHERE username = u;
END$

DELIMITER ;


CREATE FUNCTION countItemIds (u VARCHAR(20))
RETURNS INT DETERMINISTIC
RETURN (SELECT COUNT(item_id) FROM inventory WHERE username = u);

CREATE FUNCTION countAllItems (u VARCHAR(20))
RETURNS INT DETERMINISTIC
RETURN (SELECT SUM(quantity) FROM inventory WHERE username = u);


CALL newInv('admin1', 1, 1);
CALL newInv('admin1', 2, 1);
CALL newInv('admin1', 4, 2);
CALL newInv('admin1', 5, 1);
CALL newInv('admin1', 8, 2);
CALL newInv('admin1', 11, 10);

CALL newInv('admin2', 3, 2);
CALL newInv('admin2', 4, 1);
CALL newInv('admin2', 7, 1);
CALL newInv('admin2', 9, 2);
CALL newInv('admin2', 10, 1);
CALL newInv('admin2', 11, 9);

CALL newInv('user1', 2, 1);
CALL newInv('user1', 4, 1);
CALL newInv('user1', 8, 1);

CALL newInv('user2', 1, 1);
CALL newInv('user2', 2, 1);
CALL newInv('user2', 6, 1);
CALL newInv('user2', 11, 3);

CALL newInv('user3', 7, 1);
CALL newInv('user3', 9, 1);

CALL newInv('user4', 1, 1);
CALL newInv('user4', 2, 1);
CALL newInv('user4', 6, 1);
CALL newInv('user4', 8, 1);

CALL newInv('user5', 7, 1);
CALL newInv('user5', 8, 1);
CALL newInv('user5', 10, 1);
CALL newInv('user5', 11, 5);

SELECT * FROM inventory;
