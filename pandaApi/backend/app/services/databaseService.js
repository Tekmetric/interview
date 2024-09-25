const sqlRepository = require('./../repository/sqlRepository');

module.exports = {

    async clearAllData() {
        await sqlRepository.run(`delete from RedPanda;`, []);
        await sqlRepository.run(`delete from Sighting;;`, []);
    }

}