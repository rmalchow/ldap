ALTER TABLE `entry` 
ADD COLUMN `updated` DATETIME NULL AFTER `id`;

CREATE TABLE `membership` (
  `id` VARCHAR(36) NOT NULL,
  `updated` DATETIME NOT NULL,
  `group_id` VARCHAR(36) NOT NULL,
  `principal_id` VARCHAR(36) NOT NULL,
  `description` VARCHAR(45) NULL,
  PRIMARY KEY (`id`));
