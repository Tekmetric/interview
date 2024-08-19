/**
 * Pauses execution until a specified number of milliseconds has elapsed.
 * @param {number} delay The time in milliseconds to sleep
 * @returns {Promise<null>}
 */
const sleep = (delay) => new Promise((resolve) => setTimeout(resolve, delay));

export { sleep };
