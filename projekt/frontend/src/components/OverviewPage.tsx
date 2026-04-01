import type {OverviewPageProps} from "../shared/types/OverviewPageProps.ts";

function OverviewPage({documents, onCardClick}: OverviewPageProps) {
    return (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 md:gap-4 mt-8">
            {documents.map((doc) => (
                <div
                    key={doc.noteId}
                    className="group bg-white border border-gray-200 rounded-xl p-6 md:p-5 sm:p-4 cursor-pointer transition-all duration-300 shadow-sm hover:-translate-y-1 hover:shadow-lg hover:border-blue-500"
                    onClick={() => onCardClick(doc.noteId)}
                >
                    <div className="mb-4 pb-4 border-b-2 border-gray-100">
                        <h2 className="text-xl sm:text-lg font-semibold text-gray-900 leading-snug wrap-break-word">
                            {doc.title}
                        </h2>
                    </div>
                    <div className="flex flex-col gap-3 mb-4">
                        <div className="flex flex-col gap-1">
                            <span className="text-xs font-semibold uppercase text-gray-500 tracking-wide">
                                Document-ID:
                            </span>
                            <span className="text-sm sm:text-[0.85rem] text-gray-700 break-all font-mono">
                                {doc.noteId}
                            </span>
                        </div>
                    </div>

                    <div className="mt-4 pt-4 border-t border-gray-100">
                        <span className="text-sm font-semibold text-blue-500 group-hover:text-blue-700 transition-colors duration-200">
                            View →
                        </span>
                    </div>
                </div>
            ))}
        </div>
    );
}

export default OverviewPage;