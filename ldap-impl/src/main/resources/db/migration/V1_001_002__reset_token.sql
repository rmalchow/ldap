
CREATE TABLE `reset` (
  `id` VARCHAR(36) NOT NULL,
  `user_id` VARCHAR(36) NOT NULL,
  `token` VARCHAR(64) NULL,
  `expires` DATETIME NULL,
  PRIMARY KEY (`id`));
