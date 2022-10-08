create table SYSTEM_ITEM (
    id VARCHAR(20) NOT NULL PRIMARY KEY,
    url VARCHAR(255),
    update_date BIGINT NOT NULL,
    parent_id VARCHAR(20),
    type VARCHAR(6) NOT NULL,
    size_it BIGINT,
    children TEXT
);

create table IF NOT EXISTS SYSTEM_ITEM_FILE (
    id VARCHAR(20) NOT NULL PRIMARY KEY,
    url VARCHAR(255),
    update_date BIGINT NOT NULL,
    parent_id VARCHAR(20),
    size_it BIGINT
);

create table IF NOT EXISTS SYSTEM_ITEM_FOLDER (
    id VARCHAR(20) NOT NULL PRIMARY KEY,
    url VARCHAR(255),
    update_date BIGINT NOT NULL,
    parent_id VARCHAR(20),
    size_it BIGINT,
    children TEXT
);
