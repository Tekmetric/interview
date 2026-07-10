import { z } from 'zod';

// Close Approach Data Schema
export const CloseApproachDataSchema = z.object({
  close_approach_date: z.string(),
  close_approach_date_full: z.string(),
  epoch_date_close_approach: z.number().optional(),
  relative_velocity: z.object({
    kilometers_per_second: z.string(),
    kilometers_per_hour: z.string(),
    miles_per_hour: z.string(),
  }),
  miss_distance: z.object({
    astronomical: z.string(),
    lunar: z.string(),
    kilometers: z.string(),
    miles: z.string(),
  }),
  orbiting_body: z.string(),
});

// Estimated Diameter Schema
const EstimatedDiameterSchema = z.object({
  kilometers: z.object({
    estimated_diameter_min: z.number(),
    estimated_diameter_max: z.number(),
  }),
  meters: z.object({
    estimated_diameter_min: z.number(),
    estimated_diameter_max: z.number(),
  }),
  miles: z.object({
    estimated_diameter_min: z.number(),
    estimated_diameter_max: z.number(),
  }),
  feet: z.object({
    estimated_diameter_min: z.number(),
    estimated_diameter_max: z.number(),
  }),
});

// Base Asteroid Schema (for Browse API)
export const AsteroidBaseSchema = z.object({
  id: z.string(),
  neo_reference_id: z.string().optional(),
  name: z.string(),
  designation: z.string().optional(),
  nasa_jpl_url: z.string(),
  absolute_magnitude_h: z.number(),
  is_potentially_hazardous_asteroid: z.boolean(),
  estimated_diameter: EstimatedDiameterSchema,
});

// Asteroid with Close Approaches (for Feed API)
export const AsteroidWithApproachesSchema = AsteroidBaseSchema.extend({
  close_approach_data: z.array(CloseApproachDataSchema),
});

// Orbital Data Schema (for Detail API)
export const OrbitalDataSchema = z.object({
  orbit_id: z.string(),
  orbit_determination_date: z.string(),
  first_observation_date: z.string(),
  last_observation_date: z.string(),
  data_arc_in_days: z.number(),
  observations_used: z.number(),
  orbit_uncertainty: z.string(),
  minimum_orbit_intersection: z.string(),
  jupiter_tisserand_invariant: z.string(),
  epoch_osculation: z.string(),
  eccentricity: z.string(),
  semi_major_axis: z.string(),
  inclination: z.string(),
  ascending_node_longitude: z.string(),
  orbital_period: z.string(),
  perihelion_distance: z.string(),
  perihelion_argument: z.string(),
  aphelion_distance: z.string(),
  perihelion_time: z.string(),
  mean_anomaly: z.string(),
  mean_motion: z.string(),
  equinox: z.string(),
  orbit_class: z.object({
    orbit_class_type: z.string(),
    orbit_class_description: z.string(),
    orbit_class_range: z.string(),
  }),
});

// Full Asteroid Detail Schema (for Lookup API)
export const AsteroidDetailSchema = AsteroidBaseSchema.extend({
  neo_reference_id: z.string(),
  designation: z.string(),
  close_approach_data: z.array(CloseApproachDataSchema),
  orbital_data: OrbitalDataSchema,
  is_sentry_object: z.boolean(),
});

// Browse API Response Schema
export const NeoWsBrowseResponseSchema = z.object({
  links: z.object({
    next: z.string().optional(),
    prev: z.string().optional(),
    self: z.string(),
  }).optional(),
  page: z.object({
    size: z.number(),
    total_elements: z.number(),
    total_pages: z.number(),
    number: z.number(),
  }),
  near_earth_objects: z.array(AsteroidBaseSchema),
});

// Feed API Response Schema
export const NeoWsFeedResponseSchema = z.object({
  links: z.object({
    next: z.string().optional(),
    prev: z.string().optional(),
    self: z.string(),
  }).optional(),
  element_count: z.number(),
  near_earth_objects: z.record(z.string(), z.array(AsteroidWithApproachesSchema)),
});

// Export TypeScript types
export type CloseApproachData = z.infer<typeof CloseApproachDataSchema>;
export type AsteroidBase = z.infer<typeof AsteroidBaseSchema>;
export type AsteroidWithApproaches = z.infer<typeof AsteroidWithApproachesSchema>;
export type OrbitalData = z.infer<typeof OrbitalDataSchema>;
export type AsteroidDetail = z.infer<typeof AsteroidDetailSchema>;
export type NeoWsBrowseResponse = z.infer<typeof NeoWsBrowseResponseSchema>;
export type NeoWsFeedResponse = z.infer<typeof NeoWsFeedResponseSchema>;
