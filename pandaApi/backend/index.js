const express = require('express')
var bodyParser = require('body-parser')
var cors = require('cors')
const app = express()
const port = 3000

app.use(cors())
app.use(bodyParser.json())

app.get('/*', (req, res) => {
    resolveAction(req, res);
});

app.post('/*', (req, res) => {
    resolveAction(req, res);
});

app.put('/*', (req, res) => {
    resolveAction(req, res);
});

app.delete('/*', (req, res) => {
    resolveAction(req, res);
});

async function resolveAction(req, res) {
    appendRequestExtensions(req);
    var urlSegments = req.originalUrl.split('/');
    var indexOfApiSegment = urlSegments.findIndex(x => x.toLowerCase() == 'api');

    var controllerName = urlSegments[indexOfApiSegment + 1];
    if (controllerName.indexOf('?') > -1) {
        controllerName = controllerName.split('?')[0];
    }
    var controller = require('./app/controllers/' + controllerName + 'Controller.js');
    controller.actions.constructor();

    var actionName = urlSegments[indexOfApiSegment + 2];
    if (actionName && actionName.indexOf('?')) {
        actionName = actionName.split('?')[0];
    }
    if (actionName) {
        await controller.actions[actionName](req, res);
    } else {
        await controller.actions['index'](req, res);
    }
}

function appendRequestExtensions(req) {
    req.allowHttpMethod = (allowedHttpMethod) => {
        if (allowedHttpMethod.toLowerCase() !== req.method.toLowerCase()) {
            throw `${allowedHttpMethod.toUpperCase()} Allowed Only!`;
        }
    };
}

app.listen(port, () => {
    console.log(`Example app listening on port ${port}`)
});