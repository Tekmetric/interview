import React from "react";
import NEOItem from "./NEOItem";

export default function NEOList({ isLoading, error, neoData, units }) {
  if (error) {
    return <p>{error}</p>
  }

  if (isLoading) {
    return <p>Loading...</p>
  }

  if (neoData.length === 0) {
    return <p>No asteroids found for selected date.</p>
  }

  return (
    <>
      {neoData.map((neo) => (
        <NEOItem key={neo.id} neo={neo} units={units} />
      ))}
    </>
  )
}