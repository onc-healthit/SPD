DELIMITER //
 CREATE PROCEDURE CreateRestrictions()
   BEGIN
   
	DECLARE v_finished INTEGER DEFAULT 0;
    DECLARE org_id INTEGER;
    DECLARE restriction_id INTEGER;
    DECLARE scope_cc_id INTEGER;
	DECLARE role_cc_id INTEGER;
	DECLARE org_reference_id INTEGER;
	DECLARE actor_org_reference_id INTEGER;
	DECLARE actor_id INTEGER;
	DECLARE provision_id INTEGER;

	-- Find some organizations that we want to add restrictions to based on their NPI value
    DECLARE org_cursor_npi_list CURSOR FOR
		SELECT ident.organization_id, org.organization_id, ident.value 
		FROM identifier ident, vhdir_organization org 
		WHERE ident.organization_id = org.organization_id and ident.value in ('1649425497','1356596100','1336394170','1265687099','1083869671');

	-- Find some organizations that we want to add restrictions to based on their name containing the word 'Holistic'
    DECLARE org_cursor_holistic CURSOR FOR
		SELECT org.organization_id 
		FROM vhdir_organization org 
		WHERE org.name like '%Holistic%';
		
	DECLARE CONTINUE HANDLER 
        FOR NOT FOUND SET v_finished = 1;

	-- Create a 'scope' codeable concept that we'll use for all restrictions
	INSERT INTO fhir_codeable_concept(coding_code, coding_display) VALUES('private','Private');
	SET scope_cc_id = LAST_INSERT_ID();

	-- Create a 'role' codeable concept that we'll use for all actors in any provision we create
	INSERT INTO fhir_codeable_concept(coding_code, coding_display) VALUES('IRCP','Information recipient');
	SET role_cc_id = LAST_INSERT_ID();
	  
	-- Open cursor to get a sample of organizations to use to assign restrictions to
	OPEN org_cursor_holistic;
    	
    create_restriction: LOOP
		FETCH org_cursor_holistic INTO org_id;
        IF v_finished = 1 THEN 
			LEAVE create_restriction;
		END IF;
        
		-- First, create an entry in the resource_reference table for this organization that we will later associate with an actor entry
		INSERT INTO resource_reference(type,identifier,display)
		       VALUES ('vhdir_organization',org_id,'Defined User or Group');
	    SET actor_org_reference_id = LAST_INSERT_ID();
		
		-- Then, before we create a restriction, we need to create a provision.
		INSERT INTO provision(type) VALUES('permit');
		SET provision_id = LAST_INSERT_ID();

		-- However, Provisions can have a number of actors.  So add one with the role codeable concept we've already created, linked back to the provision.
		INSERT INTO actor(role_cc_id, reference_resource_id, provision_id) 
		       VALUES(role_cc_id, actor_org_reference_id, provision_id);
	    SET actor_id = LAST_INSERT_ID();
		
		-- Now we can insert a restriction with the provision (and its actor) we just created
        INSERT INTO vhdir_restriction(status, scope_cc_id, provision_id)
			VALUES('active', scope_cc_id, provision_id);
		SET restriction_id = LAST_INSERT_ID();
        
		-- Finally create a resource reference entry to this restriction, linked back to the organization
		INSERT INTO resource_reference(type,identifier,display,organization_restriction_id)
		       VALUES ('vhdir_restriction',restriction_id,'Organization Restriction',org_id);
			   
    END LOOP create_restriction;
    
    CLOSE org_cursor_holistic;
    
   END//
 DELIMITER ;