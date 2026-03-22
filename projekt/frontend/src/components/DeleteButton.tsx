import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCircleExclamation, faTrash} from "@fortawesome/free-solid-svg-icons";
import {useState} from "react";

function DeleteButton({ onDeleteClick, title = "Delete", message = "Are you sure? This action cannot be undone." }: { onDeleteClick: () => void, title?: string, message?: string }) {
    const [showModal, setShowModal] = useState(false);

    const handleConfirmDelete = () => {
        setShowModal(false)
        onDeleteClick();
    }

    return (
        <>
            <button data-model-target="popup-modal"
                    onClick={() : void => setShowModal(true)}
                    className="mt-8 px-6 py-3 bg-red-500 hover:bg-red-600 text-white font-semibold rounded-md cursor-pointer transition-colors"
            >
                <FontAwesomeIcon icon={faTrash} /> Delete
            </button>

        {showModal && (
            <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
                <div className="bg-white rounded-xl shadow-xl p-8 max-w-md w-full mx-4">
                    <div className="flex items-center gap-3 mb-4">
                        <FontAwesomeIcon icon={faCircleExclamation} className="text-red-700 text-xl" />
                        <h2 className="text-lg font-bold text-gray-900">{title}</h2>
                    </div>
                    <p className="text-gray-600 mb-6">
                        {message}
                    </p>
                    <div className="flex justify-between gap-3">
                        <button
                            onClick={handleConfirmDelete}
                            className="px-4 py-2 rounded-md bg-red-500 hover:bg-red-600 text-white font-semibold cursor-pointer transition-colors"
                        >
                            <FontAwesomeIcon icon={faTrash} /> Delete
                        </button>
                        <button
                            onClick={() => setShowModal(false)}
                            className="px-4 py-2 rounded-md border border-gray-300 text-gray-700 hover:bg-gray-100 font-semibold cursor-pointer transition-colors"
                        >
                            Cancel
                        </button>

                    </div>
                </div>
            </div>
        )}
        </>
    );

}

export default DeleteButton;