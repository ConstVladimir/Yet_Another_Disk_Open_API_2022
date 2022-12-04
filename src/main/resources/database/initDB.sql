CREATE TABLE IF NOT EXISTS SYSTEM_ITEM_FOLDER (
    id VARCHAR(36) PRIMARY KEY,
    update_date timestamp with time zone,
    parent_id VARCHAR(36) REFERENCES SYSTEM_ITEM_FOLDER (id) ON DELETE CASCADE,
    ch_files VARCHAR(36) ARRAY,
    ch_folders VARCHAR(36) ARRAY
);

CREATE TABLE IF NOT EXISTS SYSTEM_ITEM_FILE (
    id VARCHAR(36) PRIMARY KEY,
    url VARCHAR(255),
    update_date timestamp with time zone,
    parent_id VARCHAR(36) REFERENCES SYSTEM_ITEM_FOLDER (id) ON DELETE CASCADE,
    size_it BIGINT
);

CREATE OR REPLACE PROCEDURE insert_file (it_id VARCHAR, it_url VARCHAR, it_date TIMESTAMPTZ, id_parent VARCHAR, it_size BIGINT) AS
$$
DECLARE
item_prev_row RECORD;
BEGIN
IF (EXISTS (SELECT * FROM system_item_folder WHERE id = it_id))
THEN
	RAISE EXCEPTION '% is exist folder id',it_id;
END IF;
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
				            RAISE EXCEPTION 'Parent folder % not exist',id_parent;
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
				RAISE EXCEPTION 'Parent folder % not exist',id_parent;
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
IF (EXISTS (SELECT * FROM system_item_file WHERE id = it_id))
THEN
	RAISE EXCEPTION '% is exist file id',it_id;
END IF;
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
				            RAISE EXCEPTION 'Parent folder % not exist',id_parent;
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
				RAISE EXCEPTION 'Parent folder % not exist',id_parent;
		END IF;
	END IF;
END IF;
END
$$LANGUAGE plpgSQL;
------
CREATE OR REPLACE PROCEDURE delete_file (it_id VARCHAR, it_date TIMESTAMPTZ) AS
$$
DECLARE
item_prev_row RECORD;
BEGIN
	SELECT * INTO STRICT item_prev_row FROM system_item_file WHERE id = it_id;
	UPDATE system_item_folder SET ch_files = array_remove (ch_files, it_id), update_date = it_date WHERE id = id_parent;
	DELETE FROM system_item_file WHERE id = it_id;
	EXCEPTION
        WHEN NO_DATA_FOUND THEN
			RAISE EXCEPTION '% not found for deleting',it_id;
END
$$LANGUAGE plpgSQL;

/*CREATE OR REPLACE PROCEDURE delete_folder (it_id VARCHAR, it_date TIMESTAMPTZ) AS
$$
DECLARE
item_prev_row RECORD;
BEGIN
	SELECT * INTO STRICT item_prev_row FROM system_item_folder WHERE id = it_id;
	UPDATE system_item_folder SET ch_folders = array_remove (ch_folders, it_id), update_date = it_date WHERE id = item_prev_row.parent_id;
	DELETE FROM system_item_folder WHERE id=it_id;
	EXCEPTION
        WHEN NO_DATA_FOUND THEN
			RAISE EXCEPTION '% not found for deleting',it_id;
END
$$LANGUAGE plpgSQL;*/

CREATE OR REPLACE PROCEDURE delete_item (it_id VARCHAR, it_date TIMESTAMPTZ) AS
$$
DECLARE
item_prev_row RECORD;
BEGIN
	SELECT * INTO STRICT item_prev_row FROM system_item_folder WHERE id = it_id;
	UPDATE system_item_folder SET ch_folders = array_remove (ch_folders, it_id), update_date = it_date WHERE id = item_prev_row.parent_id;
	DELETE FROM system_item_folder WHERE id=it_id;
	EXCEPTION
        WHEN NO_DATA_FOUND THEN
			CALL delete_file (it_id, it_date);
END
$$LANGUAGE plpgSQL;

/*CREATE OR REPLACE FUNCTION get_folder (first_p VARCHAR) RETURNS TABLE (item_t VARCHAR, id VARCHAR, url VARCHAR, update_date TIMESTAMPTZ, parent_id VARCHAR, size_it BIGINT) AS
$$
WITH RECURSIVE parent_tree AS (
	SELECT id, update_date, parent_id FROM system_item_folder WHERE id = first_p
	UNION ALL
		SELECT t.id, t.update_date, t.parent_id FROM system_item_folder t, parent_tree pt WHERE t.parent_id = pt.id
),
child_files AS (
	SELECT t.id, t.url, t.update_date, t.parent_id, t.size_it FROM system_item_file t, parent_tree pt WHERE t.parent_id = pt.id
)
SELECT 'FOLDER', id, NULL, update_date, parent_id, NULL FROM parent_tree
UNION SELECT 'FILE', id, url, update_date, parent_id, size_it FROM child_files;
$$ LANGUAGE SQL;*/

/*CREATE OR REPLACE FUNCTION get_item (it_id VARCHAR) RETURNS TABLE (item_t VARCHAR, id VARCHAR, url VARCHAR, update_date TIMESTAMPTZ, parent_id VARCHAR, size_it BIGINT) AS
$$
BEGIN
IF (EXISTS (SELECT * FROM system_item_folder t WHERE t.id = it_id))
	THEN
		RETURN QUERY SELECT * FROM get_folder (it_id);
ELSE
		RETURN QUERY SELECT 'FILE', t.id, t.url, t.update_date, t.parent_id, t.size_it  FROM system_item_file t WHERE t.id = it_id;
END IF;
END;
$$ LANGUAGE plpgSQL;*/

CREATE OR REPLACE FUNCTION get_item (it_id VARCHAR) RETURNS TABLE (item_t VARCHAR, id VARCHAR, url VARCHAR, update_date TIMESTAMPTZ, parent_id VARCHAR, size_it BIGINT) AS
$$
WITH RECURSIVE parent_tree AS (
	SELECT id, update_date, parent_id FROM system_item_folder WHERE id = it_id
	UNION ALL
		SELECT t.id, t.update_date, t.parent_id FROM system_item_folder t, parent_tree pt WHERE t.parent_id = pt.id
),
child_files AS (
	SELECT t.id, t.url, t.update_date, t.parent_id, t.size_it FROM system_item_file t, parent_tree pt WHERE t.parent_id = pt.id
)
SELECT 'FOLDER', id, NULL, update_date, parent_id, NULL FROM parent_tree
UNION SELECT 'FILE', id, url, update_date, parent_id, size_it FROM child_files
UNION SELECT 'FILE', id, url, update_date, parent_id, size_it FROM system_item_file WHERE id = it_id;
$$ LANGUAGE SQL;

/* SYSTEM_ITEM_FOLDER (							SYSTEM_ITEM_FILE (
    id VARCHAR(36) PRIMARY KEY,						id VARCHAR(36) PRIMARY KEY,
    update_date timestamp with time zone,			url VARCHAR(255),
    parent_id VARCHAR(36),							update_date timestamp with time zone,
    ch_files VARCHAR(36) ARRAY,						parent_id VARCHAR(36),
    ch_folders VARCHAR(36) ARRAY					size_it BIGINT
);												);*/







