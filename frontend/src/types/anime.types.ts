export type Images = {
  image_url: string;
  small_image_url: string;
  large_image_url: string;
};

export type ImagesFull = Images & {
  medium_image_url: string;
  maximum_image_url: string;
};

export type DateObject = {
  day: number;
  month: number;
  year: number;
};

export type AdditionalDetails = {
  mal_id: number;
  type: string;
  name: string;
  url: string;
};

export type AnimeItem = {
  mal_id: number;
  url: string;
  images: {
    jpg: Images;
    webp: Images;
  };
  trailer: {
    youtube_id: string;
    url: string;
    embed_url: string;
    images: ImagesFull;
  };
  approved: boolean;
  titles: {
    type: string;
    title: string;
  }[];
  title: string;
  title_english: string;
  title_japanese: string;
  title_synonyms: string[];
  type: string;
  source: string;
  episodes: number;
  status: string;
  airing: boolean;
  aired: {
    from: string;
    to: string;
    prop: {
      from: DateObject;
      to: DateObject;
    };
    string: string;
  };
  duration: string;
  rating: string;
  score: number;
  scored_by: number;
  rank: number;
  popularity: number;
  members: number;
  favorites: number;
  synopsis: string;
  background: string;
  season: string;
  year: number;
  broadcast: AdditionalDetails;
  producers: AdditionalDetails[];
  licensors: AdditionalDetails[];
  studios: AdditionalDetails[];
  genres: AdditionalDetails[];
  explicit_genres: any[];
  themes: AdditionalDetails[];
  demographics: any[];
};
