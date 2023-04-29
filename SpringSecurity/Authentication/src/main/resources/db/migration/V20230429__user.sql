CREATE TABLE IF NOT EXISTS dln_user(
    pk BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    email VARCHAR(320) NOT NULL,
    hash_password varchar(100) NOT NULL,
    salt varchar(25) NOT NULL,
    role ENUM('ADMIN', 'SUBMITTER', 'MANAGER', 'SPECIALIST', 'NONE') NOT NULL,
    PRIMARY KEY (pk),
    UNIQUE KEY email (email)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;