const sqlRepository = require('./../repository/sqlRepository');
const { v4: uuidv4 } = require('uuid');

module.exports = {

    async addSighting(sightingInput) {
        var id = uuidv4();

        await sqlRepository.run(
            `insert into Sighting(id, dateTime, locationLat, locationLon, pandaId) 
            values(?, ?, ?, ?, ?);`,
            [id, sightingInput.dateTime, sightingInput.locationLat, sightingInput.locationLon, sightingInput.pandaId]
        );
    },

    async getAll() {
        return await sqlRepository.all("select * from Sighting;;");
    },

    async get(sightingId) {
        var result = await sqlRepository.filter("select * from Sighting where id = ? limit 1;", [sightingId]);
        if (result.length == 0) {
            return null;
        }
        return result[0];
    },

    async update(sightingInput) {
        await sqlRepository.run(
            `update Sighting 
            set dateTime=?, locationLat=?, locationLon=?, pandaId=? 
            where id=?;`,
            [sightingInput.dateTime, sightingInput.locationLat, sightingInput.locationLon, sightingInput.pandaId, sightingInput.id]
        );

        return sightingInput;
    },

    async filter(filterInput) {
        var dbItems = await sqlRepository.filter(
            `select s.*, rp.hasTracker rp_hasTracker, rp.color rp_color, rp.species rp_species, rp.name rp_name, rp.age rp_age from Sighting s 
            join RedPanda rp ON rp.id = s.pandaId 
            where s.${filterInput.field} = ? 
            limit ? offset ?;`
            , [filterInput.value, filterInput.itemsPerPage, filterInput.itemsPerPage * filterInput.page]);
        var dbCount = await sqlRepository.filter(`select count(*) count from Sighting where ${filterInput.field} = ?`, [filterInput.value]);

        return {
            totalPages: Math.ceil(dbCount[0].count / filterInput.itemsPerPage),
            items: dbItems.map(x => ({
                id: x.id,
                dateTime: x.dateTime,
                locationLat: x.locationLat,
                locationLon: x.locationLon,
                pandaId: x.pandaId,
                panda: {
                    hasTracker: x.rp_hasTracker,
                    color: x.rp_color,
                    species: x.rp_species,
                    name: x.rp_name,
                    age: x.rp_age
                }
            }))
        }
    },

    async delete(sightingId){
        await sqlRepository.run(
            `delete from Sighting 
            where id=?`,
            [sightingId]
        );
    }

}