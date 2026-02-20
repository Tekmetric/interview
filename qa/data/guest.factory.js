function createGuestDetails(suffix) {
  return {
    firstname: 'Jane',
    lastname: 'Doe',
    email: `jane.doe.${suffix.toLowerCase()}@example.com`,
    phone: '12345678901'
  };
}

function deterministicSuffix(testInfo) {
  const titleSlug = testInfo.title.replace(/[^a-zA-Z0-9]+/g, '-').slice(0, 24);
  return `${testInfo.parallelIndex}-${titleSlug}-${Date.now().toString().slice(-6)}`;
}

module.exports = {
  createGuestDetails,
  deterministicSuffix
};
