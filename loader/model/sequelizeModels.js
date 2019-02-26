import Sequelize from 'sequelize';
import { npidb, spddb } from '../util/dbconnect';
import _ from 'lodash';

const npiModel = npidb.define('npi', {
  organization_id: { type: Sequelize.INTEGER},
  organization_name: { type: Sequelize.STRING },
  is_subpart: { type: Sequelize.STRING },
  parent_name: { type: Sequelize.STRING },
}, {
	timestamps: false,
	underscored: true,
	freezeTableName: true,
	tableName: 'npi'	
});
npiModel.removeAttribute('id');

const Npi = npidb.models.npi;

const spdOrgModel = spddb.define('vhdir_organization', {
  organization_id: { type: Sequelize.INTEGER},
  meta_data_id: { type: Sequelize.STRING},
  active: { type: Sequelize.STRING},
  name: { type: Sequelize.STRING },
  description: { type: Sequelize.STRING},
  partOf_organization_id: { type: Sequelize.INTEGER},
  partOf_organization_name: { type: Sequelize.STRING},
}, {
	timestamps: false,
	underscored: true,
	freezeTableName: true,
	tableName: 'vhdir_organization'	
});
spdOrgModel.removeAttribute('id');

const Organization = spddb.models.vhdir_organization;

export { Npi, npidb, Organization, spddb };