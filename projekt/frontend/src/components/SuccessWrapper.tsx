import React from "react";

/**
 * A wrapper component for displaying success messages and a button to proceed.
 */
interface Props {
    children: React.ReactNode;
    buttonLabel: string;
    onButtonClick: () => void;
}

/**
 * SuccessWrapper is a reusable component that displays a success message (passed as children) and a button with a customizable label and click handler.
 * It is styled to provide a visually distinct success message area and a prominent button for user interaction.
 * @param children The HTML content to be displayed as the success message.
 * @param buttonLabel The label for the button that will be displayed below the success message.
 * @param onButtonClick The callback function that will be executed when the button is clicked.
 * @constructor
 */
export function SuccessWrapper({children, buttonLabel, onButtonClick} : Props) {

    return (
        <div className="max-w-2xl mx-auto mt-12 px-8 text-center">
            <div className="bg-green-50 border-2 border-green-500 rounded-xl p-8 mb-8">
                {children}
            </div>

            <button
                className="w-full py-3 text-base font-semibold bg-blue-500 hover:bg-blue-600 text-white rounded-md cursor-pointer transition-colors"
                onClick={onButtonClick}
            >
                {buttonLabel}
            </button>
        </div>
    );

}