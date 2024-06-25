import React, { useState, useEffect } from "react";
import "./NEOItem.css";

export default function NEOItem({ neo, units }) {
  const [estimatedDiameter, setEstimatedDiameter] = useState({ min: "", max: "", unit: "" });
  const [closeApproachDistance, setCloseApproachDistance] = useState({ value: "", unit: "" });
  const [closeApproachVelocity, setCloseApproachVelocity] = useState({ value: "", unit: "" });

  const {
    close_approach_data,
    estimated_diameter,
  } = neo;

  useEffect(() => {
    let diameterKey = "";
    let diameterUnit = "";

    let distanceKey = "";
    let distanceUnit = "";

    let velocityKey = "";
    let velocityUnit = "";

    if (units === "metric") {
      // use kilometers if asteroid is larger than 1km
      if (estimated_diameter.kilometers.estimated_diameter_max > 1) {
        diameterKey = "kilometers";
        diameterUnit = "km";
      } else {
        diameterKey = "meters";
        diameterUnit = "m";
      }

      distanceKey = "kilometers";
      distanceUnit = "km";

      velocityKey = "kilometers_per_hour";
      velocityUnit = "kph;"
    } else {
      // use miles if asteroid is larger than 1mi
      if (estimated_diameter.miles.estimated_diameter_max > 1) {
        diameterKey = "miles";
        diameterUnit = "mi";
      } else {
        diameterKey = "feet";
        diameterUnit = "ft";
      }

      distanceKey = "miles";
      distanceUnit = "mi";

      velocityKey = "miles_per_hour";
      velocityUnit = "mph";
    }

    setEstimatedDiameter({
      min: estimated_diameter[diameterKey].estimated_diameter_min.toLocaleString(),
      max: estimated_diameter[diameterKey].estimated_diameter_max.toLocaleString(),
      unit: diameterUnit,
    });

    setCloseApproachDistance({
      value: Number(close_approach_data[0].miss_distance[distanceKey]).toLocaleString(),
      unit: distanceUnit,
    });

    setCloseApproachVelocity({
      value: Number(close_approach_data[0].relative_velocity[velocityKey]).toLocaleString(),
      unit: velocityUnit,
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
        {closeApproachDistance.value} {closeApproachDistance.unit}
      </p>
      <p className="item-property">
        <strong>relative velocity</strong>:&nbsp;
        {closeApproachVelocity.value} {closeApproachVelocity.unit}
      </p>
    </div>
  )
}
