DELIMITER //
 CREATE PROCEDURE CreateOrganizationAffiliation()
   BEGIN
	DECLARE v_finished INTEGER DEFAULT 0;
    DECLARE org_id INTEGER;
    DECLARE association_id INTEGER;
    DECLARE org_cursor CURSOR FOR
		SELECT organization_id FROM vhdir_organization WHERE name like '%HOSPITAL%';
    DECLARE CONTINUE HANDLER 
        FOR NOT FOUND SET v_finished = 1;
        
	-- Open cursor before inserting association since it has HOSPITAL in the name
	OPEN org_cursor;
    
	-- Insert the association and store the ID
    INSERT INTO vhdir_organization(active, name) VALUES (1,'American Hospital Association');
    SET association_id = LAST_INSERT_ID();
    
    create_affiliation: LOOP
		FETCH org_cursor INTO org_id;
        IF v_finished = 1 THEN 
			LEAVE create_affiliation;
		END IF;
        
        -- Insert row into organization_affiliation
        INSERT INTO vhdir_organization_affiliation(active, organization_id,
			participating_organization_id) VALUES (1, association_id, org_id);
    
    END LOOP create_affiliation;
    
    CLOSE org_cursor;
    
   END//
 DELIMITER ;