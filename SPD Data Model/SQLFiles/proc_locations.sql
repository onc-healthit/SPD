DELIMITER //
 CREATE PROCEDURE CreateLocations()
   BEGIN
	DECLARE v_finished INTEGER DEFAULT 0;
    DECLARE org_id INTEGER;
    DECLARE address_id INTEGER;
    DECLARE org_name varchar(512) DEFAULT "";
    DECLARE org_cursor CURSOR FOR
		SELECT org.organization_id, org.name, addr.address_id
		FROM vhdir_organization as org, address as addr
		WHERE org.name like '%HOSPITAL%' AND addr.organization_id = org.organization_id
        AND addr.use='work';
    DECLARE CONTINUE HANDLER 
        FOR NOT FOUND SET v_finished = 1;
        
	-- Open cursor to get all organization addresses with HOSPITAL
	OPEN org_cursor;
    
    create_locations: LOOP
		FETCH org_cursor INTO org_id, org_name, address_id;
        IF v_finished = 1 THEN 
			LEAVE create_locations;
		END IF;

        -- Insert row into vhdir_location
        INSERT INTO vhdir_location(status, name, address_id, managing_organization_id)
        VALUES ('active', org_name, address_id, org_id);
    
    END LOOP create_locations;
    
    CLOSE org_cursor;
    
   END//
 DELIMITER ;