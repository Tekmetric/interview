import { useState, useEffect } from "react";

export const useRockets = () => {
  const [rockets, setRockets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchRockets = async () => {
      try {
        setLoading(true);
        const response = await fetch("https://api.spacexdata.com/v4/rockets");
        if (!response.ok) throw new Error("Failed to fetch rockets");
        const data = await response.json();
        setRockets(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchRockets();
  }, []);

  const getMaxRocketHeight = () => {
    if (rockets.length === 0) return 0;
    return Math.max(...rockets.map((rocket) => rocket.height.meters));
  };

  const getMaxRocketDiameter = () => {
    if (rockets.length === 0) return 0;
    return Math.max(...rockets.map((rocket) => rocket.diameter.meters));
  };

  return {
    rockets,
    loading,
    error,
    getMaxRocketHeight,
    getMaxRocketDiameter,
  };
};
