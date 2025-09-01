import { useState, useEffect } from "react";

export const useRockets = () => {
  const [rockets, setRockets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [maxRocketDimensions, setMaxRocketDimensions] = useState(undefined);

  useEffect(() => {
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
        setLoading(false);
      } catch (err) {
        setError(err.message);
      }
    };

    fetchRockets();
  }, []);

  return {
    rockets,
    loading,
    error,
    maxRocketDimensions,
  };
};
