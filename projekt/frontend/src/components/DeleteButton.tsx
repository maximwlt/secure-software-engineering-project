import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTrash} from "@fortawesome/free-solid-svg-icons";

function DeleteButton({ onDeleteClick }: { onDeleteClick: () => void }) {
    return (
        <button onClick={onDeleteClick} className="mt-8 px-6 py-3 bg-red-500 hover:bg-red-600 text-white font-semibold rounded-md cursor-pointer transition-colors">
            <FontAwesomeIcon icon={faTrash} /> Delete
        </button>
    );
}

export default DeleteButton;