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
    console.log(this.state.neoData)
    return (
      <div className="App">
        <header className="App-header">
          <h1>NEO Viewer</h1>
          <h2>View by date asteroids that have come within 1.3 AU* of Earth.</h2>
          <p>*1 AU = 1 Astronomical Unit ≈ {this.state.units === "metric" ? "149,597,871 km" : "92,955,807 mi"}</p>
        </header>
        <section>
          {/* date selection */}
          {/* unit selection */}
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
