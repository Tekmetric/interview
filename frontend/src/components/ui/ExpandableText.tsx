import React, { useState } from 'react';

type ExpandableTextProps = {
  text: string;
  maxLength?: number;
};

export const ExpandableText: React.FC<ExpandableTextProps> = ({ text, maxLength = 100 }) => {
  const [isExpanded, setIsExpanded] = useState(false);

  // If text is shorter than maxLength, just return it
  if (!text || text.length <= maxLength) {
    return <div className="whitespace-pre-wrap">{text}</div>;
  }

  const toggleExpand = (e: React.MouseEvent) => {
    e.stopPropagation();
    setIsExpanded(!isExpanded);
  };

  return (
    <div className="relative">
      <div className={`whitespace-pre-wrap ${!isExpanded ? 'line-clamp-2' : ''}`}>{text}</div>
      <button
        onClick={toggleExpand}
        className="text-blue-600 hover:text-blue-800 font-medium text-xs mt-1 focus:outline-none"
      >
        {isExpanded ? 'Show less' : 'Show more...'}
      </button>
    </div>
  );
};
