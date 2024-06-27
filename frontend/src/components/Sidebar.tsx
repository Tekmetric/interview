import React, { useEffect } from 'react';

interface SidebarProps {
  categories: string[];
  selectedCategory: string;
  onSelectCategory: (category: string) => void;
}

const Sidebar: React.FC<SidebarProps> = ({ categories, selectedCategory, onSelectCategory }) => {
  useEffect(() => {
    if (!selectedCategory && categories.length > 0) {
      onSelectCategory(categories[0]); // Select the first category by default
    }
  }, [categories, selectedCategory, onSelectCategory]);

  return (
    <div className="sidebar">
      <ul className="category-list">
        {categories.map((category) => (
          <li 
            key={category} 
            className={`category-item ${selectedCategory === category ? 'selected' : ''}`}
            onClick={() => onSelectCategory(category)}
          >
            {category}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Sidebar;
