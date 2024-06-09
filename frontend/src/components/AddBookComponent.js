import React, {useEffect, useState} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import BookService from "../services/BookService";
import {Link} from "react-router-dom";


const AddBookComponent = () => {

    const [name, setName] = useState('');
    const [author, setAuthor] = useState('');
    const [price, setPrice] = useState('');
    const navigate = useNavigate();
    const {id} = useParams();

    const saveBook = (e) => {
        e.preventDefault();
        const book = {name, author, price};
        BookService.addBook(book).then((response) => {
            console.log(response.data);
            navigate('/books');
        }).catch(error =>{
            console.log(error);
        })
    }

    useEffect(() => {
        BookService.getAllBooks(id).then((response) => {
            setName(response.data.name)
            setAuthor(response.data.author)
            setPrice(response.data.price)
        }).catch(error => {
            console.log(error);
        })
    }, []);

    const title = () => {
        if(id) {
            return <h2 className="text-center">Update Book</h2>
        }else{
            return <h2 className="text-center">Add Book</h2>
        }
    }
    return (
        <div>
            <br/><br/>
            <div className="container">
                <div className="row justify-content-center">
                    <div className="card col-md-6 offset-md-3 offset-md-3">
                        {
                            title()
                        }
                        <div className="card-body">
                            <form>
                                <div className="form-group mb-2">
                                    <label className="form-label">Book Name</label>
                                    <input
                                        type="text"
                                        placeholder="Enter Book Name"
                                        name="name"
                                        className="form-control"
                                        value={name}
                                        onChange={(e) => setName(e.target.value)}
                                    >
                                    </input>
                                </div>
                                <div className="form-group mb-2">
                                    <label className="form-label">Author Name</label>
                                    <input
                                        type="text"
                                        placeholder="Enter Author Name"
                                        name="author"
                                        className="form-control"
                                        value={author}
                                        onChange={(e) => setAuthor(e.target.value)}
                                    >
                                    </input>
                                </div>
                                <div className="form-group mb-2">
                                    <label className="form-label">Price</label>
                                    <input
                                        type="text"
                                        placeholder="Enter Price"
                                        name="price"
                                        className="form-control"
                                        value={price}
                                        onChange={(e) => setPrice(e.target.value)}
                                    >
                                    </input>
                                </div>
                                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                                    <button onClick={(e) => saveBook(e)} className="btn btn-success">Add Book</button>
                                    <Link to="/books" className="btn btn-danger mb-2">Cancel</Link>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default AddBookComponent;