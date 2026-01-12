import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router";
import Navbar from "../components/Navbar.tsx";
import { vi, describe, it, expect } from "vitest";
import { useAuth } from "../utils/useAuth.ts";

// useAuth mocken
vi.mock("../utils/useAuth");
const mockedUseAuth = vi.mocked(useAuth);

describe("Navbar", () => {
    it("zeigt Benutzer-Navigation wenn eingeloggt", () => {
        mockedUseAuth.mockReturnValue({
            isAuthenticated: true,
            token: "some-token",
            login: vi.fn(),
            logout: vi.fn(),
            refreshAccessToken: vi.fn(),
        });

        render(
            <MemoryRouter>
                <Navbar />
            </MemoryRouter>
        );

        expect(screen.getByText("Erstellen")).toBeInTheDocument();
        expect(screen.getByText("Meine Dokumente")).toBeInTheDocument();
        expect(screen.getByText("Profil")).toBeInTheDocument();
        expect(screen.queryByText("Login")).not.toBeInTheDocument();
        expect(screen.queryByText("Registrieren")).not.toBeInTheDocument();
    });

    it("zeigt Login & Registrieren wenn NICHT eingeloggt", () => {
        mockedUseAuth.mockReturnValue({
            isAuthenticated: false,
            token: null,
            login: vi.fn(),
            logout: vi.fn(),
            refreshAccessToken: vi.fn(),
        });

        render(
            <MemoryRouter>
                <Navbar />
            </MemoryRouter>
        );

        expect(screen.getByText("Login")).toBeInTheDocument();
        expect(screen.getByText("Registrieren")).toBeInTheDocument();
        expect(screen.queryByText("Erstellen")).not.toBeInTheDocument();
        expect(screen.queryByText("Meine Dokumente")).not.toBeInTheDocument();
    });
});
