import {Route, Routes} from "react-router";
import LoginPage from "./components/LoginPage";
import RegisterPage from "./components/RegisterPage.tsx";
import AuthProvider from "./components/AuthProvider.tsx";
import {PublicDocumentsPage} from "./components/PublicDocumentsPage.tsx";
import DocumentDetailPage from "./components/DocumentDetailPage.tsx";
import Navbar from "./components/Navbar.tsx";
import UserDocumentsPage from "./components/UserDocumentsPage.tsx";
import {PWResetEmail} from "./components/PWResetEmail.tsx";
import {PWResetPassword} from "./components/PWResetPassword.tsx";
import {Profile} from "./components/Profile.tsx";
import {UpdateDocument} from "./components/UpdateDocument.tsx";
import {DocumentForm} from "./components/DocumentForm.tsx";
import {PrivateChat} from "./components/PrivateChat.tsx";


function App() {
    return (
        <AuthProvider>
            <Routes>
                <Route path="login" element={<LoginPage />} />
                <Route path="register" element={<RegisterPage />} />
                <Route path="forgot-password" element={<PWResetEmail /> } />
                <Route path="reset-password" element={<PWResetPassword />} />

                <Route path="documents">
                    <Route path="public" element={<PublicDocumentsPage />} />
                    <Route path=":documentId" element={<DocumentDetailPage />} />
                    <Route path="create" element={<> <Navbar /> <DocumentForm isEdit={false} /> </> } />
                    <Route path=":documentId/edit" element={<UpdateDocument />} />
                </Route>
                <Route path="users">
                    <Route path="my-documents" element={<UserDocumentsPage />} />
                    <Route path="profile" element={<Profile />} />
                </Route>
                <Route path="*" element={<> <Navbar /> <div>Main Page</div> </>} />
                <Route path="websocket" element={<PrivateChat />} />
            </Routes>
        </AuthProvider>
    );
}
// TODO: Add a main page with some welcome text and links to public documents, login, etc.
export { App };
