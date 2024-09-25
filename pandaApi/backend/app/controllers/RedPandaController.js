exports.actions = {

    services: {},

    constructor() {
        this.services.redPandaService = require('../services/redPandaService');
    },

    async index(req, res) {
        req.allowHttpMethod('get');
        const redPandaId = req.query.id;

        var result = await this.services.redPandaService.get(redPandaId);

        res.send(result);
    },

    async add(req, res) {
        req.allowHttpMethod('post');
        const redPandaInput = {
            hasTracker: req.body['hasTracker'],
            color: req.body['color'],
            species: req.body['species'],
            name: req.body['name'],
            age: req.body['age']
        };

        var result = await this.services.redPandaService.addRedPanda(redPandaInput);

        res.send({ id: result });
    },

    async list(req, res) {
        req.allowHttpMethod('get');

        var result = await this.services.redPandaService.getAll();

        res.send(result);
    },

    async update(req, res) {
        req.allowHttpMethod('put');
        const redPandaInput = {
            id: req.body['id'],
            hasTracker: req.body['hasTracker'],
            color: req.body['color'],
            species: req.body['species'],
            name: req.body['name'],
            age: req.body['age']
        };

        var result = await this.services.redPandaService.update(redPandaInput);

        res.send(result);
    },

    async delete(req, res) {
        req.allowHttpMethod('delete');

        await this.services.redPandaService.delete(req.query.id);

        res.send(req.query.id);
    }

}