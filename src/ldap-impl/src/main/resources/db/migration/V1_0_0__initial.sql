CREATE TABLE `entry` (
  `id` VARCHAR(36) NOT NULL,
  `type` VARCHAR(16) NULL,
  `name` VARCHAR(128) NULL,
  `display_name` VARCHAR(128) NULL,
  `username_short` VARCHAR(16) NULL,
  `email` VARCHAR(128) NULL,
  `familyname` VARCHAR(128) NULL,
  `givenname` VARCHAR(128) NULL,
  `parent_id` VARCHAR(36) NULL,
  `dn` VARCHAR(512) NULL,
  `path` VARCHAR(512) NULL,
  PRIMARY KEY (`id`));
