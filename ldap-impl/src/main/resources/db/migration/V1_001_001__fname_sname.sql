ALTER TABLE `entry` 
ADD COLUMN `given_name` VARCHAR(128) NULL AFTER `name`,
ADD COLUMN `family_name` VARCHAR(128) NULL AFTER `name`;
