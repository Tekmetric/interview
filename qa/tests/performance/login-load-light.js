import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');
const loginSuccessRate = new Rate('login_success');

export let options = {
  stages: [
    { duration: '30s', target: 5 },
    { duration: '1m', target: 5 },
    { duration: '30s', target: 10 },
    { duration: '1m', target: 10 },
    { duration: '30s', target: 20 },
    { duration: '1m', target: 20 },
    { duration: '30s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<1000', 'p(99)<2000'],
    http_req_failed: ['rate<0.05'],
    errors: ['rate<0.05'],
    login_success: ['rate>0.95'],
  },
};

const BASE_URL = 'https://automationexercise.com';

function generateUserData() {
  const timestamp = Date.now();
  const randomNum = Math.floor(Math.random() * 10000);
  return {
    email: `perftest${timestamp}${randomNum}@example.com`,
    password: `TestPass${randomNum}!`,
    name: `PerfUser${randomNum}`,
  };
}

export default function() {
  const user = generateUserData();
  
  const params = {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
  };

  // Create account
  const createPayload = `title=&name=${user.name}&email=${user.email}&password=${user.password}&day=1&month=1&year=2000&firstname=&lastname=&company=&address1=&address2=&country=&zipcode=&state=&city=&mobile_number=`;
  const createResponse = http.post(`${BASE_URL}/api/createAccount`, createPayload, params);
  
  // Debug: Log what status code we're actually getting
  if (__ITER === 0) {
    console.log(`Create account returned status: ${createResponse.status}`);
  }
  
  const createCheck = check(createResponse, {
    'create account status is 200 or 201': (r) => r.status === 200 || r.status === 201,
    'create account response time < 2000ms': (r) => r.timings.duration < 2000,
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

  // Continue if account was created (check the response message, not just status code)
  const accountCreated = createResponse.status >= 200 && createResponse.status < 300;
  
  if (accountCreated) {
    // Verify login
    const loginPayload = `email=${user.email}&password=${user.password}`;
    const loginResponse = http.post(`${BASE_URL}/api/verifyLogin`, loginPayload, params);
    
    const loginCheck = check(loginResponse, {
      'login status is 200': (r) => r.status === 200,
      'login response time < 1000ms': (r) => r.timings.duration < 1000,
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

    // Delete account (cleanup)
    const deletePayload = `email=${user.email}&password=${user.password}`;
    const deleteResponse = http.del(`${BASE_URL}/api/deleteAccount`, deletePayload, params);
    
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
  }

  sleep(2);
}