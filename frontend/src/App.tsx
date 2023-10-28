import React, {Suspense} from 'react';
import {Header} from './components';
import {Navigate, Route, Routes} from "react-router-dom";
import {PageFactory} from "./pages";

const Pokedex = React.lazy(() => PageFactory.Pokedex());
const Pokemon = React.lazy(() => PageFactory.Pokemon());

const App = () => {
    return (
        <main>
            <Header/>
            <Suspense>
                <Routes>
                    <Route path={"/"} element={<Navigate to={"/pokemon"} replace />} />
                    <Route path={"/pokemon"}>
                        <Route index element={<Pokedex />} />
                        <Route path={"/pokemon/:id"} element={<Pokemon />} />
                    </Route>
                </Routes>
            </Suspense>
        </main>
    )
}

export default App;
