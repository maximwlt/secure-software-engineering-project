import {useNavigate} from "react-router";

/**
 * A simple back button component that either uses a provided onBack function or defaults to navigating back in history.
 * @param onBack Optional callback function to execute when the back button is clicked. If not provided, it will navigate back using the browser history.
 * @constructor
 */
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