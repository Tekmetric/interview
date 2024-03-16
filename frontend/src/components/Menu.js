import React from "react";
import "./Menu.css";

export default function Menu({
  date,
  today,
  handleDateChange,
  resetDate,
  units,
  handleUnitChange
}) {
  return (
    <section className="menu">
      <div>
        <label htmlFor="date" className="date-label">Date:</label>
        <input
          type="date"
          id="date"
          name="date"
          value={date}
          max={today}
          onChange={handleDateChange}
          className="date-input"
        />
        <button type="button" onClick={resetDate}>Reset to today</button>
      </div>

      <fieldset className="units">
        <legend align="center">Units</legend>
        <input
          type="radio"
          id="metric"
          name="metric"
          value="metric"
          checked={units === "metric"}
          onChange={handleUnitChange}
        />
        <label htmlFor="metric">Metric</label>

        <input
          type="radio"
          id="imperial"
          name="imperial"
          value="imperial"
          checked={units === "imperial"}
          onChange={handleUnitChange}
        />
        <label htmlFor="imperial">Imperial</label>
      </fieldset>
    </section>
  )
}