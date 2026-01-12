// @vitest-environment jsdom
import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import SearchBar from "./Searchbar";
import "@testing-library/jest-dom";

describe("SearchBar", () => {
    it("rendert Input und Button", () => {
        render(
            <SearchBar value="" onChange={() => {}} onSubmit={() => {}} />
        );

        expect(screen.getByPlaceholderText("Eingabe...")).toBeInTheDocument();
        expect(screen.getByRole("button", { name: /suchen/i })).toBeInTheDocument();
    });

    it("ruft onChange auf, wenn getippt wird", async () => {
        const user = userEvent.setup();
        const onChange = vi.fn();

        render(
            <SearchBar value="" onChange={onChange} onSubmit={() => {}} />
        );

        const input = screen.getByPlaceholderText("Eingabe...");
        await user.type(input, "test");

        // wird für jedes Zeichen aufgerufen
        expect(onChange).toHaveBeenCalled();
    });

    it("ruft onSubmit auf bei gültiger Eingabe", async () => {
        const user = userEvent.setup();
        const onSubmit = vi.fn();

        render(
            <SearchBar value="hello" onChange={() => {}} onSubmit={onSubmit} />
        );

        const button = screen.getByRole("button", { name: /suchen/i });
        await user.click(button);

        expect(onSubmit).toHaveBeenCalledTimes(1);
    });

});
