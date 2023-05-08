const fetchArtDetails = async ({ queryKey }) => {
  // hacky workaround, the detail API expects NL ids that do not have the en- prefix
  const id = queryKey[1].split("en-")[1];
  const API_KEY = import.meta.env.VITE_RJIKS_API_KEY;
  const apiRes = await fetch(
    `https://www.rijksmuseum.nl/api/en/collection/${id}?key=${API_KEY}`
  );

  if (!apiRes.ok) {
    throw new Error(`FetchArtDetails is failing for the following id: ${id}`);
  }

  return apiRes.json();
};

export default fetchArtDetails;
