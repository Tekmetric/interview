import React, { Component } from "react";
import { fetchNEOs } from "./utils";

 
class App extends Component {
  constructor(props) {
    super(props);

    const today = new Date();
    // Account for timezone when setting today's date.
    const offset = today.getTimezoneOffset() * 60 * 1000;

    this.state = {
      date: new Date(today.getTime() - offset).toISOString().split("T")[0],
      neoData: [],
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
          <h2>View by date asteroids that have come within 1.3 AU of Earth.</h2>
        </header>
        <main>
        </main>
      </div>
    );
  }
}

export default App;
