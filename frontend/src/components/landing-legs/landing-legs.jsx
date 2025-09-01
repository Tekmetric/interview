import styles from "./landing-legs.module.css";

export default function LandingLegs({ rocketState }) {
  const legClass = rocketState
    ? `${styles.leg} ${styles[rocketState]}`
    : styles.leg;
  const standClass = rocketState
    ? `${styles.stand} ${styles[rocketState]}`
    : styles.stand;

  return (
    <>
      <div className={legClass + " " + styles.leg1}></div>
      <div className={legClass + " " + styles.leg2}></div>
      <div className={legClass + " " + styles.leg3}></div>
      <div className={standClass + " " + styles.stand1}></div>
      <div className={standClass + " " + styles.stand2}></div>
      <div className={standClass + " " + styles.stand3}></div>
    </>
  );
}
