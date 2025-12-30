import {Route, Routes} from "react-router";
import LoginPage from "./components/LoginPage";
import RegisterPage from "./components/RegisterPage.tsx";


function App() {
    return (
        <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

        </Routes>
    );
}

export { App };
