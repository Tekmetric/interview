import axios from 'axios'

axios.defaults.baseURL = 'http://localhost:8080'
axios.defaults.timeout = 2000

axios.defaults.headers['Accept'] = 'application/json'
axios.defaults.headers['Content-Type'] = 'application/json'

axios.interceptors.response.use(
  response => response,
  error => Promise.reject(error),
)

const axios_instance = axios.create()

export { axios_instance }
