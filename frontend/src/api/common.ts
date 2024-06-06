export const baseUrl = import.meta.env.API_TARGET;

const networkDelay = parseInt(import.meta.env.NETWORK_DELAY);

export function delayNetwork() {
  return new Promise((resolve) => setTimeout(resolve, networkDelay));
}
