import styles from "./rocket-scene.module.css";
import RocketModel from "../rocket-model";

export default function RocketScene({ rocket }) {
  return (
    <li className={styles.rocketScene} id={rocket.id}>
      <h2>{rocket.name}</h2>
      <RocketModel
        rocketState={"idle"}
        height={rocket.height.meters}
        width={rocket.diameter.meters}
      />
    </li>
  );
}
