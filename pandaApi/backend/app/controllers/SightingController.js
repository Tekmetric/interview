exports.actions = {

    services: {},

    constructor() {
        this.services.sightingService = require('../services/sightingService');
    },

    async index(req, res) {
        req.allowHttpMethod('get');
        const sightingId = req.query.id;

        var result = await this.services.sightingService.get(sightingId);

        res.send(result);
    },

    async add(req, res) {
        req.allowHttpMethod('post');
        const sightingInput = {
            dateTime: req.body['dateTime'],
            locationLat: req.body['location'].latitude,
            locationLon: req.body['location'].longitude,
            pandaId: req.body['pandaId']
        };

        await this.services.sightingService.addSighting(sightingInput);

        res.send(true);
    },

    async list(req, res) {
        req.allowHttpMethod('get');

        var result = await this.services.sightingService.getAll();

        res.send(result);
    },

    async update(req, res) {
        req.allowHttpMethod('put');
        const sightingInput = {
            id: req.body['id'],
            dateTime: req.body['dateTime'],
            locationLat: req.body['location'].latitude,
            locationLon: req.body['location'].longitude,
            pandaId: req.body['pandaId']
        };

        var result = await this.services.sightingService.update(sightingInput);

        res.send(result);
    },

    async filter(req, res) {
        req.allowHttpMethod('get');
        const filterInput = {
            field: req.query.field,
            value: req.query.value,
            itemsPerPage: req.query.itemsPerPage,
            page: req.query.page
        };

        var result = await this.services.sightingService.filter(filterInput);

        res.send(result);
    },

    async delete(req, res) {
        req.allowHttpMethod('delete');

        await this.services.sightingService.delete(req.query.id);

        res.send(req.query.id);
    }

}