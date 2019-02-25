import Sequelize from 'sequelize';

if (typeof process.env.SPD_DB_HOST == 'undefined' ||
	typeof process.env.SPD_DB == 'undefined' || 
	typeof process.env.SPD_DB_USER == 'undefined' || 
	typeof process.env.SPD_DB_PWD == 'undefined') {
	console.log("You're unable to connect to the database due to lack of db connection configuration!");
}

const npidb = new Sequelize(process.env.NPPES_DB, process.env.SPD_DB_USER, process.env.SPD_DB_PWD, {
  host: process.env.SPD_DB_HOST,
  dialect: process.env.SPD_DB_DIALECT,
  port: process.env.SPD_DB_PORT
});

const spddb = new Sequelize(process.env.SPD_DB, process.env.SPD_DB_USER, process.env.SPD_DB_PWD, {
  host: process.env.SPD_DB_HOST,
  dialect: process.env.SPD_DB_DIALECT,
  port: process.env.SPD_DB_PORT
});

export {npidb, spddb} ;