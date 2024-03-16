import React, { useState, useEffect } from "react";
import "./NEOItem.css";

export default function NEOItem({ neo, units }) {
  const [estimatedDiameter, setEstimatedDiameter] = useState({ min: "", max: "", unit: "" });

  const {
    close_approach_data: closeApproachData,
    estimated_diameter,
  } = neo;

  useEffect(() => {
    let key = "";
    let unit = "";

    if (units === "metric") {
      // use kilometers if asteroid is larger than 1km
      if (estimated_diameter.kilometers.estimated_diameter_max > 1) {
        key = "kilometers";
        unit = "km";
      } else {
        key = "meters";
        unit = "m";
      }
    } else {
      // use miles if asteroid is larger than 1mi
      if (estimated_diameter.miles.estimated_diameter_max > 1) {
        key = "miles";
        unit = "mi";
      } else {
        key = "feet";
        unit = "ft";
      }
    }

    setEstimatedDiameter({
      min: estimated_diameter[key].estimated_diameter_min.toLocaleString(),
      max: estimated_diameter[key].estimated_diameter_max.toLocaleString(),
      unit,
    });

  }, [units]);

  return (
    <div className="item">
      <h3 className="item-name">
        <a href={neo.nasa_jpl_url} target="_blank" rel="noopener noreferrer">{neo.name}</a>
        {neo.is_potentially_hazardous_asteroid && <span role="img" aria-label="warning sign"> ⚠️</span>}
      </h3>
      <p className="item-property">
        <strong>diameter</strong>:&nbsp;
        {estimatedDiameter.min} - {estimatedDiameter.max} {estimatedDiameter.unit}
      </p>
      <p className="item-property">
        <strong>closest distance</strong>:&nbsp;
        {units === "metric"
          ? `${Number(closeApproachData[0].miss_distance.kilometers).toLocaleString()} km`
          : `${Number(closeApproachData[0].miss_distance.miles).toLocaleString()} mi`}
      </p>
      <p className="item-property">
        <strong>relative velocity</strong>:&nbsp;
        {units === "metric"
          ? `${Number(closeApproachData[0].relative_velocity.kilometers_per_hour).toLocaleString()} kph`
          : `${Number(closeApproachData[0].relative_velocity.miles_per_hour).toLocaleString()} mph`}
      </p>
    </div>
  )
}
