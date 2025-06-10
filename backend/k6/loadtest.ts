import http from 'k6/http';
import { check } from 'k6';
import type { Options } from 'k6/options';

export let options: Options = {
    scenarios: {
        constant_users: {
            executor: 'constant-vus',
            // Number of concurrent users
            vus: 50,
            // How long to run
            duration: '60s',
        },
    },
    thresholds: {
        // 95% of requests should complete below 500ms
        http_req_duration: ['p(95)<500'],

        // At least 1000 requests per second
        http_reqs: ['rate>1000'],
    },
};

interface SetupData {
    token: string;
}

export function setup(): SetupData {
    const loginPayload = JSON.stringify({
        username: 'sorin',
        password: 'pass123'
    });
    const loginHeaders = { 'Content-Type': 'application/json' };
    const res = http.post('http://app:8080/auth/login', loginPayload, { headers: loginHeaders });
    check(res, { 'login status 200': (r) => r.status === 200 });
    const token = JSON.parse(res.body).token;
    return { token };
}

function randomPastDate(): string {
    const now = new Date();
    const past = new Date(now.getFullYear() - Math.floor(Math.random() * 40 + 18), Math.floor(Math.random() * 12), Math.floor(Math.random() * 28) + 1);
    return past.toISOString().replace(/\.\d{3}Z$/, 'Z');
}

function randomAlphaString(length: number): string {
    const chars = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    let result = '';
    for (let i = 0; i < length; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
}

function randomName(): string {
    return `${randomAlphaString(5)} ${randomAlphaString(7)}`;
}

export default function (data: SetupData): void {
    const personalNumber = `${__VU}${__ITER}${Math.floor(10000000 + Math.random() * 90000000)}`;

    const payload = JSON.stringify({
        name: randomName(),
        personalNumber: personalNumber,
        address: `123 Main St`,
        birthDate: randomPastDate()
    });

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${data.token}`
    };

    const res = http.post('http://app:8080/owners', payload, { headers });
    check(res, { 'POST /owners status 201': (r) => r.status === 201 });
}