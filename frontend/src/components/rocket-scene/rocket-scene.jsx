import styles from "./rocket-scene.module.css";

export default function RocketScene({ rocket }) {
  return (
    <li className={styles.rocketScene} id={rocket.id}>
      <h3>{rocket.name}</h3>
    </li>
  );
}
