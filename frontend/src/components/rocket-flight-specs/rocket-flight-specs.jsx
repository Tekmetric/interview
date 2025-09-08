import styles from "./rocket-flight-specs.module.css";

export default function RocketFlightSpecs({ rocket }) {
  return (
    <div className={styles.rocketSpecs}>
      <div>
        <h3>First Stage</h3>
        <p>
          <strong>Reusable:</strong>{" "}
          {rocket.first_stage.reusable ? "Yes" : "No"}
        </p>
        <p>
          <strong>Engines:</strong> {rocket.first_stage.engines} engines
        </p>
        <p>
          <strong>Fuel Amount Tons:</strong>{" "}
          {rocket.first_stage.fuel_amount_tons} fuel amount tons
        </p>
        <p>
          <strong>Burn Time Seconds:</strong> {rocket.first_stage.burn_time_sec}{" "}
          burn time seconds
        </p>
      </div>
      <div>
        <h3>Second Stage</h3>
        <p>
          <strong>Reusable:</strong>{" "}
          {rocket.second_stage.reusable ? "Yes" : "No"}
        </p>
        <p>
          <strong>Engines:</strong> {rocket.second_stage.engines} engines
        </p>
        <p>
          <strong>Fuel Amount Tons:</strong>{" "}
          {rocket.second_stage.fuel_amount_tons} fuel amount tons
        </p>
        <p>
          <strong>Burn Time Seconds:</strong>{" "}
          {rocket.second_stage.burn_time_sec} burn time seconds
        </p>
      </div>
    </div>
  );
}
