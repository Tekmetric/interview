import styles from "./rockets-list.module.css";
import RocketScene from "../rocket-scene";

export default function Rockets({ rockets }) {
  return (
    <ul className={styles.rockets}>
      {rockets.map((rocket) => (
        <RocketScene key={rocket.id} rocket={rocket} />
      ))}
    </ul>
  );
}
