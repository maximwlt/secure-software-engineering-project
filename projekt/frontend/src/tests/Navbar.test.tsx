import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router";
import Navbar from "../components/Navbar.tsx";
import { vi, describe, it, expect } from "vitest";
import { useAuth } from "../shared/utils/useAuth.ts";

vi.mock("../utils/useAuth");
const mockedUseAuth = vi.mocked(useAuth);

describe("Navbar", () => {
    it("shows User Navigation when authenticated", () => {
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
        expect(screen.getByText("Create")).toBeInTheDocument();
        expect(screen.getByText("My Documents")).toBeInTheDocument();
        expect(screen.getByText("Profile")).toBeInTheDocument();
        expect(screen.queryByText("Login")).not.toBeInTheDocument();
        expect(screen.queryByText("Register")).not.toBeInTheDocument();
    });

    it("shows Login and Registration when NOT authenticated", () => {
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
        expect(screen.getByText("Register")).toBeInTheDocument();
        expect(screen.queryByText("Create")).not.toBeInTheDocument();
        expect(screen.queryByText("My Documents")).not.toBeInTheDocument();
    });
});