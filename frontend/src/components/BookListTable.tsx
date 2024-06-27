import React, { useState, memo, useCallback } from 'react';
import { Book } from '../types';

interface BookListTableProps {
  books: Book[];
  onSelectBook: (book: Book) => void;
}

const BookListTable: React.FC<BookListTableProps> = ({ books, onSelectBook }) => {
  const handleSelectBook = useCallback((book: Book) => {
    onSelectBook(book);
  }, [onSelectBook]);

  return (
    <table className="min-w-full bg-white">
      <thead>
        <tr>
          <th className="py-2">Cover</th>
          <th className="py-2">Book Name</th>
          <th className="py-2">Author</th>
          <th className="py-2">Date Written</th>
          <th className="py-2">Actions</th>
        </tr>
      </thead>
      <tbody>
        {books.map((book) => (
          <MemoizedBookRow key={book.key} book={book} onSelectBook={handleSelectBook} />
        ))}
      </tbody>
    </table>
  );
};

interface BookRowProps {
  book: Book;
  onSelectBook: (book: Book) => void;
}

const BookRow: React.FC<BookRowProps> = ({ book, onSelectBook }) => {
  const [loaded, setLoaded] = useState(false);
  const coverUrl = book.cover_i ? `https://covers.openlibrary.org/b/id/${book.cover_i}-S.jpg` : `${process.env.PUBLIC_URL}/default-image.jpg`;

  const handleImageLoad = () => {
    setLoaded(true);
  };

  const handleImageError = (e: React.SyntheticEvent<HTMLImageElement, Event>) => {
    e.currentTarget.src = `${process.env.PUBLIC_URL}/default-image.jpg`; // Fallback placeholder image
  };

  return (
    <tr key={book.key} className="hover:bg-gray-100">
      <td className="py-2 px-4">
        <img
          src={coverUrl}
          alt={book.title}
          className={`w-12 h-16 ${loaded ? 'opacity-100' : 'opacity-0'}`} // Smooth transition
          onLoad={handleImageLoad}
          onError={handleImageError}
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
      <td className="py-2 px-4">{book.title}</td>
      <td className="py-2 px-4">{book.authors.map((author) => author.name).join(', ')}</td>
      <td className="py-2 px-4">{book.first_publish_year}</td>
      <td className="py-2 px-4">
        <button
          onClick={() => onSelectBook(book)}
          className="px-2 py-1 bg-blue-500 text-white rounded"
        >
          View More
        </button>
      </td>
    </tr>
  );
};

const MemoizedBookRow = memo(BookRow);

export default memo(BookListTable);
