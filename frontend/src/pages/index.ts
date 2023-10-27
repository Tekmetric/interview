/**
 * PageFactory is an object which exports functions that import components to be leverages by React Lazy & Suspense
 */
export const PageFactory = {
    Pokedex: () => {
        return import(/* webpackChunkName: "Pokedex" */ './pokedex/Pokedex');
    },
    Pokemon: () => {
        return import(/* webpackChunkName: "Pokemon" */ './pokemon/Pokemon');
    },
}
