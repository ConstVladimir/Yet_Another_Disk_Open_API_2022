create table IF NOT EXISTS SYSTEM_ITEM_FILE (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    url VARCHAR(255),
    update_date BIGINT NOT NULL,
    parent_id VARCHAR(36),
    size_it BIGINT
);

create table IF NOT EXISTS SYSTEM_ITEM_FOLDER (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    url VARCHAR(255),
    update_date BIGINT NOT NULL,
    parent_id VARCHAR(36),
    size_it BIGINT,
    children TEXT
);
