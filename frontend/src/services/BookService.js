import axios from 'axios';

const BOOK_SERVICE_API_URL = 'http://localhost:8080/api/v1/books';

class BookService {

    getAllBooks = () => {
        console.log(BOOK_SERVICE_API_URL);
        return axios.get(BOOK_SERVICE_API_URL);
    }

    addBook = (book) => {
        return axios.post(BOOK_SERVICE_API_URL, book);
    }

    getBookById = (id) => {
        return axios.post(BOOK_SERVICE_API_URL, id);
    }
}

export default new BookService();