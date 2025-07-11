CREATE TABLE `contract_type` (
	`Name`	TEXT,
	`shouldEnterPassport`	TEXT
);

ALTER TABLE `orders` ADD `code_contract` TEXT;