CREATE TABLE `pub_key` (
  `id` VARCHAR(36) NOT NULL,
  `serial` BIGINT(20) NOT NULL,
  `algorithm` VARCHAR(45) NOT NULL,
  `key` MEDIUMTEXT NOT NULL,
  PRIMARY KEY (`id`));
