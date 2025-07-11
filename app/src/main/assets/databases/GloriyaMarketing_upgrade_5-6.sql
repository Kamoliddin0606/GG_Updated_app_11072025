CREATE TABLE `organization` (
	`code`	TEXT,
	`name`	TEXT
);

CREATE TABLE `stock` (
	`code`	TEXT,
	`name`	TEXT,
	`code_org`	TEXT
);

ALTER TABLE `orders` ADD `code_warehouse` TEXT;
ALTER TABLE `orders` ADD `code_org` TEXT;
