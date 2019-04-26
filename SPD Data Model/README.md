<h2>Synthetic Provider Data - SPD Data Model</h2>

This section of the repository holds the relational data model representing the FHIR profiles defined in the VHDIR Implementation Guide (http://build.fhir.org/ig/HL7/VhDir/index.html).

The model is a MySQL data model (.mwb file).

MySQL create scripts are present in the SQL Files folder. These scripts are generated from the data model file, and they are all identical except
for the name of the database schema being created. Six schemas are used in this project:

<ul>
<li><b>spd</b> - the base schema, used during development to hold the data from various external datasources</li>
<li><b>spd_scrubbed</b> - a replica of the spd schema. This will hold the same data as the spd schema, but the data will be 'scrubbed' to remove 
or de-identify personal or locational data.
<li><b>spd_small</b> - a replica of the spd schema that holds only a small subset of the external datasource data
<li><b>spd_scrubbed_small</b> - a replica of the spd_small schema, but with scrubbed or di-identified data elements
<li><b>spd_medium</b> - a replica of the spd schema that holds only a moderately largesubset of the external datasource data
<li><b>spd_scrubbed_medium</b> - a replica of the spd_medium schema, but with scrubbed or di-identified data elements
<li><b>spd_large</b> - a replica of the spd schema that holds all of the external datasource data - a very large amount of data
<li><b>spd_scrubbed_large</b> - a replica of the spd_large schema, but with scrubbed or di-identified data elements
</ul>
