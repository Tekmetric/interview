import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./views/login/login";

const App = () => {
  return (
    <div className="bg-slate-100 h-screen">
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Login />} />
        </Routes>
      </BrowserRouter>
    </div>
  );
};

export default App;
