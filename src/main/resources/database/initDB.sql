CREATE TABLE IF NOT EXISTS SYSTEM_ITEM_FOLDER (
    id VARCHAR(36) PRIMARY KEY,
    update_date timestamp with time zone,
    parent_id VARCHAR(36) REFERENCES SYSTEM_ITEM_FOLDER (id),
    ch_files VARCHAR(36) ARRAY,
    ch_folders VARCHAR(36) ARRAY
);

CREATE TABLE IF NOT EXISTS SYSTEM_ITEM_FILE (
    id VARCHAR(36) PRIMARY KEY,
    url VARCHAR(255),
    update_date timestamp with time zone,
    parent_id VARCHAR(36) REFERENCES SYSTEM_ITEM_FOLDER (id),
    size_it BIGINT
);

CREATE OR REPLACE PROCEDURE insert_file (it_id VARCHAR, it_url VARCHAR, it_date TIMESTAMPTZ, id_parent VARCHAR, it_size BIGINT) AS
$$
DECLARE
item_prev_row RECORD;
BEGIN
IF (EXISTS (SELECT * FROM system_item_file WHERE id = it_id))
	THEN
		SELECT * INTO STRICT item_prev_row FROM system_item_file WHERE id = it_id;
        IF (it_date > item_prev_row.update_date)
            THEN
                IF (id_parent != item_prev_row.parent_id)
			        THEN
			            IF (EXISTS (SELECT * FROM system_item_folder WHERE id = id_parent))
				            THEN
					            UPDATE system_item_folder SET ch_files = array_append (ch_files, it_id), update_date = it_date WHERE id = id_parent;
					            UPDATE system_item_folder SET ch_files = array_remove (ch_files, it_id), update_date = it_date WHERE id = item_prev_row.parent_id;
			            ELSE
				            RAISE EXCEPTION 'New parent folder not exist %',id_parent;
			            END IF;
		        END IF;
		    UPDATE system_item_file SET url= it_url, update_date = it_date, parent_id = id_parent, size_it = it_size WHERE id = it_id;
        END IF;

ELSE
	IF id_parent IS NULL
		THEN
			INSERT INTO system_item_file VALUES (it_id, it_url, it_date, id_parent, it_size);
	ELSE
		IF (EXISTS (SELECT * FROM system_item_folder WHERE id = id_parent))
			THEN
				INSERT INTO system_item_file VALUES (it_id, it_url, it_date, id_parent, it_size);
				UPDATE system_item_folder SET ch_files = array_append (ch_files, it_id), update_date = it_date WHERE id = id_parent;
		ELSE
				RAISE EXCEPTION 'New parent folder not exist %',id_parent;
		END IF;
	END IF;
END IF;
END
$$LANGUAGE plpgSQL;

CREATE OR REPLACE PROCEDURE insert_folder (it_id VARCHAR, it_date TIMESTAMPTZ, id_parent VARCHAR) AS
$$
DECLARE
item_prev_row RECORD;
BEGIN
IF (EXISTS (SELECT * FROM system_item_folder WHERE id = it_id))
	THEN
		SELECT * INTO STRICT item_prev_row FROM system_item_folder WHERE id = it_id;
        IF (it_date > item_prev_row.update_date)
            THEN
                IF (id_parent != item_prev_row.parent_id)
			        THEN
                        IF (EXISTS (SELECT * FROM system_item_folder WHERE id = id_parent))
				            THEN
                                UPDATE system_item_folder SET ch_folders = array_append (ch_folders, it_id), update_date = it_date WHERE id = id_parent;
				                UPDATE system_item_folder SET ch_folders = array_remove (ch_folders, it_id), update_date = it_date WHERE id = item_prev_row.parent_id;
                                UPDATE system_item_folder SET parent_id = id_parent, update_date = it_date WHERE id = it_id;
                        ELSE
				            RAISE EXCEPTION 'New parent folder not exist %',id_parent;
                        END IF;
                END IF;
        END IF;
ELSE
    IF id_parent IS NULL
		THEN
			INSERT INTO system_item_folder VALUES (it_id, it_date, id_parent, NULL, NULL);
	ELSE
		IF (EXISTS (SELECT * FROM system_item_folder WHERE id = id_parent))
			THEN
				UPDATE system_item_folder SET ch_folders = array_append (ch_folders, it_id), update_date = it_date WHERE id = id_parent;
				INSERT INTO system_item_folder VALUES (it_id, it_date, id_parent, NULL, NULL);
		ELSE
				RAISE EXCEPTION 'New parent folder not exist %',id_parent;
		END IF;
	END IF;
END IF;
END
$$LANGUAGE plpgSQL;
