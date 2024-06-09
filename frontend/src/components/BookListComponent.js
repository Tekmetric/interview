import React, {useState, useEffect} from 'react';
import BookService from '../services/BookService';
import {Link} from "react-router-dom";

export default function BookListComponent() {
    const [books, setBooks] = useState([])

    useEffect(() => {
        BookService.getAllBooks().then((response) => {
            setBooks(response.data)
            console.log(response.data);
        }).catch(error => {
            console.log(error);
        })
    }, []);


    return (
        <div className="container">
            <h2 className="text-center">Tekmetrics Book Shop</h2>
            <Link to="/add-book" className="btn btn-primary mb-2">Add Book</Link>
            <table className="table table-bordered table-striped">
                <thead>
                    <th>Book Id</th>
                    <th>Name</th>
                    <th>Author</th>
                    <th>Price</th>
                    <th>Action</th>
                </thead>
                <tbody>
                {
                    books.map(
                        book =>
                            <tr key={book.id}>
                                <td>{book.id}</td>
                                <td>{book.name}</td>
                                <td>{book.author}</td>
                                <td>{book.price}</td>

                            </tr>
                    )
                }
                </tbody>
            </table>
        </div>
    )
}

