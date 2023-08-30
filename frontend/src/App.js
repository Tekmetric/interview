import {Route, Routes} from 'react-router-dom';
import Player from "./components/Player/Player";
import Header from "./components/Header/Header";
import Details from "./components/Player/Details/Details";
import PageNotFound from "./components/PageNotFound";

function App() {
    return (
        <div className="App">
            <Header></Header>
            <Routes>
                <Route exact path="/" element={<Player/>}></Route>
                <Route path="/details" element={<Details/>}></Route>
                <Route element={<PageNotFound/>}></Route>
            </Routes>
        </div>
    );
}

export default App;
