from pydantic import BaseModel, computed_field


class EstimatedDiameters(BaseModel):
    estimated_diameter_min: float
    estimated_diameter_max: float

    def get_average_diameter(self) -> float:
        return (self.estimated_diameter_min + self.estimated_diameter_max) / 2


class EstimatedDiametersMeters(BaseModel):
    meters: EstimatedDiameters


class MissDistance(BaseModel):
    kilometers: float
    lunar: float
    miles: float
    astronomical: float


class RelativeVelocity(BaseModel):
    kilometers_per_second: float
    kilometers_per_hour: float
    miles_per_hour: float


class CloseApproach(BaseModel):
    miss_distance: MissDistance
    close_approach_date: str
    relative_velocity: RelativeVelocity


class Observation(BaseModel):
    first_observation_date: str
    last_observation_date: str
    observations_used: int
    orbital_period: str


class NearEarthObject(BaseModel):
    id: str
    neo_reference_id: str
    name: str
    name_limited: str
    designation: str
    nasa_jpl_url: str
    absolute_magnitude_h: float
    is_potentially_hazardous_asteroid: bool
    orbital_data: Observation
    estimated_diameter: EstimatedDiametersMeters
    close_approach_data: list[CloseApproach]

    @property
    @computed_field
    def closest_approach(self) -> CloseApproach | None:
        if not self.close_approach_data:
            return None

        return min(
            self.close_approach_data,
            key=lambda approach: approach.miss_distance.kilometers,
        )
