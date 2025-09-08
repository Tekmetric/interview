import styles from "./rocket-model.module.css";
import { useRocketsContext } from "../../contexts/RocketsContext";
import LandingLegs from "../landing-legs";

export default function RocketModel({
  rocketState,
  height,
  width,
  landing_legs,
  payload,
}) {
  const { maxRocketDimensions } = useRocketsContext();

  if (!maxRocketDimensions) return null;

  const browserHeight = window.innerHeight;
  const maxDisplayHeight = browserHeight * 0.75;
  const heightRatio = height / maxRocketDimensions.height;

  const maxDisplayWidth = 100;
  const diameterRatio = width / maxRocketDimensions.diameter;

  const displayHeight = heightRatio * maxDisplayHeight;
  const displayWidth = diameterRatio * maxDisplayWidth;

  const rocketClass = rocketState
    ? `${styles.rocket} ${styles[rocketState]}`
    : styles.rocket;

  return (
    <div
      className={rocketClass}
      style={{
        height: `${displayHeight}px`,
        width: `${displayWidth}px`,
      }}
    >
      {payload && <div className={styles.payload}></div>}
      <div className={styles.booster}>
        <div className={styles.flame}>
          <div className={styles.fire}></div>
          <div className={styles.shadows}></div>
        </div>
      </div>

      {landing_legs.number > 0 && <LandingLegs rocketState={rocketState} />}
    </div>
  );
}
