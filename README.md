# Synthetic Provider Data - SPD

This repository contains the tools and applications used to create the Synthetic Provider Data (SPD) FHIR datasets.

Multiple disparate data sources are present in their own MySql schemas houses on the SPD VM.  These data sources are read by data loader programs, converted as needed, and then inserted into a single SPD schema (MySql) also on the VM.

Once the SPD schema is populated, the data is de-identified and/or "scrubbed" and inserted into an SPD_Scrubbed (MySql) schema on the VM.

Finally, a Java application(s) will read the scrubbed data from various tables in the SPD_scrubbed schema, and generate FHIR resource data files created from the data.
