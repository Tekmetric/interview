import styles from "./loader.module.css";

export default function Loader({ text }) {
  return (
    <div className={styles.loader}>
      <div className={styles.spinner}></div>
      <div className={styles.text}>{text}</div>
    </div>
  );
}
