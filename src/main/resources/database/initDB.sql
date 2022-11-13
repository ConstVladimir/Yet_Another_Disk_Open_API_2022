CREATE TABLE IF NOT EXISTS SYSTEM_ITEM_FILE (
    id VARCHAR(36) PRIMARY KEY,
    url VARCHAR(255),
    --update_date BIGINT NOT NULL,
    update_date timestamp with time zone,
    parent_id VARCHAR(36) REFERENCES SYSTEM_ITEM_FOLDER (id),
    size_it BIGINT
);

CREATE TABLE IF NOT EXISTS SYSTEM_ITEM_FOLDER (
    id VARCHAR(36) PRIMARY KEY,
    url VARCHAR(255),
    --update_date BIGINT NOT NULL,
    update_date timestamp with time zone,
    parent_id VARCHAR(36) REFERENCES SYSTEM_ITEM_FOLDER (id),
    size_it BIGINT,
    children VARCHAR(36) ARRAY --REFERENCES SYSTEM_ITEM_FILE (id)
);