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
    <div className="bg-gray-900 text-white h-full min-h-screen flex flex-col">
      <div className="p-4">
        <h1 className="text-2xl font-bold">Categories</h1>
      </div>
      <nav className="flex-1">
        <ul>
          {categories.map((category) => (
            <li key={category}>
              <button
                onClick={() => onSelectCategory(category)}
                className={`flex items-center w-full p-4 text-left hover:bg-gray-700 ${
                  selectedCategory === category ? 'bg-blue-500' : ''
                }`}
              >
                {category}
              </button>
            </li>
          ))}
        </ul>
      </nav>
    </div>
  );
};

export default Sidebar;
