import { createLogger } from 'redux-logger';

const loggerMiddleware = createLogger({
	collapsed: true,
	titleFormatter: (action, time, took) =>
		`Learning Resources action ${action.type} @${time} took ${took.toFixed(2)} ms`,
});

export default loggerMiddleware;
