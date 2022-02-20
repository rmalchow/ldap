CREATE TABLE otp (
	`id` varchar(36) NOT NULL,
	`secret` varchar(100) NOT NULL,
	`algorithm` varchar(100) NOT NULL,
	`interval` INT NOT NULL,
	`digits` INT NOT NULL,
	PRIMARY KEY (`id`)
);

