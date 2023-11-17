const ComponentFactory = {
  GameCard: () => import(/* webpackChunkName: "GameCard" */ './gamecard/GameCard'),
  GameList: () => import(/* webpackChunkName: "GameList" */ './gamelist/GameList'),
};

export {
  ComponentFactory,
};
