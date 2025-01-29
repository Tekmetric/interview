import RocketDetails from '../RocketDetails'

export default {
  title: 'Components/RocketDetails',
  component: RocketDetails,
}

const rocketsExample = [
  {
    id: '1',
    name: 'Falcon 9',
    description: 'A reusable rocket designed and manufactured by SpaceX.',
    flickr_images: ['/images/falcon9.png'],
    height: { meters: 70, feet: 229.6 },
    diameter: { meters: 3.7, feet: 12 },
    mass: { kg: 549054, lb: 1219650 },
    stages: 1,
    boosters: 0,
    first_flight: '2010-06-04',
    company: 'SpaceX',
    country: 'USA',
    cost_per_launch: 62000000,
    success_rate_pct: 97,
    active: true,
    first_stage: {
      reusable: true,
      engines: 9,
      fuel_amount_tons: 385,
      burn_time_sec: 162,
      thrust_sea_level: { kN: 7607, lbf: 1710000 },
      thrust_vacuum: { kN: 8227, lbf: 1840000 },
    },
    second_stage: {
      engines: 1,
      reusable: false,
      fuel_amount_tons: 90,
      burn_time_sec: 397,
      thrust: { kN: 934, lbf: 210000 },
    },
    engines: {
      number: 9,
      type: 'merlin',
      version: '1D+',
      layout: 'octaweb',
      engine_loss_max: 2,
      propellant_1: 'liquid oxygen',
      propellant_2: 'RP-1 kerosene',
      thrust_sea_level: { kN: 845, lbf: 190000 },
      thrust_vacuum: { kN: 914, lbf: 205500 },
      thrust_to_weight: 180,
    },
    landing_legs: {
      number: 4,
      material: 'carbon fiber',
    },
    payload_weights: [
      { id: 'leo', name: 'Low Earth Orbit', kg: 22800, lb: 50265 },
    ],
    wikipedia: 'https://en.wikipedia.org/wiki/Falcon_9',
  },
]

export const Default = () => <RocketDetails rockets={rocketsExample} />
