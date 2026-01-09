import {Route, Routes} from "react-router";
import LoginPage from "./components/LoginPage";
import RegisterPage from "./components/RegisterPage.tsx";
import AuthProvider from "./components/AuthProvider.tsx";
import {PublicDocumentsPage} from "./components/PublicDocumentsPage.tsx";
import DocumentDetailPage from "./components/DocumentDetailPage.tsx";
import {CreateDocument} from "./components/CreateDocument.tsx";
import Navbar from "./components/Navbar.tsx";
import UserDocumentsPage from "./components/UserDocumentsPage.tsx";


function App() {
    return (
        <AuthProvider>
            <Routes>
                <Route path="login" element={<LoginPage />} />
                <Route path="register" element={<RegisterPage />} />
                <Route path="documents">
                    <Route path="public" element={<PublicDocumentsPage />} />
                    <Route path=":documentId" element={<DocumentDetailPage />} />
                    <Route path="create" element={<CreateDocument /> } />
                </Route>
                <Route path="users">
                    <Route path="my-documents" element={<UserDocumentsPage />} />
                </Route>
                <Route path="*" element={<> <Navbar /> <div>Not Found</div> </>} />
            </Routes>
        </AuthProvider>
    );
}

export { App };
