type Environment = 'development' | 'qa' | 'staging' | 'production';

// Function to strictly fetch an environment variable
export const requireEnvVariable = (name: string): string => {
  const value = process.env[name];
  if (!value) throw new Error(`Environment variable ${name} is not set.`);
  return value;
};

// Get the API URL based on the environment
export const getApiUrl = (): string => {
  const env: Environment = (process.env.TEST_ENV as Environment) || 'development';
  const apiUrls = {
    development: 'DEV_API_URL',
    qa: 'QA_API_URL',
    staging: 'STAGING_API_URL',
    production: 'PROD_API_URL',
  };
  return requireEnvVariable(apiUrls[env]);
};