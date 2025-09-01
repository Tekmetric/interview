import styles from "./rocket-scene.module.css";
import RocketModel from "../rocket-model";
import { useState, useEffect } from "react";
import RocketFlightSpecs from "../rocket-flight-specs";
import RocketSpecs from "../rocket-specs";
import Button from "../button";

export default function RocketScene({ rocket }) {
  const [rocketState, setRocketState] = useState("idle");

  const landRocket = () => {
    setRocketState("landing");
    setTimeout(() => {
      setRocketState("idle");
    }, 4000);
  };

  useEffect(() => {
    landRocket();
  }, []);

  return (
    <li className={styles.rocketScene} id={rocket.id}>
      <div>
        <h2 className="rocket-name">{rocket.name}</h2>
        {rocketState === "idle" && (
          <Button onClick={() => setRocketState("takeoff")}>Take Off</Button>
        )}
        {rocketState === "takeoff" && rocket.first_stage.reusable === false && (
          <Button onClick={() => setRocketState("idle")}>
            Get a new Rocket
          </Button>
        )}
        {rocketState === "takeoff" && rocket.first_stage.reusable === true && (
          <Button onClick={() => landRocket()}>Land</Button>
        )}
        {rocketState === "landing" && (
          <span className="landing-text">Landing..</span>
        )}
      </div>
      <div>
        <RocketModel
          rocketState={rocketState}
          landing_legs={rocket.landing_legs}
          height={rocket.height.meters}
          width={rocket.diameter.meters}
        />
      </div>
      <div>
        {rocketState === "idle" && <RocketSpecs rocket={rocket} />}
        {rocketState === "takeoff" && <RocketFlightSpecs rocket={rocket} />}
      </div>
    </li>
  );
}
