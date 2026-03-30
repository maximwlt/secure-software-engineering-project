import React from "react";

interface Props {
    children: React.ReactNode;
    buttonLabel: string;
    onButtonClick: () => void;
}

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