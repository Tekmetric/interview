import styles from "./sky.module.css";

export default function Sky() {
  return (
    <div className={styles.sky}>
      <div className={styles.stars}></div>
      <div className={styles.stars1}></div>
      <div className={styles.stars2}></div>
      <div className={styles.shootingStar}></div>
    </div>
  );
}
