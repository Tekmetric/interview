import { useState } from "react";

export default function Drawer({ main, expandedContent }: { main: React.ReactNode; expandedContent: React.ReactNode }) {
    const [expanded, setExpanded] = useState(false);

    const toggleExpanded = () => {
        setExpanded(!expanded);
    }

    return (
        <div className="md:p-4 p-2 border border-gray-300 rounded-md cursor-pointer" onClick={toggleExpanded}>
            <div className="flex md:space-x-2 space-x-1 items-center">
                <div className="flex-1">{main}</div>
                <span className="material-icons-outlined text-gray-500">{expanded ? 'expand_less' : 'expand_more'}</span>
            </div>
            {expanded && <div className="mt-4 pt-4 border-t border-gray-300">{expandedContent}</div>}
        </div>
    )
}