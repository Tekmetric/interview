import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Rate } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');
const loginSuccessRate = new Rate('login_success');

// Test configuration
export const options = {
  stages: [
    { duration: '30s', target: 20 },    // Ramp up to 50 users
    { duration: '1m', target: 30 },    // Stay at 50 users
    { duration: '1m', target: 50 },   // Ramp up to 100 users
    { duration: '1m', target: 50 },   // Stay at 100 users
    { duration: '30s', target: 0 },     // Ramp down
  ],
  thresholds: {
    http_req_duration: ['p(95)<500', 'p(99)<1000'],
    http_req_failed: ['rate<0.01'],
    errors: ['rate<0.01'],
    login_success: ['rate>0.99'],
  },
};

// Configuration - update with your actual API URL
const BASE_URL = 'https://automationexercise.com';
const API_ENDPOINTS = {
  createAccount: `${BASE_URL}/api/createAccount`,
  verifyLogin: `${BASE_URL}/api/verifyLogin`,
  deleteAccount: `${BASE_URL}/api/deleteAccount`,
};

// Generate random user data
function generateUserData() {
  const timestamp = Date.now();
  const randomNum = Math.floor(Math.random() * 10000);
  return {
    email: `perftest${timestamp}${randomNum}@example.com`,
    password: `TestPass${randomNum}!`,
    name: `PerfUser${randomNum}`,
  };
}

// Helper function to make API calls with configurable HTTP method
function makeApiCall(endpoint, payload, method = 'POST') {
  const params = {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
  };

  if (method === 'DELETE' || method === 'DEL') {
    return http.del(endpoint, payload, params);
  } else if (method === 'POST') {
    return http.post(endpoint, payload, params);
  } else if (method === 'GET') {
    return http.get(endpoint, params);
  } else if (method === 'PUT') {
    return http.put(endpoint, payload, params);
  }

  // Default to POST if method not recognized
  return http.post(endpoint, payload, params);
}

// This is the required default export function
export default function () {
  // Generate unique user for this iteration
  const user = generateUserData();

  // Test Scenario 1: Valid Login Flow (Happy Path)
  group('Valid Login Flow', function () {
    // Step 1: Create account
    const createPayload = `title=&name=${user.name}&email=${user.email}&password=${user.password}&day=1&month=1&year=2000&firstname=&lastname=&company=&address1=&address2=&country=&zipcode=&state=&city=&mobile_number=`;

    const createResponse = makeApiCall(API_ENDPOINTS.createAccount, createPayload);

    const createCheck = check(createResponse, {
      'create account status is 200 or 201': (r) => r.status === 200 || r.status === 201,
      'create account response time < 1000ms': (r) => r.timings.duration < 1000,
      'account created successfully': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.responseCode === 201 && body.message === 'User created!';
        } catch {
          return false;
        }
      },
    });

    errorRate.add(!createCheck);

    // Step 2: Verify login with valid credentials
    const loginPayload = `email=${user.email}&password=${user.password}`;

    const loginResponse = makeApiCall(API_ENDPOINTS.verifyLogin, loginPayload);

    const loginCheck = check(loginResponse, {
      'login status is 200': (r) => r.status === 200,
      'login response time < 500ms': (r) => r.timings.duration < 500,
      'login successful': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.responseCode === 200 && body.message === 'User exists!';
        } catch {
          return false;
        }
      },
    });

    errorRate.add(!loginCheck);
    loginSuccessRate.add(loginCheck);

    // Step 3: Delete account (cleanup) - NOW USING DELETE METHOD
    const deletePayload = `email=${user.email}&password=${user.password}`;

    const deleteResponse = makeApiCall(API_ENDPOINTS.deleteAccount, deletePayload, 'DELETE');

    check(deleteResponse, {
      'delete account status is 200': (r) => r.status === 200,
      'account deleted successfully': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.responseCode === 200 && body.message === 'Account deleted!';
        } catch {
          return false;
        }
      },
    });
  });

  // Test Scenario 2: Invalid Login Attempts (Unhappy Paths)
  group('Invalid Login Attempts', function () {
    const testUser = generateUserData();

    // Test non-existent user
    const nonExistentPayload = `email=${testUser.email}&password=${testUser.password}`;
    const nonExistentResponse = makeApiCall(API_ENDPOINTS.verifyLogin, nonExistentPayload);

    check(nonExistentResponse, {

      'non-existent user message correct': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.responseCode === 404 && body.message === 'User not found!';
        } catch {
          return false;
        }
      },
    });

    // Test empty credentials
    const emptyPayload = 'email=&password=';
    const emptyResponse = makeApiCall(API_ENDPOINTS.verifyLogin, emptyPayload);

    check(emptyResponse, {

      'empty credentials error in body': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.responseCode === 404;
        } catch {
          return false;
        }
      },
    });

    // Test missing email parameter
    const missingEmailPayload = 'password=testpassword';
    const missingEmailResponse = makeApiCall(API_ENDPOINTS.verifyLogin, missingEmailPayload);

    check(missingEmailResponse, {
      'missing email error message correct': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.responseCode === 400 &&
            body.message === 'Bad request, email or password parameter is missing in POST request.';
        } catch {
          return false;
        }
      },
    });
  });

  // Simulate user think time
  sleep(1);
}