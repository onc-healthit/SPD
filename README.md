# Synthetic Provider Data - SPD

This repository contains the tools and applications used to create the Synthetic Provider Data (SPD) FHIR datasets.

Multiple disparate data sources are present in their own MySql schemas housed on the SPD VM.  Examples of these data sets are NPPES NPI data and CCIIO insurance plan data. These data sets are read by data loader programs, converted as needed, and then inserted into a single SPD schema (MySql) also on the VM.  The SPD schema models the VHDIR (Virtual Healthcare Directory) data as defined in the VHDIR Implementation Guide (IG) located at https://www.hl7.org/fhir/valueset-encounter-type.html

Once the SPD schema is populated, the data is de-identified and/or "scrubbed" and inserted into an SPD_Scrubbed (MySql) schema on the VM.

Finally, a Java application(s) will read the scrubbed data from various tables in the SPD_scrubbed schema, and generate FHIR resource data files created from the data.
