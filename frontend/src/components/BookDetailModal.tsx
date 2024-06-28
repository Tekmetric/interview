import React from 'react';
import { Book } from '../types';

interface BookDetailModalProps {
  book: Book;
  onClose: () => void;
}

const BookDetailModal: React.FC<BookDetailModalProps> = ({ book, onClose }) => {
  const coverUrl = book.cover_i || `${process.env.PUBLIC_URL}/default-image.jpg`;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-lg w-3/4 max-w-3xl p-6 flex">
        <div className="flex-shrink-0">
          <img src={coverUrl} alt={book.title} className="w-48 h-auto rounded-md" />
          {book.previewLink && (
            <a
              href={book.previewLink}
              target="_blank"
              rel="noopener noreferrer"
              className="mt-4 p-2 bg-tekOrange text-white rounded block text-center"
            >
              Read
            </a>
          )}
          
        </div>
        <div className="ml-6">
          <h2 className="text-2xl font-bold mb-2">{book.title}</h2>
          {book.authors && book.authors.length > 0 && (
            <p className="text-lg text-gray-700 mb-2">{book.authors.join(', ')}</p>
          )}
          {book.first_publish_year && (
            <p className="text-md text-gray-600 mb-2">Published: {book.first_publish_year}</p>
          )}
          {book.isbn && book.isbn.length > 0 && (
            <p className="text-md text-gray-600 mb-2">ISBN: {book.isbn.join(', ')}</p>
          )}
          {book.publisher && (
            <p className="text-md text-gray-600 mb-2">Publisher: {book.publisher}</p>
          )}
          {book.number_of_pages && (
            <p className="text-md text-gray-600 mb-2">Pages: {book.number_of_pages}</p>
          )}
          {book.description && (
            <div className='mb-2'>
                <p><strong>Description:</strong></p>
            <div className="overflow-y-auto max-h-48 max-w-md border border-nearWhite p-2">
            <p>{book.description}</p>
          </div>
            </div>
            
          )}
          {book.subjects && book.subjects.length > 0 && (
            <p className="text-md text-gray-600 mb-2">Subjects: {book.subjects.join(', ')}</p>
          )}
          <div className=' flex w-full justify-right'>
          <button onClick={onClose} className="px-4 py-2 bg-gray text-white rounded ml-auto">Close</button>
          </div>
          
        </div>
      </div>
    </div>
  );
};

export default BookDetailModal;
