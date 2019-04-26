<h2>Synthetic Data Provider -  SPD Data Loader</h2>

This project implements the methods for loading data into the SPD schemas (MySQL) defined on the SPD VM.
The source data is contained in various MySQL schemas located on the SPD VM:  NPPES NPI data, CCIIP insurance data, etc.
This project will read data from the source, and insert it into the proper tables in the SPD schema.

Steps to follow to run the spd loader:
1) Check out the loader code and run npm install
2) Set up the following environment variables
	NPPES_DB
	SPD_DB
	SPD_DB_USER
	SPD_DB_PWD
	SPD_DB_HOST,
	SPD_DB_DIALECT,
	SPD_DB_PORT
3) run npm start
