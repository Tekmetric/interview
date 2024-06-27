import React from 'react';
import { Book } from '../types';

interface BookDetailModalProps {
  book: Book;
  onClose: () => void;
}

const BookDetailModal: React.FC<BookDetailModalProps> = ({ book, onClose }) => {
  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
      <div className="bg-white p-4 rounded-lg max-w-lg w-full">
        <div className="flex justify-between items-center">
          <h2 className="text-xl font-bold">{book.title}</h2>
          <button onClick={onClose} className="text-gray-500">&times;</button>
        </div>
        <p><strong>Author:</strong> {book.authors.map(author => author.name).join(', ')}</p>
        <p><strong>Date Written:</strong> {book.first_publish_year}</p>
        <p><strong>Description:</strong> {book.description || 'No description available.'}</p>
      </div>
    </div>
  );
};

export default BookDetailModal;
