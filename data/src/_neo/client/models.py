from pydantic import BaseModel


class EstimatedDiameter(BaseModel):
    estimated_diameter_min: float
    estimated_diameter_max: float


class EstimatedDiameterMeters(BaseModel):
    meters: EstimatedDiameter


class RelativeVelocity(BaseModel):
    kilometers_per_second: str


class MissDistance(BaseModel):
    kilometers: str
    astronomical: str


class ClosestApproachData(BaseModel):
    close_approach_date: str
    miss_distance: MissDistance
    relative_velocity: RelativeVelocity


class OrbitalData(BaseModel):
    first_observation_date: str | None
    last_observation_date: str | None
    observations_used: int | None
    orbital_period: float | None


class NearEarthObject(BaseModel):
    id: str
    neo_reference_id: str
    name: str
    name_limited: str | None = None
    designation: str
    nasa_jpl_url: str
    absolute_magnitude_h: float
    is_potentially_hazardous_asteroid: bool
    estimated_diameter: EstimatedDiameterMeters
    close_approach_data: list[ClosestApproachData]
    orbital_data: OrbitalData

    @property
    def closest_approach(self) -> ClosestApproachData | None:
        """Return the closest approach data based on minimum miss distance in kilometers."""
        if not self.close_approach_data:
            return None
        return min(self.close_approach_data, key=lambda x: float(x.miss_distance.kilometers), default=None)

    @property
    def closest_approach_date(self) -> str | None:
        """Return the date of the closest approach."""
        return self.closest_approach.close_approach_date if self.closest_approach else None

    @property
    def closest_approach_miss_distance(self) -> str | None:
        """Return the miss distance in kilometers of the closest approach."""
        return self.closest_approach.miss_distance.kilometers if self.closest_approach else None

    @property
    def closest_relative_velocity_kps(self) -> str | None:
        """Return the relative velocity in kilometers per second of the closest approach."""
        return self.closest_approach.relative_velocity.kilometers_per_second if self.closest_approach else None
