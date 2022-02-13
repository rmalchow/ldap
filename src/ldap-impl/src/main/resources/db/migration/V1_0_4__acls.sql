CREATE TABLE `entry_acl` (
  `id` VARCHAR(36) NOT NULL,
  `entry_id` VARCHAR(36) NULL,
  `principal_id` VARCHAR(36) NULL,
  `permission` VARCHAR(36) NULL,
  `recursive` TINYINT(1) NULL,
  `path` VARCHAR(512) NULL,
  PRIMARY KEY (`id`));
