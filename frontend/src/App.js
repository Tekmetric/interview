import React, { Component } from "react";
import { fetchNEOs } from "./utils";
import NEOItem from "./components/NEOItem";

 
class App extends Component {
  constructor(props) {
    super(props);

    const today = new Date();
    // Account for timezone when setting today's date.
    const offset = today.getTimezoneOffset() * 60 * 1000;

    this.state = {
      date: new Date(today.getTime() - offset).toISOString().split("T")[0],
      neoData: [],
      units: "metric",
    }
  }

  async componentDidMount() {
    const neoData = await fetchNEOs(this.state.date);
    this.setState({ neoData });
  }
  
  render() {
    return (
      <div className="App">
        <header className="App-header">
          <h1>NEO Viewer</h1>
          <h2>View by date asteroids that have come within 1.3 AU* of Earth.</h2>
          <p>*1 AU = 1 Astronomical Unit ≈ {this.state.units === "metric" ? "149,597,871 km" : "92,955,807 mi"}</p>
        </header>
        <section>
          {/* date selection */}
          <fieldset>
            <legend>Units</legend>
            <input
              type="radio"
              id="metric"
              name="metric"
              value="metric"
              checked={this.state.units === "metric"}
              onChange={() => this.setState({ units: "metric" })}
            />
            <label htmlFor="metric">Metric</label>

            <input
              type="radio"
              id="imperial"
              name="imperial"
              value="imperial"
              checked={this.state.units === "imperial"}
              onChange={() => this.setState({ units: "imperial" })}
            />
            <label htmlFor="imperial">Imperial</label>
          </fieldset>
        </section>
        <main>
          {this.state.neoData.map((neo) => (
            <NEOItem key={neo.id} neo={neo} units={this.state.units} />
          ))}
        </main>
      </div>
    );
  }
}

export default App;
