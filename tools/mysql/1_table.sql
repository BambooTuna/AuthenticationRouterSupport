DROP SCHEMA IF EXISTS authentication;
CREATE SCHEMA authentication;
USE authentication;

CREATE TABLE `authentication_data` (
    `id` VARCHAR(255) NOT NULL,
    `login_id` VARCHAR(255) NOT NULL,
    `encryption_pass` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY (`login_id`)
)
