/**
 * Logger utility that only logs in development mode
 * In production, errors are logged but info/debug are suppressed
 */

const isDevelopment = process.env.NODE_ENV === 'development';

type LogFunction = (...args: any[]) => void;

interface Logger {
  log: LogFunction;
  info: LogFunction;
  warn: LogFunction;
  error: LogFunction;
  debug: LogFunction;
}

export const logger: Logger = {
  log: (...args: any[]): void => {
    if (isDevelopment) {
      console.log(...args);
    }
  },

  info: (...args: any[]): void => {
    if (isDevelopment) {
      console.info(...args);
    }
  },

  warn: (...args: any[]): void => {
    if (isDevelopment) {
      console.warn(...args);
    }
  },

  error: (...args: any[]): void => {
    // Always log errors, even in production
    console.error(...args);
  },

  debug: (...args: any[]): void => {
    if (isDevelopment) {
      console.debug(...args);
    }
  }
};
