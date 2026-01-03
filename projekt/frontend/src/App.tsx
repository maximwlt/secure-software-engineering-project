import {Route, Routes} from "react-router";
import LoginPage from "./components/LoginPage";
import RegisterPage from "./components/RegisterPage.tsx";
import AuthProvider from "./components/AuthProvider.tsx";
import {PublicDocumentsPage} from "./components/PublicDocumentsPage.tsx";
import {DocumentDetailPage} from "./components/DocumentDetailPage.tsx";
import {CreateDocument} from "./components/CreateDocument.tsx";


function App() {
    return (
        <AuthProvider>
            <Routes>
                <Route path="login" element={<LoginPage />} />
                <Route path="register" element={<RegisterPage />} />
                <Route path="documents">
                    <Route path="public" element={<PublicDocumentsPage />} />
                    <Route path="search" element={<div>Search Documents Page</div>} />
                    <Route path=":documentId" element={<DocumentDetailPage />} />
                    <Route path="create" element={<CreateDocument /> } />
                </Route>
                <Route path="users">
                    <Route path="me" element={<div>My Profile Page</div>} />
                    <Route path="my-documents" element={<div>My Documents Page</div>} />
                    { /* <Route path="search" element={<div>Search Users Page</div>} /> */}
                </Route>
                <Route path="*" element={<div>404 Not Found</div>} />
            </Routes>
        </AuthProvider>
    );
}

export { App };
