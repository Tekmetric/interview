type Environment = 'dev' | 'stg' | 'prod';


const ENV: Environment = (process.env.ENV as Environment) || 'prod';

const config = {
  dev: {
    
    baseURL: 'https://dev.automationintesting.online',

   
    apiURL: 'https://restful-booker.herokuapp.com',
  },

  stg: {
    baseURL: 'https://stg.automationintesting.online',
    apiURL: 'https://restful-booker.herokuapp.com',
  },

  prod: {
    baseURL: 'https://automationintesting.online',
    apiURL: 'https://restful-booker.herokuapp.com',
  },
};

export const envConfig = config[ENV];
export const currentEnv = ENV;