DELIMITER //
 CREATE PROCEDURE CreateCAValidations()
   BEGIN
	DECLARE v_finished INTEGER DEFAULT 0;
    DECLARE org_id INTEGER;
    DECLARE prac_id INTEGER;
    DECLARE cc_id INTEGER;
    DECLARE need_cc_id INTEGER;
    DECLARE val_cc_id INTEGER;
    DECLARE fail_cc_id INTEGER;
    DECLARE att_comm_cc_id INTEGER;
    DECLARE attestation_id INTEGER;
    DECLARE primary_id INTEGER;
    DECLARE primary_who_ref_id INTEGER;
    DECLARE att_who_ref_id INTEGER;
    DECLARE val_id INTEGER;
    DECLARE start_date DATETIME;
    DECLARE end_date DATETIME;
    DECLARE counter INTEGER DEFAULT 0;
    DECLARE prac_cursor CURSOR FOR
		SELECT DISTINCT(prac.practitioner_id)
		FROM vhdir_practitioner prac, address addr, name n
		WHERE prac.practitioner_id = addr.practitioner_id
		AND prac.practitioner_id = n.practitioner_id
		AND addr.state = 'CA' AND (n.prefix = 'DR.' OR n.suffix='MD');
    DECLARE CONTINUE HANDLER 
        FOR NOT FOUND SET v_finished = 1;
        
	-- First we insert an organization to be the validation body
    INSERT INTO vhdir_organization(active, name) VALUES (1, 'The Medical Board of California');
    SET org_id = LAST_INSERT_ID();
    
    -- Then we insert the type
    INSERT INTO fhir_codeable_concept(coding_system, coding_code, coding_display, organization_type_id)
		VALUES ('http://terminology.hl7.org/CodeSystem/organization-type', 'govt', 'Government', org_id);
    
    -- Then we insert the address
    INSERT INTO address(type, line1, line2, city, state, postalCode, organization_id)
		VALUES ('both', '2005 Evergreen St.', 'Ste 1200', 'Sacramento', 'CA', '95815', org_id);
    
    -- Then we insert the telecoms
    INSERT INTO telecom(system, value, organization_id)
		VALUES ('phone', '9162632482', org_id);
	INSERT INTO telecom(system, value, organization_id)
		VALUES ('fax', '9162632435', org_id);
    
    -- Then insert a codeable concept for MD that can be used for qualifications later
    INSERT INTO fhir_codeable_concept(coding_system, coding_code, coding_display)
		VALUES ('http://terminology.hl7.org/CodeSystem/v2-0360|2.7', 'MD', 'Doctor of Medicine');
    SET cc_id = LAST_INSERT_ID();
    
    -- insert need codeable concept for use later
    INSERT INTO fhir_codeable_concept(coding_system, coding_code, coding_display)
		VALUES ('http://terminology.hl7.org/CodeSystem/need', 'periodic', 'Periodic');
    SET need_cc_id = LAST_INSERT_ID();
    
    -- insert validation type codeable concept for later
    INSERT INTO fhir_codeable_concept(coding_code, coding_display)
		VALUES ('primary', 'Primary Source');
    SET val_cc_id = LAST_INSERT_ID();
    
    -- insert failure action codeable concept for use later
    INSERT INTO fhir_codeable_concept(coding_system, coding_code, coding_display)
		VALUES ('http://terminology.hl7.org/CodeSystem/failure-action', 'fatal', 'Fatal');
    SET fail_cc_id = LAST_INSERT_ID();
    
    -- insert the resource reference for the primary source who to use later
    INSERT INTO resource_reference(type, identifier) VALUES ('vhdir_organization', org_id);
    SET primary_who_ref_id = LAST_INSERT_ID();
    
    -- insert the attestation communication method
	INSERT INTO fhir_codeable_concept(coding_system, coding_code, coding_display)
		VALUES ('http://terminology.hl7.org/CodeSystem/verificationresult-communication-method',
        'portal', 'Portal');
	SET att_comm_cc_id = LAST_INSERT_ID();
    
	-- Open cursor to get practitioners in CA with MD or DR
	OPEN prac_cursor;
    
    create_validation: LOOP
		FETCH prac_cursor INTO prac_id;
        IF v_finished = 1 THEN 
			LEAVE create_validation;
		END IF;
        
        SET counter = counter + 1;
        
        -- Insert the qualification for each practitioner
        SET start_date = FROM_UNIXTIME(UNIX_TIMESTAMP(now()) - FLOOR(0 + (RAND() * 63072000)));
        SET end_date = DATE_ADD(start_date, INTERVAL 2 YEAR);
        INSERT INTO qualification(issuing_organization_id, code_cc_id, period_start, period_end)
			VALUES (org_id, cc_id, start_date, end_date);
            
		-- insert the resource reference for the attestation who to use later
		INSERT INTO resource_reference(type, identifier) VALUES ('vhdir_practitioner', prac_id);
		SET att_who_ref_id = LAST_INSERT_ID();
            
		-- Insert attestation for use later
		INSERT INTO attestation(who_resource_reference_id, communication_method_cc_id, date)
			VALUES (att_who_ref_id, att_comm_cc_id, '2019-02-25');
		SET attestation_id = LAST_INSERT_ID();
            
		IF (counter % 10 = 0) THEN
			-- Insert Validation
			INSERT INTO vhdir_validation(need_cc_id, target_location, status, status_date, validation_type_cc_id,
				frequency, failure_action_cc_id, attestation_id)
				VALUES (need_cc_id, 'Practitioner.qualification[0]', 'reval-fail', '2019-06-03', val_cc_id,
				'1;wk', fail_cc_id, attestation_id);
		ELSE
			-- Insert Validation
			INSERT INTO vhdir_validation(need_cc_id, target_location, status, status_date, validation_type_cc_id,
				frequency, failure_action_cc_id, attestation_id)
				VALUES (need_cc_id, 'Practitioner.qualification[0]', 'validated', '2019-06-03', val_cc_id,
				'1;wk', fail_cc_id, attestation_id);
		END IF;
        
        SET val_id = LAST_INSERT_ID();
        
        -- Insert validation process codeable concept
        INSERT INTO fhir_codeable_concept(coding_code, coding_display, validation_process_id)
			VALUES ('pull', 'Pull', val_id);
        
        -- Insert the target reference as Practitioner
        INSERT INTO resource_reference(type, identifier, validation_target_id)
			VALUES ('vhdir_practitioner', prac_id, val_id);
            
		-- Insert the primary source
        INSERT INTO primary_source(validation_id, who_resource_reference_id)
			VALUES (val_id, primary_who_ref_id);
		SET primary_id = LAST_INSERT_ID();
            
		-- insert primary source type codeable concept
		INSERT INTO fhir_codeable_concept(coding_system, coding_code, coding_display, primary_source_type_id)
			VALUES ('http://terminology.hl7.org/CodeSystem/primary-source-type', 'issuer', 'Issuing Source',
            primary_id);
		
        -- insert primary source communication codeable concept
		INSERT INTO fhir_codeable_concept(coding_system, coding_code, coding_display,
			primary_source_communication_method_id)
			VALUES ('http://terminology.hl7.org/CodeSystem/verificationresult-communication-method',
			'pull', 'Pull', primary_id);
    
    END LOOP create_validation;
    
    CLOSE prac_cursor;
    
   END//
 DELIMITER ;