/**
 * A simple loading bar component that can be used to indicate that documents are being loaded.
 */
function LoadingBar() {
    return (
        <div className="max-w-350 mx-auto px-4 py-16 flex flex-col items-center gap-4">
            <div className="w-full max-w-md h-1.5 bg-gray-200 rounded-full overflow-hidden">
                <div className="h-full w-1/3 bg-blue-500 rounded-full animate-loading" />
            </div>
            <span className="text-sm text-gray-400">Loading...</span>
        </div>
    );
}

export default LoadingBar;