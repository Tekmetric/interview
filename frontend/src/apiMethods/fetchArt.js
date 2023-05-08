async function fetchArt({ queryKey }) {
  const { author, title } = queryKey[1];
  const API_KEY = import.meta.env.VITE_RJIKS_API_KEY;

  const res = await fetch(
    `https://www.rijksmuseum.nl/api/en/collection?key=${API_KEY}&involvedMaker=${author}&q=${title}`
  );

  if (!res.ok)
    throw new Error(
      `FetchArt is failing for the following parametes: ${author}, ${title}`
    );

  return res.json();
}

export default fetchArt;
