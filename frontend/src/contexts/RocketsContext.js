import React, { createContext, useContext, useState } from "react";

const RocketsContext = createContext();

export const useRocketsContext = () => {
  const context = useContext(RocketsContext);
  if (!context) {
    throw new Error("useRocketsContext must be used within a RocketsProvider");
  }
  return context;
};

export const RocketsProvider = ({ children }) => {
  const [rockets, setRockets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [maxRocketDimensions, setMaxRocketDimensions] = useState(undefined);

  const fetchRockets = async () => {
    try {
      setLoading(true);
      const response = await fetch("https://api.spacexdata.com/v4/rockets");
      if (!response.ok) throw new Error("Failed to fetch rockets");
      const data = await response.json();

      if (data.length > 0) {
        const dimensions = {
          height: Math.max(...data.map((rocket) => rocket.height.meters)),
          diameter: Math.max(...data.map((rocket) => rocket.diameter.meters)),
        };

        setMaxRocketDimensions(dimensions);
      }

      setRockets(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const value = {
    rockets,
    loading,
    error,
    maxRocketDimensions,
    fetchRockets,
  };

  return (
    <RocketsContext.Provider value={value}>{children}</RocketsContext.Provider>
  );
};
