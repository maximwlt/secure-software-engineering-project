import { NavLink } from "react-router";
import { useAuth } from "../utils/useAuth";
import '../styling/Navbar.css';

function Navbar() {
    const { isAuthenticated } = useAuth();

    return (
        <nav className="navbar">
            <div className="navbar-container">
                <div className="navbar-links">
                    <NavLink
                        to="/documents/public"
                        className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}
                    >
                        Öffentliche Dokumente
                    </NavLink>

                    {isAuthenticated ? (
                        <>
                            <NavLink
                                to="/documents/create"
                                className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}
                            >
                                Erstellen
                            </NavLink>
                            <NavLink
                                to="/login"
                                className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}
                            >
                                Profil
                            </NavLink>

                            <NavLink
                                to="/users/my-documents"
                                className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}
                            >
                                Meine Dokumente
                            </NavLink>
                        </>
                    ) : (
                        <>
                            <NavLink
                                to="/login"
                                className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}
                            >
                                Login
                            </NavLink>
                            <NavLink
                                to="/register"
                                className="nav-link nav-link-primary"
                            >
                                Registrieren
                            </NavLink>
                        </>
                    )}
                </div>
            </div>
        </nav>
    );
}

export default Navbar;