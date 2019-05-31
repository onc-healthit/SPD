DELIMITER //
 CREATE PROCEDURE CreateCareteams()
   BEGIN
	DECLARE v_finished INTEGER DEFAULT 0;
    DECLARE org_id INTEGER;
    DECLARE careteam_id INTEGER;
    DECLARE participant_id INTEGER;
    DECLARE org_cursor CURSOR FOR
		SELECT organization_id FROM vhdir_organization
        WHERE name like '%HOSPITAL%' LIMIT 5;
    DECLARE CONTINUE HANDLER 
        FOR NOT FOUND SET v_finished = 1;
        
	-- Open cursor to get a sample of organizations to use for care teams
	OPEN org_cursor;
    
    create_careteam: LOOP
		FETCH org_cursor INTO org_id;
        IF v_finished = 1 THEN 
			LEAVE create_careteam;
		END IF;
        
        -- Insert first careteam linked to the top level organization
        INSERT INTO vhdir_careteam(status, name, period_start, managing_organization_id)
			VALUES('proposed', 'Acute Physical Rehabilitation', '2019-01-01', org_id);
		SET careteam_id = LAST_INSERT_ID();
        
        -- Insert alias
        INSERT INTO care_team_alias(name, careteam_id) VALUES('Acute Rehab', careteam_id);
        
        -- Insert careteam category
        INSERT INTO fhir_codeable_concept(coding_code, coding_display, careteam_category_id)
			VALUES('LA27978-8', 'Condition-focused care team', careteam_id);

        -- Insert first participant
        INSERT INTO participant(careteam_id) VALUES (careteam_id);
        SET participant_id = LAST_INSERT_ID();
        
        INSERT INTO fhir_codeable_concept(coding_code, coding_display, careteam_participant_role_id)
			VALUES('309360001', 'Rehabilitation physician', participant_id);
            
		-- Insert second participant
        INSERT INTO participant(careteam_id) VALUES (careteam_id);
        SET participant_id = LAST_INSERT_ID();
        
        INSERT INTO fhir_codeable_concept(coding_code, coding_display, careteam_participant_role_id)
			VALUES('309366007', 'Rehabilitation psychiatrist', participant_id);
            
		-- Insert third participant
        INSERT INTO participant(careteam_id) VALUES (careteam_id);
        SET participant_id = LAST_INSERT_ID();
        
		INSERT INTO fhir_codeable_concept(coding_code, coding_display, careteam_participant_role_id)
			VALUES('36682004', 'PT - Physiotherapist', participant_id);

		-- Insert fourth participant
        INSERT INTO participant(careteam_id) VALUES (careteam_id);
        SET participant_id = LAST_INSERT_ID();
        
		INSERT INTO fhir_codeable_concept(coding_code, coding_display, careteam_participant_role_id)
			VALUES('6816002', 'Specialized Nurse', participant_id);

		-- Insert fifth participant
        INSERT INTO participant(careteam_id) VALUES (careteam_id);
        SET participant_id = LAST_INSERT_ID();
        
		INSERT INTO fhir_codeable_concept(coding_code, coding_display, careteam_participant_role_id)
			VALUES('80546007', 'OT - Occupational therapist', participant_id);

		-- Insert sixth participant
        INSERT INTO participant(careteam_id) VALUES (careteam_id);
        SET participant_id = LAST_INSERT_ID();
        
		INSERT INTO fhir_codeable_concept(coding_code, coding_display, careteam_participant_role_id)
			VALUES('224598009', 'Trained social worker counselor', participant_id);

		-- Insert seventh participant
        INSERT INTO participant(careteam_id) VALUES (careteam_id);
        SET participant_id = LAST_INSERT_ID();
        
		INSERT INTO fhir_codeable_concept(coding_code, coding_display, careteam_participant_role_id)
			VALUES('56397003', 'Neurologist', participant_id);
            
		-- Insert second careteam linked to the top level organization
        INSERT INTO vhdir_careteam(status, name, period_start, managing_organization_id)
			VALUES('proposed', 'Physical Rehabilitation', '2019-01-01', org_id);
		SET careteam_id = LAST_INSERT_ID();
        
        -- Insert alias
        INSERT INTO care_team_alias(name, careteam_id) VALUES('Physical Rehab', careteam_id);
        
        -- Insert careteam category
        INSERT INTO fhir_codeable_concept(coding_code, coding_display, careteam_category_id)
			VALUES('LA27978-8', 'Condition-focused care team', careteam_id);

        -- Insert first participant
        INSERT INTO participant(careteam_id) VALUES (careteam_id);
        SET participant_id = LAST_INSERT_ID();
        
        INSERT INTO fhir_codeable_concept(coding_code, coding_display, careteam_participant_role_id)
			VALUES('309360001', 'Rehabilitation physician', participant_id);
            
		-- Insert second participant
        INSERT INTO participant(careteam_id) VALUES (careteam_id);
        SET participant_id = LAST_INSERT_ID();
        
        INSERT INTO fhir_codeable_concept(coding_code, coding_display, careteam_participant_role_id)
			VALUES('309366007', 'Rehabilitation psychiatrist', participant_id);
            
		-- Insert third participant
        INSERT INTO participant(careteam_id) VALUES (careteam_id);
        SET participant_id = LAST_INSERT_ID();
        
		INSERT INTO fhir_codeable_concept(coding_code, coding_display, careteam_participant_role_id)
			VALUES('36682004', 'PT - Physiotherapist', participant_id);
            
		-- Insert third careteam linked to the top level organization
        INSERT INTO vhdir_careteam(status, name, period_start, managing_organization_id)
			VALUES('proposed', 'Stroke Rehabilitation', '2019-01-01', org_id);
		SET careteam_id = LAST_INSERT_ID();
        
        -- Insert alias
        INSERT INTO care_team_alias(name, careteam_id) VALUES('Stroke Rehab', careteam_id);
        
        -- Insert careteam category
        INSERT INTO fhir_codeable_concept(coding_code, coding_display, careteam_category_id)
			VALUES('LA27975-4', 'Event-focused care team', careteam_id);

        -- Insert first participant
        INSERT INTO participant(careteam_id) VALUES (careteam_id);
        SET participant_id = LAST_INSERT_ID();
        
        INSERT INTO fhir_codeable_concept(coding_code, coding_display, careteam_participant_role_id)
			VALUES('309360001', 'Rehabilitation physician', participant_id);
            
		-- Insert second participant
        INSERT INTO participant(careteam_id) VALUES (careteam_id);
        SET participant_id = LAST_INSERT_ID();
        
        INSERT INTO fhir_codeable_concept(coding_code, coding_display, careteam_participant_role_id)
			VALUES('309366007', 'Rehabilitation psychiatrist', participant_id);
            
		-- Insert third participant
        INSERT INTO participant(careteam_id) VALUES (careteam_id);
        SET participant_id = LAST_INSERT_ID();
        
		INSERT INTO fhir_codeable_concept(coding_code, coding_display, careteam_participant_role_id)
			VALUES('36682004', 'PT - Physiotherapist', participant_id);

		-- Insert fourth participant
        INSERT INTO participant(careteam_id) VALUES (careteam_id);
        SET participant_id = LAST_INSERT_ID();
        
		INSERT INTO fhir_codeable_concept(coding_code, coding_display, careteam_participant_role_id)
			VALUES('6816002', 'Specialized Nurse', participant_id);

		-- Insert fifth participant
        INSERT INTO participant(careteam_id) VALUES (careteam_id);
        SET participant_id = LAST_INSERT_ID();
        
		INSERT INTO fhir_codeable_concept(coding_code, coding_display, careteam_participant_role_id)
			VALUES('80546007', 'OT - Occupational therapist', participant_id);

		-- Insert sixth participant
        INSERT INTO participant(careteam_id) VALUES (careteam_id);
        SET participant_id = LAST_INSERT_ID();
        
		INSERT INTO fhir_codeable_concept(coding_code, coding_display, careteam_participant_role_id)
			VALUES('224598009', 'Trained social worker counselor', participant_id);
    
    END LOOP create_careteam;
    
    CLOSE org_cursor;
    
   END//
 DELIMITER ;