package com.esacinc.spd.DataGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.hl7.fhir.r4.model.Address.AddressUse;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.hl7.fhir.r4.model.Identifier.IdentifierUse;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.TimeType;

import com.esacinc.spd.model.VhDirAddress;
import com.esacinc.spd.model.VhDirAlias;
import com.esacinc.spd.model.VhDirContactPoint;
import com.esacinc.spd.model.VhDirContactPointAvailableTime;
import com.esacinc.spd.model.VhDirDigitalCertificate;
import com.esacinc.spd.model.VhDirGeoLocation;
import com.esacinc.spd.model.VhDirIdentifier;
import com.esacinc.spd.model.VhDirIdentifier.IdentifierStatus;
import com.esacinc.spd.model.VhDirPractitioner;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BulkPractitionerBuilder {
	
	private String[] certs = new String[3];
	
	public BulkPractitionerBuilder() {
		// 5 sample certificates to use for samples
		certs[0] = "-----BEGIN CERTIFICATE-----" +
			"MIIE2DCCAsACCQDMxyOS8py9rTANBgkqhkiG9w0BAQsFADAuMQswCQYDVQQGEwJV" +
			"UzEOMAwGA1UECAwFVGV4YXMxDzANBgNVBAcMBkRhbGxhczAeFw0xOTA0MTUxOTIy" +
			"MjFaFw0yMDA0MTQxOTIyMjFaMC4xCzAJBgNVBAYTAlVTMQ4wDAYDVQQIDAVUZXhh" +
			"czEPMA0GA1UEBwwGRGFsbGFzMIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKC" +
			"AgEAzUVfF7+Pif89oqJsIB9n07iRIAD0Fz5fZRsxucwm0DXk4VBXDXOso4JAM960" +
			"gR75ExABQuIIWmGNwdp74aMdMLD3kvvd0DVBvjb9i9fsLDpP8N7RFMMNyKCKFdmo" +
			"PU+qEO+nM/LHp4hiYzeMoOQevtRBVo6URfQnoCek1j1+HiYaxeV9LrErgsI0i2P7" +
			"4sCprwE8iHqbauKNFJahBite90kVL4cRc0r9HZWzX0j10Xzw7kSD1dEHG7NxDgqy" +
			"usr3aVFadTVIl4wl2KHeZdGOQiF0kfzb4qdIhVyNABYYP6Rfc+aGZK4EysQyXW7R" +
			"2avgTxHe1EZlUqNkmDeuugQxljsjWb5yevfyNTCrSy1eYpFlzzetrEwnLXp74IMA" +
			"yvFaR6yQNKuQXE47XJVESDpoQ+PqowQnnFqTtCp+NzFbp2nwsZD6wJMsUviM0u9t" +
			"haytrcTniy86mm5PL/0mJVasgRF5U5r3PzIvMDlleiqyQvak6+z4txEA29hlsbBg" +
			"mBArQ+3XI9bXnP8qbMs1AuVbMvj+zWpBrYj3gRhpMOubKT177wTy7trCT8/51bvL" +
			"mybDsoQsJxX1xCzMK6518qQY+4Pu8kEzdpSJVMAZl/9Qzq6dYJy2iAgcMBf1zo1H" +
			"RGdhQJ6iKfaSvBDM1dqPhoFUlgX2Ccxq+hFkBI5+6RLKZn0CAwEAATANBgkqhkiG" +
			"9w0BAQsFAAOCAgEAADxVF4DC5Lr1PpkaMRILRkydW3AEB2vSkNkRbvYtVAMIli2B" +
			"q1RacCm5e7IfQCF4HGJzTjwGHCAyxl2+suEyQ1jjT9biO3kWHdHmTKEoxeDsaSUB" +
			"w+jziee9PhVUayOqRh+zn8WDBPDwm8EoE9lQfK/TAVlXxbom0YeGVJKMN6xr/EBI" +
			"dY1/gpbJ9/QQIWe+JeG9MxDHP8WEmVqv9RwBI4CtLmhxqXxyKG8Lmps3rdgiQ9hL" +
			"DHqGfu1F4St3sjnjTUZdB67G+HN3TKoN2InBcjGxIsHW6kyMamo1kFc5hgOG9CnU" +
			"4fqn4HhXoWcmYDq3aGet4+SAkQ03D3/RyeviL2f9ptwX4Bm7uZcdI/ZkqQdLIYZX" +
			"pSOUQsidq6NWcZffKfBNgoiPWj1TynNdKfM09sxfTk94OB1k9UaP7KlIR8VuZsoS" +
			"2vC0HYf8x+16Wll3vnkumYVDoOpwOxnSv1q/TcksPmwJftEonZdqWA1HiuytXqcn" +
			"Il5jeqcLchyn9MWlZzl4BrqOZVBMrFjzOmSTPHJDdKngrP7ZjQKiVUHjFeMpfqmN" +
			"h4qfhg9D3bnIrVtMf0mvHiy/gZaVwZ0xTPmCRgK3k71ZAhksxvPA/e4cEepoV+n8" +
			"DQrcPNSd5ml8tq1SnB/bx/rXm6IF8yvuHlEHqV6wCrC33DLsHMv1VT0nzbc=" +
			"-----END CERTIFICATE-----";
		certs[1] = "-----BEGIN CERTIFICATE-----" +
			"MIIE2DCCAsACCQDjhQnQs4wMXDANBgkqhkiG9w0BAQsFADAuMQswCQYDVQQGEwJV" +
			"UzEOMAwGA1UECAwFVGV4YXMxDzANBgNVBAcMBlNhY2hzZTAeFw0xOTA0MTUxOTM1" +
			"MzRaFw0yMDA0MTQxOTM1MzRaMC4xCzAJBgNVBAYTAlVTMQ4wDAYDVQQIDAVUZXhh" +
			"czEPMA0GA1UEBwwGU2FjaHNlMIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKC" +
			"AgEA4e0UOJlkQnGVtndeLFIOjQGQvOrOZl9Myt4B8KfmNHq81mydtptWvioIcPOq" +
			"jHlWVtmylV7kFS5EiTkvxmjT7uGJQdfaCunX9CooQGcF0U6XNSvyLRcStMN+Cn6d" +
			"sC3QW2rUuIZV6qQrP/hdW5f5IqZA6UaL8rPV2xKhl33o+GOX/FuRDXW/V9ytqtc1" +
			"Sm3VSV/SWOhGzHmtY23Cg7NlJ7+j9FmQbaFKfXmIgY5Qzzx61NcdGCr9i3IFpHWY" +
			"4u7n1lM1m/Vufc165JDBUtHZ7DRN4RHbTtxmX2SNrCjDgD/Ww3UBCdVDkcxt9FmG" +
			"qxOCi11C1pJGjIOX+znafmFjpmzUIkGma5g27a97ZcyVZ+hVQBYfce2T6nY1I/yj" +
			"lp/gkVHtQVmNaYjSVb8r9zeyIRAgOGaKt5d4iefjSPQ6BLRfrPveFQxscxLjOEk7" +
			"+yTq2XJFDycP5wMEOixYobAPfhFLb0PxAyZ34VMw5yxHxFS4FE4aDh1ASG+3D4ay" +
			"CG/eJ6qc5lNa0F5GOrI68BBTob6jTmM7KFp2cgYrGMy+QGLyV68dKfxx/4KWi3Kx" +
			"w/PNeOgTyneJWmxqAARoHQRRC9LaFz1Aj402Y8FKcqSlrTxJ7RJhmllGP1S8R78N" +
			"zY0X0zF8xubL4tiIgZ5XPLqp217jivRuHoBLcv8eEzD/hAMCAwEAATANBgkqhkiG" +
			"9w0BAQsFAAOCAgEAgmxQ1amNXH6LpM2D6Edh2CgcL+tRrtS0GBXnnDcwZyoPOTIe" +
			"tN4bRIyYZVYf1fd3JlgPHbCleUuxAoIzB1bK5968YYQpVIf7zmMvPcV+zFZQJP3c" +
			"+KSSksuQiHm24O8/GYEmUI9/oex1YcElog9ef+m17FgUYfD0TebwUA0zrLgbMqFh" +
			"8fkTesiyAdT+TOa+2W84LddwR7xC3SAxrDeiUaNSKK+Rqd/NONmLwJCDJjZEeBE+" +
			"MkW5s040YXru9cZFVZ/U9V/s+d+3v3YfkzARGdU/fAjoBq0iAqcmdz0FvsUwBTno" +
			"gn+ZrybJ+TCnYxAbNjgnW23UyQ+4t3VUr0QaA47BUfD5LGknPjsPHB9aA+1sI3t+" +
			"1gHuETA7OcJPh1wd6BYOPoV2VXcgA7AVVcMdviE/A2gakWhZzcF8Ep3WbSCVSls6" +
			"fsh4lbRo4wczc9hUaryFo0eBVaw44V7qtwIJw2IUcWA7U6w8q62gL+tOJhp2AU5K" +
			"fKS5yP5HWBhyQV2IHtfoO1YMHXkhhDaMI3giMt12HyV+U+aJ0KODvcHDE8Qp6BR2" +
			"AbbkMirbBeDCjdC30NF6lD7hPJAfpzc5C1vE3IS98BnqjaxNJSthXFgZWggrtyYi" +
			"5hn6ExT1XblkKvgox7zgq9mG430V24CT5uVzqJSc2S+U7R0ntCYuydgaP58=" +
			"-----END CERTIFICATE-----";
		certs[2] = "-----BEGIN CERTIFICATE-----" +
			"MIIE5DCCAswCCQCHIWZ/N40MkzANBgkqhkiG9w0BAQsFADA0MQswCQYDVQQGEwJV" +
			"UzERMA8GA1UECAwIQ29sb3JhZG8xEjAQBgNVBAcMCUV2ZXJncmVlbjAeFw0xOTA0" +
			"MTUxOTM5MzdaFw0yMDA0MTQxOTM5MzdaMDQxCzAJBgNVBAYTAlVTMREwDwYDVQQI" +
			"DAhDb2xvcmFkbzESMBAGA1UEBwwJRXZlcmdyZWVuMIICIjANBgkqhkiG9w0BAQEF" +
			"AAOCAg8AMIICCgKCAgEAq30Nk0+N4bUJ8J7HVpWXqG9Dov1Sj1Um+t5uYCMEZ5XZ" +
			"L3+sl9TeGhTwtUQTn5HgtSsQWdvMDffwLfsoOukZNNYsBRQ+10pNhWTaOSN5xibA" +
			"9p5hEeyS/9n/xlD+AumLu3aSeQYTILEXSGbuidh9iYiIPgRI5Eud1vwypDRo3GA3" +
			"ZC2uG1IAQUjoi3QI5DINt2IO3rH1fvS+6MJGL9jMuXbLaho0Sqztg+gGHP2uFqon" +
			"stzJUzr/IWm+Z1i+jGmxdGG19vlMS340UsPxYV5xuo31NTC6VtsjoSflIErp4LmQ" +
			"rg4X93c95rp2XuylInaRhXIhaXJjsd4gG+ukAgXMilbeSiSpW/l8uVSWNLuYNErm" +
			"zeNabirn1IaIqWYx0B+J7Nsl5u6mZD/Di/Feu0rWr9pfaIHXQWnBDFR0IBrQhEj8" +
			"9+gwncHD9dOmuhJDLjxFjNAIUR2Bc2Ph+jlQOasaPHMDg3mGtxMu12z1++dX8BkY" +
			"eQFe6tUW8g78VA4c5fqbVTmkB7/VK1bpSNYT2gkaITwqIQwl3iCHFG8tbgcU8lN/" +
			"E0V3+yiIMExETESPhxBX0cCoYxRje1vSrxyznj7EQ7iJ4026ci0doopwsJmQAkzr" +
			"rYjmhowZS3fpzjr1kz8Ghrdre7ro17NUJYWu3tYjZkBkY9Z1TxSWLw0UQjguHp8C" +
			"AwEAATANBgkqhkiG9w0BAQsFAAOCAgEAKN5U+Rm1eL2b7idzn89gvwQLJqDqRsQo" +
			"YJw2D+kFwhEjw5IFwd/67WaI2HOMq5Mqu5YT9bkRGkA24sh1IkH50HvJ4idZgbB9" +
			"MbcpiMvrP3yvWTvVwHvzdLkOqn0M0j5RL0/pPuCsTFZ93SFn1PKcnJeyOTRQrb1R" +
			"DsHS1di/AclQnnv65ed7qqNdKGFp5tVAQiHBK8sT3oIufZMTWCD47FpAVxw0jCYD" +
			"lUrwWZxqqSOx/No3R7BgTdfQLaU107UeoGe6cgiwZ1L1+oJPKuHsER+ydotrbew9" +
			"DrvlL0c70/vP5YFqlZxLyoguMyOypliqhqCBR+D/jRuexF3KfY0bErxNDUKicrwU" +
			"m+S7YQ0Z9v29IglrC6zkKkVGK+z6NFq1ND7opt7vxrDc97QhFMRELQpx+vvJYuac" +
			"Gh2oc2aXC/e70IztUc+eukx+trK3deqlzg11anJj0Yehu/I6zOK5s2JSSNfcO00O" +
			"T0vtWAQGxFsnG1FiRaRP9VYwkh+LdYns5hOLJsUKhMmVNbHVvajAxU5YAGghdxi2" +
			"wSMIwlHK1MbsKwPzXOtonta0Bn0cAkSyfmlpXrZs1PkrxwyyNPYisV0zzlvtDQV0" +
			"QGIPs0N9k18ywBu2onwnGA7nvg8a/zIeE7OVgC/iEVDDN3kWZVVTZeHQxKvz0ii+" +
			"e1smllz52UE=" +
			"-----END CERTIFICATE-----";
	}
	
	/**
	 * uses the connection provided to get all practitioner and then builds a list
	 * of Practitioner objects to return
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	public List<VhDirPractitioner> getPractitioners(Connection connection) throws SQLException, ParseException {
		List<VhDirPractitioner> practitioners = new ArrayList<VhDirPractitioner>();
		
		int certCount = 0;
		String sql = "SELECT * FROM vhdir_practitioner LIMIT 3";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
		while (resultSet.next()) {
			VhDirPractitioner prac = new VhDirPractitioner();
		
			// set the id
			int pracId = resultSet.getInt("practitioner_id");
			prac.setId(resultSet.getString("practitioner_id"));
			 					
			
			// Add a digital certificate to the first 3 practitioners
			if (certCount < 3) {
				VhDirDigitalCertificate cert = new VhDirDigitalCertificate();
				Coding certType = new Coding();
				certType.setCode("role");
				certType.setSystem("http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-digitalcertificate");
				cert.setType(certType);
				Coding certUse = new Coding();
				certUse.setCode("auth");
				certUse.setSystem("http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-digitalcertificate");
				cert.setUse(certUse);
				Coding certStd = new Coding();
				certStd.setCode("x.509v3");
				certStd.setSystem("http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-digitalcertificate");
				cert.setCertificateStandard(certStd);
				cert.setCertificate(certs[certCount++]);
				Date expire = new Date();
				Calendar cal = Calendar.getInstance();
				cal.setTime(expire);
				cal.add(Calendar.YEAR, 1);
				cert.setExpirationDate(cal.getTime());
				CodeableConcept certTrust = new CodeableConcept();
				Coding trustCode = new Coding();
				trustCode.setCode("other");
				trustCode.setSystem("http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-digitalcertificate");
				certTrust.addCoding(trustCode);
				cert.setTrustFramework(certTrust);
				prac.addDigitalcertficate(cert);
			}
						
			// Handle the identifiers
			handleIdentifiers(connection, prac, pracId);
			
			// TODO Hard coded active for now
			prac.setActive(true);
			
            // Handle names
         	handleNames(connection, prac, pracId);
					
			
            // Handle the telecoms
         	handleTelecoms(connection, prac, pracId);
         	
			// Handle the addresses
         	handleAddresses(connection, prac, pracId);
         	
            // Handle the restrictions
         	handleRestrictions(connection, prac, pracId);
         	
         	// Handle the communications
         	handleCommunications(connection, prac, pracId);
			
			practitioners.add(prac);
		}
		
		return practitioners;
	}
	
	/**
	 * Handles all the elements of the identifiers for Practitioners
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleIdentifiers(Connection connection, VhDirPractitioner prac, int pracId) throws SQLException {
		String idSql = "SELECT * from identifier where practitioner_id = ?";
		PreparedStatement idStatement = connection.prepareStatement(idSql);
		idStatement.setInt(1, pracId);
		ResultSet idResultset = idStatement.executeQuery();
		while(idResultset.next()) {
			VhDirIdentifier identifier = new VhDirIdentifier();
			
			// Set id
			identifier.setId(idResultset.getString("identifier_id"));
			
			// Handle identifier status
			String status = idResultset.getString("identifier_status_value_code");
			if ("active".equals(status))
				identifier.setStatus(IdentifierStatus.ACTIVE);
		    if ("inactive".equals(status))
		    	identifier.setStatus(IdentifierStatus.INACTIVE);
		    if ("issuedinerror".equals(status))
		    	identifier.setStatus(IdentifierStatus.ISSUEDINERROR);
		    if ("revoked".equals(status))
		    	identifier.setStatus(IdentifierStatus.REVOKED);
		    if ("pending".equals(status))
		    	identifier.setStatus(IdentifierStatus.PENDING);
		    if ("unknown".equals(status))
		    	identifier.setStatus(IdentifierStatus.UNKNOWN);
		    
		    // Handle use
		    String use = idResultset.getString("use");
		    if ("usual".equals(use))
		        identifier.setUse(IdentifierUse.USUAL);
		    if ("official".equals(use))
		    	identifier.setUse(IdentifierUse.OFFICIAL);
		    if ("temp".equals(use))
		    	identifier.setUse(IdentifierUse.TEMP);
		    if ("secondary".equals(use))
		    	identifier.setUse(IdentifierUse.SECONDARY);
		    if ("old".equals(use))
		    	identifier.setUse(IdentifierUse.OLD);
		    
		    // Handle system
		    String system = idResultset.getString("system");
		    identifier.setSystem(system);
		    
		    // Handle value
		    String value = idResultset.getString("value");
		    identifier.setValue(value);
			
			prac.addIdentifier(identifier);
		}
	}
	
	/**
	 * Handles the addresses for the passed in practitioner ID
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleAddresses(Connection connection, VhDirPractitioner prac, int pracId) throws SQLException {
		String addrSql = "SELECT * from address where practitioner_id = ?";
		PreparedStatement addrStatement = connection.prepareStatement(addrSql);
		addrStatement.setInt(1, pracId);
		ResultSet addrResultset = addrStatement.executeQuery();
		while(addrResultset.next()) {
			VhDirAddress addr = new VhDirAddress();
			
			// Set ID
			addr.setId(addrResultset.getString("address_id"));
			
			// Set use
			String use = addrResultset.getString("use");
			if ("home".equals(use))
				addr.setUse(AddressUse.HOME);
			if ("work".equals(use))
				addr.setUse(AddressUse.WORK);
			if ("temp".equals(use))
				addr.setUse(AddressUse.TEMP);
			if ("old".equals(use))
				addr.setUse(AddressUse.OLD);
			if ("billing".equals(use))
				addr.setUse(AddressUse.BILLING);
			
			// Set Type
			
			// Set Text
			
			// Set Line
			String line1 = addrResultset.getString("line1");
			if (line1 != null ) {
				addr.addLine(line1);
			}
			String line2 = addrResultset.getString("line2");
			if (line2 != null) {
				addr.addLine(line2);
			}
			
			// Set City
			String city = addrResultset.getString("line2");
			if (city != null) {
				addr.setCity(city);
			}
			
			// Set District (County)
			String district = addrResultset.getString("district");
			if (district != null) {
				addr.setDistrict(district);
			}
						
			// Set State
			String state = addrResultset.getString("state");
			if (state != null) {
				addr.setState(state);
			}
			
			// Set PostalCode
			String postal = addrResultset.getString("postalCode");
			if (postal != null) {
				addr.setPostalCode(postal);
				VhDirGeoLocation loc = geocodePostalCode(postal.substring(0,5));
				addr.setGeolocation(loc);
			}
			
			// Set PostalCode
			String country = addrResultset.getString("country");
			if (country != null) {
				addr.setCountry(country);
			}
			
			prac.addAddress(addr);
		}
	}
	
	/**
	 * Handles the telecoms for the practitioner id passed in
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleTelecoms(Connection connection, VhDirPractitioner prac, int pracId) throws SQLException {
		String addrSql = "SELECT * from telecom where practitioner_id = ?";
		PreparedStatement telecomStatement = connection.prepareStatement(addrSql);
		telecomStatement.setInt(1, pracId);
		ResultSet telecomResultset = telecomStatement.executeQuery();
		while(telecomResultset.next()) {
			VhDirContactPoint tele = new VhDirContactPoint();
			
			// Set id
			tele.setId(telecomResultset.getString("telecom_id"));
			
			// Set system
			String system = telecomResultset.getString("system");
			if (system == null)
				tele.setSystem(ContactPointSystem.NULL);
			else if ("email".equals(system))
				tele.setSystem(ContactPointSystem.EMAIL);
			else if ("fax".equals(system))
				tele.setSystem(ContactPointSystem.FAX);
			else if ("other".equals(system))
				tele.setSystem(ContactPointSystem.OTHER);
			else if ("pager".equals(system))
				tele.setSystem(ContactPointSystem.PAGER);
			else if ("phone".equals(system))
				tele.setSystem(ContactPointSystem.PHONE);
			else if ("sms".equals(system))
				tele.setSystem(ContactPointSystem.SMS);
			else if ("url".equals(system))
				tele.setSystem(ContactPointSystem.URL);
			
			// Set value
			tele.setValue(telecomResultset.getString("value"));
			
			// Set some available time - for practitioners make it all day 7 days a week
			VhDirContactPointAvailableTime available = new VhDirContactPointAvailableTime();
			CodeType days = new CodeType();
			days.setSystem("http://hl7.org/fhir/days-of-week");
			days.setValue("sun");
			available.addDaysOfWeek(days);
			days = new CodeType();
			days.setSystem("http://hl7.org/fhir/days-of-week");
			days.setValue("mon");
			available.addDaysOfWeek(days);
			days = new CodeType();
			days.setSystem("http://hl7.org/fhir/days-of-week");
			days.setValue("tue");
			available.addDaysOfWeek(days);
			days = new CodeType();
			days.setSystem("http://hl7.org/fhir/days-of-week");
			days.setValue("wed");
			available.addDaysOfWeek(days);
			days = new CodeType();
			days.setSystem("http://hl7.org/fhir/days-of-week");
			days.setValue("thu");
			available.addDaysOfWeek(days);
			days = new CodeType();
			days.setSystem("http://hl7.org/fhir/days-of-week");
			days.setValue("fri");
			available.addDaysOfWeek(days);
			days = new CodeType();
			days.setSystem("http://hl7.org/fhir/days-of-week");
			days.setValue("sat");
			available.addDaysOfWeek(days);
			available.setAllDay(true);
			tele.addAvailableTime(available);
			
			prac.addTelecom(tele);
		}
	}
	
	
	/**
	 * Handles the telecoms for the practitioner id passed in
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleNames(Connection connection, VhDirPractitioner prac, int pracId) throws SQLException {
		String addrSql = "SELECT * from name where practitioner_id = ?";
		PreparedStatement nameStatement = connection.prepareStatement(addrSql);
		nameStatement.setInt(1, pracId);
		ResultSet names = nameStatement.executeQuery();
		while(names.next()) {
			HumanName name = new HumanName();
			// Set id
			name.setId(names.getString("name_id"));
			name.setFamily(names.getString("family"));
			name.addGiven(names.getString("given"));
			name.addPrefix(names.getString("prefix"));
			name.addSuffix(names.getString("suffix"));
			Period per = new Period();
			per.setStart(names.getDate("period_start"));
			per.setEnd(names.getDate("period_end"));
			name.setPeriod(per);
			
			String use = names.getString("use");
			if ( use == null || "usual".equals(use))
				name.setUse(NameUse.USUAL);
			
			
			prac.addName(name);
		}
	}
	
	private void handleRestrictions(Connection connection, VhDirPractitioner prac, int pracId) throws SQLException {
		String resSql = "SELECT * from vhdir_restriction where practitioner_id = ?";
		PreparedStatement nameStatement = connection.prepareStatement(resSql);
		nameStatement.setInt(1, pracId);

		//Reference restriction_ref = new Reference();
		//prac.addUsageRestriction(restriction_ref);

	}
	
	private void handleCommunications(Connection connection, VhDirPractitioner prac, int pracId) throws SQLException {
		// A communication is a codeable concept. Such codeable concepts can have multiple codings in it.
		// First, query the db for all the communications for this practioner
		String commSql = "SELECT * from communication where practitioner_id = ?";
		PreparedStatement commStatement = connection.prepareStatement(commSql);
		commStatement.setInt(1, pracId);
		ResultSet comms = commStatement.executeQuery();
		// Then, for each communication in the result set above, go and get all the codings for that communication...
		int cnt = 0;
		while (comms.next())   
		{	
			cnt++;
			CodeableConcept comm_cc = new CodeableConcept(); // To hold all the codings
			String commId = comms.getString("communication_id"); // set commId to the communication id
			comm_cc.setId(commId);
			// Now get all the codes belonging to this communication
			String codingSql = "SELECT * from fhir_codeable_concept where communication_id = ?";
			PreparedStatement codeStatement = connection.prepareStatement(codingSql);
			codeStatement.setString(1, commId);
			ResultSet codes = codeStatement.executeQuery();
			// Then for each code, above, add to the codeable concept
			while(codes.next()) {
				Coding coding = new Coding();
				coding.setId(codes.getString(codes.getString("codeable_concept_id")));
				coding.setSystem("http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-languageproficiency");
				coding.setVersion("0.2.0");
				coding.setDisplay(codes.getString("coding_display"));
				coding.setUserSelected(codes.getBoolean("coding_user_selected"));
				coding.setCode(codes.getString("coding_code"));
				comm_cc.addCoding(coding);
				String text = "";
				if (comm_cc.getText() != null) {
					text = comm_cc.getText();
				}
				comm_cc.setText(text + ", " + codes.getString("text"));
			}
			prac.addCommunication(comm_cc);
		}
		if (cnt == 0) {
			//  Didn't find any communications in the db, so let's just make something up for now
			CodeableConcept comm_cc = new CodeableConcept();
			Coding coding = new Coding();
			coding.setDisplay("Functional Native Proficiency");
			coding.setSystem("http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-languageproficiency");
			coding.setVersion("0.2.0");
			coding.setUserSelected(true);
			coding.setCode("50");
			// Generate a totally random id, prefaced by "x"
			Random ran = new Random();
			comm_cc.setId("x"+ ran.nextInt(10000-1 + 1));
			comm_cc.setText("Just made something up");
			comm_cc.addCoding(coding);
			prac.addCommunication(comm_cc);
		}
	}
	
	/**
	
	/**
	 * Calls a web service to get the lat/lon for the postal code passed in.
	 * 
	 * @param postalCode
	 * @return
	 */
	private VhDirGeoLocation geocodePostalCode(String postalCode) {
		VhDirGeoLocation loc = new VhDirGeoLocation();
		String baseUrl = "http://api.geonames.org/postalCodeSearchJSON?country=US&postalcode=";
		String fullUrl = baseUrl + postalCode + "&username=mholck";
		
		URL postResource;
		try {
			postResource = new URL(fullUrl);
			HttpURLConnection con;
			con = (HttpURLConnection) postResource.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json");
			con.setDoOutput(true);
			con.setConnectTimeout(0);
			con.setReadTimeout(0);
	
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String l = null;
			while ((l=br.readLine())!=null) {
				System.out.println(l);
				JsonElement result = new JsonParser().parse(l);
			    JsonObject resultObj = result.getAsJsonObject();
			    JsonArray postalCodes = resultObj.getAsJsonArray("postalCodes");
			    JsonObject propertiesJson = postalCodes.get(0).getAsJsonObject();
			    double lat = propertiesJson.get("lat").getAsDouble();
			    double lon = propertiesJson.get("lng").getAsDouble();
			    loc.setLatitude(lat);
			    loc.setLongitude(lon);
			}
			br.close();
	
			con.disconnect();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return loc;
	}
}
