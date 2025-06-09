import React, { useState } from 'react';

type ExpandableTextProps = {
  text: string;
  maxLength?: number;
};

export const ExpandableText = ({ text, maxLength = 100 }: ExpandableTextProps) => {
  const [isExpanded, setIsExpanded] = useState(false);

  if (!text || text.length <= maxLength) {
    return <div className="whitespace-pre-wrap">{text}</div>;
  }

  const toggleExpand = (e: React.MouseEvent) => {
    e.stopPropagation();
    setIsExpanded(!isExpanded);
  };

  return (
    <div className="relative">
      <div className={`whitespace-pre-wrap ${!isExpanded ? 'line-clamp-1' : ''}`}>{text}</div>
      <button
        onClick={toggleExpand}
        className="text-blue-600 hover:text-blue-800 font-medium text-xs mt-1 focus:outline-none"
      >
        {isExpanded ? 'Show less' : 'Show more...'}
      </button>
    </div>
  );
};
