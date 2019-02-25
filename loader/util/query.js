import { Npi, npidb, Organization } from '../model/sequelizeModels';

export const query = {
	async getNpi() {
		var npiQuery = "SELECT NPI as organization_id, `Provider Organization Name (Legal Business Name)` as organization_name, `Parent Organization LBN` as parent_name, `Is Organization Subpart` as is_subpart from npi where `Entity Type Code` = 2 limit 0, 20";
		var res = await npidb.query(npiQuery, { model: Npi } );
		//console.log("NPI result: "+JSON.stringify(res));
		for (var i = 0; i < res.length; i++) {
			Organization.findOrCreate({where: {name: res[i].organization_name}, defaults: {
				  active: '1',
				  name: res[i].organization_name,
				  partOf_organization_name: res[i].parent_name
			}
			});
		}

	}
};