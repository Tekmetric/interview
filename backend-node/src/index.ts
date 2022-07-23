import path from 'path';
import express, { RequestHandler } from 'express';
import cors from 'cors';
import bodyParser from 'body-parser';
import pino from 'pino-http';
import { logger } from './log';

import { API_VERSION } from './config';
import routes from './routes';

const app = express();

app.options('*', cors());
app.use(cors());

app.use('/media', express.static(path.join(__dirname, '../media')));

app.use(bodyParser.urlencoded({ extended: true }) as RequestHandler);
app.use(bodyParser.json() as RequestHandler);
app.use(pino() as RequestHandler);

app.get('/', (_req, res) => res.send('API is running'));

app.use(`/v${API_VERSION}`, routes);

const port = process.env.PORT || '3001';

if (process.env.ENV !== 'test') {
    (async () => {
        app.listen(parseInt(port), '0.0.0.0');
        logger.info('API running at localhost:' + port);
    })();
}
