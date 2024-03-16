import React from "react";
import NEOItem from "./NEOItem";
import "./NEOList.css";

export default function NEOList({ isLoading, error, neoData, units }) {
  if (error) {
    return (
      <p className="error">
        Oops! Something went wrong. :(
        <br />
        Message: {error}
      </p>
    );
  }

  if (isLoading) {
    return <p>Loading...</p>
  }

  if (neoData.length === 0) {
    return <p>No asteroids found for selected date.</p>
  }

  return (
    <>
      <main className="list">
        {neoData.map((neo) => (
          <NEOItem key={neo.id} neo={neo} units={units} />
        ))}
      </main>
      <p className="total">
        <strong>total</strong>: {neoData.length} asteroids
      </p>
      <p className="pha">
        <span role="img" aria-label="warning sign">⚠️</span> = "potentially hazardous asteroid", meaning it comes within 0.05 AU{" "}
        ({units === "metric" ? "~7,480,000 km" : "~4,650,000 mi"}) and is larger than{" "}
        about {units === "metric" ? "140 m" : "500 ft"} in diameter.
      </p>
    </>
  )
}