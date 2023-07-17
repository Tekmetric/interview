type Dog = {
  name: string;
  bread_for: string;
  life_span: string;
  temperament: string;
  weight: {
    imperial: string;
    metric: string;
  };
  image: {
    id: string;
    width: number;
    height: number;
    url: string;
  };
};

export type { Dog };
