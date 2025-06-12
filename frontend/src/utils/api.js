import { FETCH_NAME_URL, FETCH_NUMBER_URL } from "./constants";

const fetchMeaning = async (url) => {
  try {
    const response = await fetch(url);
    return response.ok ? response : null;
  } catch (error) {
    return null;
  }
}

export const fetchNameMeaning = async (name) => {
  const url = FETCH_NAME_URL.replace('$NAME', name);
  const response = await fetchMeaning(url);
  
  if (!response) return 'Oops! You fooled our search engine. We couldn\'t find anything about that!';

  const html = await response.text();
  const parser = new DOMParser();
  const doc = parser.parseFromString(html, 'text/html');

  return doc.querySelector('#mwAQ > p').textContent;
}

export const fetchNumberMeaning = async (number) => {
  const url = FETCH_NUMBER_URL + number;
  const response = await fetchMeaning(url);
  
  return await response.text();
}

