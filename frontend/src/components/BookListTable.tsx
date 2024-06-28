import React, { useState, memo, useCallback } from 'react';
import { Book } from '../types';
import { LazyLoadImage } from 'react-lazy-load-image-component';
import 'react-lazy-load-image-component/src/effects/blur.css';

interface BookListTableProps {
  books: Book[];
  onSelectBook: (book: Book) => void;
}

const BookListTable: React.FC<BookListTableProps> = ({ books, onSelectBook }) => {
  const handleSelectBook = useCallback((book: Book) => {
    onSelectBook(book);
  }, [onSelectBook]);

  return (
    <div className="relative overflow-x-auto">
      <table className="min-w-full bg-nearWhite rounded-lg overflow-hidden shadow-lg">
        <thead className="bg-black text-white sticky top-0 z-10">
          <tr>
            <th className="py-2 px-4 text-center">Cover</th>
            <th className="py-2 px-4 text-center">Title</th>
            <th className="py-2 px-4 text-center">Author(s)</th>
            <th className="py-2 px-4 text-center">Published on</th>
            <th className="py-2 px-4 text-center"></th>
          </tr>
        </thead>
        <tbody>
          {books.map((book, index) => (
            <MemoizedBookRow
              key={book.key}
              book={book}
              onSelectBook={handleSelectBook}
              isAlternate={index % 2 === 0}
            />
          ))}
        </tbody>
      </table>
    </div>
  );
};

interface BookRowProps {
  book: Book;
  onSelectBook: (book: Book) => void;
  isAlternate: boolean;
}

const BookRow: React.FC<BookRowProps> = ({ book, onSelectBook, isAlternate }) => {
  const [loaded, setLoaded] = useState(false);
  const coverUrl = book.cover_i || `${process.env.PUBLIC_URL}/default-image.jpg`;

  const handleImageLoad = () => {
    setLoaded(true);
  };

  const handleImageError = (e: React.SyntheticEvent<HTMLImageElement, Event>) => {
    e.currentTarget.src = `${process.env.PUBLIC_URL}/default-image.jpg`; // Fallback placeholder image
  };

  return (
    <tr key={book.key} className={`hover:bg-lighterGray ${isAlternate ? 'bg-nearWhite' : 'bg-white'}`}>
      <td className="py-2 px-4 text-center">
        <LazyLoadImage
          src={coverUrl}
          alt={book.title}
          className={`w-12 h-16 ${loaded ? 'opacity-100' : 'opacity-0'}`} // Smooth transition
          onLoad={handleImageLoad}
          onError={handleImageError}
          effect="blur"
          key={coverUrl} // Add key to image element
        />
        {!loaded && (
          <img
            src={`${process.env.PUBLIC_URL}/default-image.jpg`}
            alt="loading"
            className="w-12 h-16"
          />
        )}
      </td>
      <td className="py-2 px-4 text-center max-w-xs overflow-hidden text-ellipsis whitespace-nowrap">{book.title}</td>
      <td className="py-2 px-4 text-center max-w-xs overflow-hidden text-ellipsis whitespace-nowrap">{book.authors.join(', ')}</td>
      <td className="py-2 px-4 text-center">{book.first_publish_year}</td>
      <td className="py-2 px-4 text-center">
        <button
          onClick={() => onSelectBook(book)}
          className="px-2 py-1 bg-tekOrange text-white rounded"
        >
          Details
        </button>
      </td>
    </tr>
  );
};

const MemoizedBookRow = memo(BookRow);

export default memo(BookListTable);
