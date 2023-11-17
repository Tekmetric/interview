const ComponentFactory = {
  GameCard: () => import(/* webpackChunkName: "GameCard" */ './gamecard/GameCard'),
  GameList: () => import(/* webpackChunkName: "GameList" */ './gamelist/GameList'),
  Pokedex: () => import(/* webpackChunkName: "Pokedex" */ './pokedex/Pokedex'),
  PokedexEntry: () => import(/* webpackChunkName: "PokedexEntry" */ './pokedexentry/PokedexEntry'),
};

export { ComponentFactory };
