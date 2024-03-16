import React, { Component } from "react";
import { fetchNEOs } from "./utils";
import NEOList from "./components/NEOList";
import Menu from "./components/Menu";
import "./App.css";

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
    this.handleUnitChange = this.handleUnitChange.bind(this);
    this.resetDate = this.resetDate.bind(this);
  }

  async componentDidMount() {
    // Could cache this first load of data for use with "reset to today"
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

  handleUnitChange(e) {
    const units = e.target.value;
    this.setState({ units });
  }

  resetDate() {
    this.setState({ date: this.today });
  }
  
  render() {
    return (
      <div className="app">
        <header className="header">
          <h1 className="title">Near Earth Object (NEO) Viewer</h1>
          <h2 className="subtitle">View by date asteroids that have come within 1.3 AU* of Earth.</h2>
          <p className="definition">*1.3 AU = 1.3 Astronomical Unit ≈ {this.state.units === "metric" ? "194,477,232 km" : "120,842,549 mi"}</p>
        </header>
        
        <Menu
          date={this.state.date}
          today={this.today}
          handleDateChange={this.handleDateChange}
          units={this.state.units}
          resetDate={this.resetDate}
          handleUnitChange={this.handleUnitChange}
        />

        
        <NEOList
          isLoading={this.state.isLoading}
          error={this.state.error}
          neoData={this.state.neoData}
          units={this.state.units}
        />
      </div>
    );
  }
}

export default App;
