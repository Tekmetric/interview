import React, { Component } from "react";
import { fetchNEOs } from "./utils";
import NEOList from "./components/NEOList";

// Normally I would convert this to a function component for consistency,
// but leaving it as a class to demonstrate I know how to use them as well. :^}
class App extends Component {
  constructor(props) {
    super(props);

    const todayDate = new Date();
    // Account for timezone when setting today's date.
    const offset = todayDate.getTimezoneOffset() * 60 * 1000;
    this.today = new Date(todayDate.getTime() - offset).toISOString().split("T")[0];

    this.state = {
      date: this.today,
      neoData: [],
      units: "metric",
      isLoading: true,
      error: "",
    }

    this.updateData = this.updateData.bind(this);
    this.handleDateChange = this.handleDateChange.bind(this);
  }

  async componentDidMount() {
    await this.updateData();
  }

  async componentDidUpdate(_, prevState) {
    if (this.state.date !== prevState.date) {
      await this.updateData();
    }
  }

  async updateData() {
    this.setState({ isLoading: true });

    const neoData = await fetchNEOs(this.state.date);
    if (typeof neoData === "string") {
      this.setState({ error: neoData });
    } else {
      this.setState({ neoData });
    }

    this.setState({ isLoading: false });
  }

  handleDateChange(e) {
    const date = e.target.value;
    this.setState({ date });
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
          <label htmlFor="date">Date:</label>
          <input
            type="date"
            id="date"
            name="date"
            value={this.state.date}
            max={this.today}
            onChange={this.handleDateChange}
          />

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
          <NEOList
            isLoading={this.state.isLoading}
            error={this.state.error}
            neoData={this.state.neoData}
            units={this.state.units}
          />
        </main>
      </div>
    );
  }
}

export default App;
