const express = require('express');
const request = require('request');

const app = express();
const API_URL = 'https://api.jikan.moe/v4'

app.use((req, res, next) => {
  res.header('Access-Control-Allow-Origin', '*');
  next();
});

app.get('/:route_path', (req, res) => {
  request(
    // used the route_path param to make it dynamic
    { url: `${API_URL}/${req.params.route_path}?page=${req.query.page}` },
    (error, response, body) => {
      if (error || response.statusCode !== 200) {
        return res.status(500).json({ type: 'error', message: error.message });
      }

      res.json(JSON.parse(body));
    }
  );
});

app.get('/:route_path/:id', (req, res) => {
  request(
    // used the route_path param to make it dynamic
    { url: `${API_URL}/${req.params.route_path}/${req.params.id}` },
    (error, response, body) => {
      if (error || response.statusCode !== 200) {
        return res.status(response.statusCode).json({ type: 'error', message: error?.message });
      }

      res.json(JSON.parse(body));
    }
  );
});

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`listening on ${PORT}`));