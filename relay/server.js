/* 
    this is a relay server that abstracts the API key from .env file away from the frontend
    to avoid having it exposed to the public. The endpoints are pass-through to CoinMarketCap's
    API.
*/

require('dotenv').config();
const express = require('express');
const fetch = require('node-fetch');
const rateLimit = require('express-rate-limit');
const proxy = require('express-http-proxy');
const cors = require('cors');

const app = express();
const port = process.env.PORT || 3001;

const corsOptions = {
    origin: process.env.FRONTEND_URL, // only allow requests from our own React app
    optionsSuccessStatus: 200
};

const limiter = rateLimit({
    windowMs: 10 * 1000, // 10 seconds
    max: 30 // 30 requests per minute
});

app.use(limiter);
app.use(cors(corsOptions));

// pass-through endpoint for CoinMarketCap's API
app.use('/api', proxy('https://pro-api.coinmarketcap.com', {
    proxyReqOptDecorator: function(proxyReqOpts, srcReq) {
        proxyReqOpts.headers['X-CMC_PRO_API_KEY'] = process.env.CMC_API_KEY;
        return proxyReqOpts;
    }
}));

app.listen(port, () => console.log(`Listening on port ${port}`));