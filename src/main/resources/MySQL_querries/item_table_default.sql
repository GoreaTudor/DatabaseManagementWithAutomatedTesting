DROP PROCEDURE IF EXISTS newItem;
DROP TABLE IF EXISTS item_table;

CREATE TABLE item_table (
	item_id INT NOT NULL AUTO_INCREMENT,
    item_name VARCHAR(45) NOT NULL,
    item_description VARCHAR(100) NOT NULL,
    PRIMARY KEY(item_id)
);

DELIMITER $proc_delim$
CREATE PROCEDURE newItem (IN new_item_name VARCHAR(45), IN new_item_desc VARCHAR(100))
BEGIN
	INSERT INTO item_table (item_name, item_description) VALUES (new_item_name, new_item_desc);
END $proc_delim$

DELIMITER ;

CALL newItem('Microsoft Office', 'contains: Word, Excel, PowerPoint'); 	-- 1
CALL newItem('Windows 10 License', ''); 								-- 2
CALL newItem('WinRAR License', ''); 									-- 3
CALL newItem('HP Laptop', 'model: Chromebook x360'); 					-- 4
CALL newItem('HP Printer', 'model: HP Laserjet 1018'); 					-- 5
CALL newItem('Dell Laptop', 'model: Vostro 5502'); 						-- 6
CALL newItem('Apple Laptop', 'model: MacBook Air'); 					-- 7
CALL newItem('HAMA Mouse', 'model: Wireless HAMA MW-110'); 				-- 8
CALL newItem('Logitech Mouse', 'model: Wireless Logitech M185'); 		-- 9
CALL newItem('EPSON Printer', 'model: EPSON Inkjet L810 CISS'); 		-- 10
CALL newItem('Paper Stack', ''); 										-- 11

SELECT * FROM item_table;
