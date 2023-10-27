interface Stat {
    base_stat: number
    effort: number,
    stat: {
        name: string,
    }
}

interface Type {
    type: {
        name: string,
    }
}

export interface Pokemon {
    id: number;
    name: string;
    sprites: {
        other: {
            dream_world: {
                front_default: string;
            }
        }
    }
    stats: Stat[],
    types: Type[],
}
