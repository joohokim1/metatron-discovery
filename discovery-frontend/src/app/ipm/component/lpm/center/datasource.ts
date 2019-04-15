export class DataSource {
	
	constructor() {

	}

	public getDataSourceOptions(layerId: string): any {
		switch(layerId) {
			case 'dsmc_bypcell_flow_popul_inf_1h': 
				return this.getFloatingPopulationInfo();

			default: return null;
		}
	}

	public getFloatingPopulationInfo(): any {
		return {
            dataSource: 'dsmc_bypcell_flow_popul_inf_1h',
            addrField: 'ldong_cd',
            dateTimeField: 'event_time',
            ageField: 'age',
            genderField: 'sex',
            sort: { by: 'TEXT', direction: 'ASC' }
        }
	}
}