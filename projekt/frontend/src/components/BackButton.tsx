import {useNavigate} from "react-router";

function BackButton({onBack}: { onBack?: () => void }) {
    const navigate = useNavigate();
    const handleBack = onBack ?? (() => navigate(-1));

    return (
        <button onClick={handleBack} className="text-blue-500 hover:text-blue-700 hover:underline font-semibold cursor-pointer">
            ← Back to Overview
        </button>
    );
}

export default BackButton;