import styles from "./rocket-specs.module.css";

export default function RocketSpecs({ rocket }) {
  return (
    <div className={styles.rocketSpecs}>
      <p>
        <strong>Height:</strong> {rocket.height.meters}m ({rocket.height.feet}
        ft)
      </p>
      <p>
        <strong>Diameter:</strong> {rocket.diameter.meters}m (
        {rocket.diameter.feet}
        ft)
      </p>
      <p>
        <strong>Mass:</strong> {rocket.mass.kg.toLocaleString()}kg (
        {rocket.mass.lb.toLocaleString()}lb)
      </p>
      <p>
        <strong>Engines:</strong> {rocket.engines.number} {rocket.engines.type}
      </p>
      <p>
        <strong>Reusable:</strong> {rocket.first_stage.reusable ? "Yes" : "No"}
      </p>
      <p>
        <strong>Success Rate:</strong> {rocket.success_rate_pct}%
      </p>
      <p>
        <strong>Cost:</strong> ${rocket.cost_per_launch.toLocaleString()}
      </p>
    </div>
  );
}
