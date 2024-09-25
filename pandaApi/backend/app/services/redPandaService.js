const sqlRepository = require('./../repository/sqlRepository');
const { v4: uuidv4 } = require('uuid');

module.exports = {

    async addRedPanda(redPandaInput) {
        var id = uuidv4();

        await sqlRepository.run(
            `insert into RedPanda(id, hasTracker, color, species, name, age) 
            values(?, ?, ?, ?, ?, ?);`,
            [id, redPandaInput.hasTracker, redPandaInput.color, redPandaInput.species, redPandaInput.name, redPandaInput.age]
        );

        return id;
    },

    async getAll() {
        return await sqlRepository.all("select * from RedPanda;");
    },

    async get(redPandaId) {
        var result = await sqlRepository.filter(`
            select rp.*, s.dateTime mostRecentSightingDateTime, s.locationLat mostRecentSightingLocationLat, s.locationLon mostRecentSightingLocationLon from RedPanda rp 
            left join Sighting s ON s.pandaId = rp.id 
            where rp.id = ? 
            order by s.dateTime desc 
            limit 1 
            ;`, [redPandaId]);
        if (result.length == 0) {
            return null;
        }
        result = result[0];

        return {
            id: result.id,
            hasTracker: result.hasTracker,
            color: result.color,
            species: result.species,
            name: result.name,
            age: result.age,
            mostRecentSighting: {
                dateTime: result.mostRecentSightingDateTime,
                locationLat: result.mostRecentSightingLocationLat,
                locationLon: result.mostRecentSightingLocationLon
            }
        };
    },

    async update(redPandaInput) {
        await sqlRepository.run(
            `update RedPanda 
            set hasTracker=?, color=?, species=?, name=?, age=? 
            where id=?`,
            [redPandaInput.hasTracker, redPandaInput.color, redPandaInput.species, redPandaInput.name, redPandaInput.age, redPandaInput.id]
        );

        return redPandaInput;
    },

    async delete(redPandaId) {
        await sqlRepository.run(
            `delete from RedPanda 
            where id=?`,
            [redPandaId]
        );
    }

}