module.exports = {
  generateSku,
  generateProductName,
  generatePrice,
  logResponse,
  validateResponse
};

function generateSku(context, events, done) {
  const timestamp = Date.now();
  const random = Math.floor(Math.random() * 1000);
  context.vars.sku = `LOAD-${timestamp}-${random}`;
  return done();
}

function generateProductName(context, events, done) {
  const adjectives = ['Premium', 'Deluxe', 'Standard', 'Professional', 'Economy'];
  const products = ['Laptop', 'Mouse', 'Keyboard', 'Monitor', 'Chair', 'Desk', 'Phone', 'Tablet'];
  
  const adj = adjectives[Math.floor(Math.random() * adjectives.length)];
  const prod = products[Math.floor(Math.random() * products.length)];
  
  context.vars.productName = `${adj} ${prod} ${Math.floor(Math.random() * 1000)}`;
  return done();
}

function generatePrice(context, events, done) {
  const price = (Math.random() * 500 + 10).toFixed(2);
  context.vars.price = parseFloat(price);
  return done();
}

function logResponse(requestParams, response, context, ee, next) {
  if (response.statusCode >= 400) {
    console.log(`Error ${response.statusCode}: ${requestParams.url}`);
    console.log('Response body:', response.body);
  }
  return next();
}

function validateResponse(requestParams, response, context, ee, next) {
  if (response.statusCode >= 500) {
    ee.emit('error', `Server error: ${response.statusCode}`);
  }
  return next();
}