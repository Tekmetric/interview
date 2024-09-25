exports.actions = {

    services: {},

    constructor() {
        this.services.databaseService = require('../services/databaseService');
    },

    index(req, res) {
        req.allowHttpMethod('delete');

        this.services.databaseService.clearAllData();

        res.send(true);
    }

}