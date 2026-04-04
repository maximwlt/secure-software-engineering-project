import {faPenToSquare} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {useNavigate} from "react-router";

function EditButton({docId} : {docId : string | undefined}) {
    const navigate = useNavigate();
    return (
        <button
            onClick={() => navigate(`/documents/${docId}/edit`)}
            className="px-6 py-3 rounded-md bg-orange-400 hover:bg-orange-600 hover:underline text-white font-semibold cursor-pointer">
            <FontAwesomeIcon icon={faPenToSquare} /> Edit
        </button>
    );
}

export default EditButton;