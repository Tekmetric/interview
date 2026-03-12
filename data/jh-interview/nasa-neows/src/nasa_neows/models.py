from pydantic import BaseModel, ConfigDict


class BaseModelExtra(BaseModel):
    """Base model that silently ignores extra fields from API responses."""

    model_config = ConfigDict(extra="ignore")


class PageInfo(BaseModelExtra):
    """Pagination metadata returned by the NeoWs browse endpoint."""

    size: int
    total_elements: int
    total_pages: int
    number: int


class NearEarthObject(BaseModelExtra):
    """A single Near Earth Object (asteroid or comet) as returned by the NASA NeoWs API."""

    id: str
    neo_reference_id: str
    name: str
    name_limited: str | None = None
    designation: str | None = None
    nasa_jpl_url: str
    absolute_magnitude_h: float
    is_potentially_hazardous_asteroid: bool
    estimated_diameter: dict
    close_approach_data: list[dict]
    orbital_data: dict


class BrowseResponse(BaseModelExtra):
    """Top-level response from the NeoWs browse endpoint, containing a page of NEOs."""

    links: dict
    page: PageInfo
    near_earth_objects: list[NearEarthObject]
