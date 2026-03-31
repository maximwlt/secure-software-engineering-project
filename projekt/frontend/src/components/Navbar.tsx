import { NavLink } from "react-router";
import { useAuth } from "../utils/useAuth";
import {faCircleUser, faFileCirclePlus, faGlobe, faUserLock} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

function Navbar() {
    const { isAuthenticated } = useAuth();

    const navLinkClass = ({ isActive }: { isActive: boolean }) =>
        `px-4 py-2 font-medium rounded-md border transition-all duration-200 text-sm
    ${isActive
            ? 'bg-blue-50 text-blue-500 border-blue-500'
            : 'text-gray-500 border-transparent hover:bg-gray-100 hover:text-gray-900'
        }`;

    return (
        <nav className="bg-white border-b border-gray-200 shadow-sm sticky top-0 z-1000">
            <div className="max-w-350 mx-auto px-8 py-4 flex justify-between items-center">
                <div className="flex gap-2 items-center">
                    <NavLink to="/documents/public"
                        className={navLinkClass}
                    >
                        <><FontAwesomeIcon icon={faGlobe} /> All Documents</>
                    </NavLink>

                    {isAuthenticated ? (
                        <>
                            <NavLink
                                to="/documents/create"
                                className={navLinkClass}
                            >
                                <><FontAwesomeIcon icon={faFileCirclePlus} /> Create</>
                            </NavLink>

                            <NavLink
                                to="/users/my-documents"
                                className={navLinkClass}
                            >
                                <><FontAwesomeIcon icon={faUserLock} /> My Documents</>
                            </NavLink>

                            <NavLink
                                to="/login"
                                className={navLinkClass}
                            >
                                <><FontAwesomeIcon icon={faCircleUser} /> Profile</>
                            </NavLink>
                        </>
                    ) : (
                        <>
                            <NavLink
                                to="/login"
                                className={navLinkClass}
                            >
                                Login
                            </NavLink>
                            <NavLink
                                to="/register"
                                className="px-4 py-2 font-medium rounded-md border text-sm text-white bg-blue-500 border-blue-500 transition-all duration-200 hover:bg-blue-700 hover:border-blue-700 [&.active]:bg-blue-800 [&.active]:border-blue-800"
                            >
                                Register
                            </NavLink>
                        </>
                    )}
                </div>
            </div>
        </nav>
    );
}

export default Navbar;