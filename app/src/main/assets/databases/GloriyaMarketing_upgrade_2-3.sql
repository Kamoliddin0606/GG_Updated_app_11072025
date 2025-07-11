ALTER TABLE `orders` ADD `tp_code` TEXT;
ALTER TABLE `orders` ADD `order_type` INTEGER;

CREATE TABLE `dealers` (
	`code`	TEXT,
	`name`	TEXT,
	`updated_date`	TEXT
);

CREATE TABLE `parcel_temp` (
	`br_code`	TEXT,
	`br_name`	TEXT,
	`comment`	TEXT,
	`currency_type`	TEXT,
	`summa`	TEXT,
	`rate` TEXT
);