import { useEffect, useRef, useState } from "react";
import { deleteBook, getBooks, saveBook } from "../services/bookService";
import BookCard from "../components/BookCard";
import {
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Button,
  Typography,
  TextField,
  TablePagination,
} from "@mui/material";
import Book, { PageOfBooks } from "../types/book";
import { useAuth } from "../hooks/useAuth";
import BookEditDialog from "../components/BookEditDialog";
import { DEFAULT_PAGE_SIZE } from "../util/constants";

const Books = () => {
  const [pageOfBooks, setPageOfBooks] = useState<PageOfBooks | null>(null);
  const [loading, setLoading] = useState(true);
  const [selectedBook, setSelectedBook] = useState<Book | null>(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [refresh, setRefresh] = useState(false);
  const [openAuthorDialog, setOpenAuthorDialog] = useState(false);
    
  const { user } = useAuth();
  const debounceTimer = useRef<number | null>(null);

  const [keyword, setKeyword] = useState("");
  const [page, setPage] = useState(0); 
  const [pageSize, setPageSize] = useState(DEFAULT_PAGE_SIZE);

  const rowsPerPageOptions = [4, 8, 12, 16];
  
  useEffect(() => {
    if (debounceTimer.current) {
      clearTimeout(debounceTimer.current);
    }
    debounceTimer.current = window.setTimeout(() => {
      setLoading(true);
      getBooks(keyword, page, pageSize)
        .then((data) => setPageOfBooks(data))
        .catch((error) => console.error("Error fetching books:", error))
        .finally(() => setLoading(false));
      }, 300);
  }, [refresh, keyword, page, pageSize]);

  const handleChangePage = (event: unknown, newPage: number) => {
    setPage(newPage);
  };

  const handleChangePageSize = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newPageSize = parseInt(event.target.value, 10);
    if (rowsPerPageOptions.includes(newPageSize)) {
      setPageSize(newPageSize);
      setPage(0);
    }
  };

  const handleShowAuthor = (book: Book) => {
    setSelectedBook(book);
    setOpenAuthorDialog(true);
  };

  const handleCloseAuthorDialog = () => {
    setOpenAuthorDialog(false);
  };

  const handleSelect = (book: Book) => {
    setSelectedBook(book);
    setOpenDialog(true);
  };

  const handleCreate = () => {
    setSelectedBook({
      id: null,
      name: "",
      publicationYear: null,
      author: null,
    });
    setOpenDialog(true);
  };

  const handleSave = async (updatedBook : Book) => {
    try {
      await saveBook(updatedBook);
      setRefresh((prev) => !prev);
    } catch (error) {
      console.error("Error saving/updating book:", error);
      alert("An error occurred while saving the book. Please try again.");
      setOpenDialog(true);
    }
  };

  const handleDelete = async (deletedBookId : number) => {
    try {
      await deleteBook(deletedBookId);
      setRefresh((prev) => !prev);
    } catch (error) {
      console.error("Error deleting book:", error);
      alert("An error occurred while deleting the book. Please try again.");
      setOpenDialog(true);
    }
  }

  const handleCloseDialog = () => {
    setOpenDialog(false);
  };

  if (loading) {
    return <Typography>Loading...</Typography>;
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <Typography variant="h4" className="font-bold">Books</Typography>
        <TextField
            label="type to search in book or author name"
            value={keyword}
            onChange={(e) => { setKeyword(e.target.value); }}
            margin="dense"
            className="w-80"
        />
        <TablePagination
            rowsPerPageOptions={rowsPerPageOptions}
            component="div"
            count={pageOfBooks?.totalItems ?? 0}
            rowsPerPage={pageSize}
            page={page}
            onPageChange={handleChangePage}
            onRowsPerPageChange={handleChangePageSize}
        />
        {user?.admin && (
          <Button variant="contained" color="primary" onClick={handleCreate}>
            Create
          </Button>
        )}
      </div>
      
      {pageOfBooks && pageOfBooks.books.length > 0 ? 
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {pageOfBooks?.books.map((book) => (
          <BookCard key={book.id} book={book} onShowAuthor={() => handleShowAuthor(book)} onHandleSelect={() => handleSelect(book)} />
        ))}
        </div>
        : "No books exist."
      }
  
      <Dialog open={openAuthorDialog} onClose={handleCloseAuthorDialog}>
        <DialogTitle>{selectedBook ? `${selectedBook.author?.firstName} ${selectedBook.author?.lastName}` : "Author"}</DialogTitle>
        <DialogContent>
          {selectedBook?.author?.photoUrl ? (
            <img
              src={selectedBook.author?.photoUrl}
              alt={`${selectedBook.author?.firstName} ${selectedBook.author?.lastName}`}
              className="w-80 h-80 rounded-full mx-auto mb-4"
            />
          ) : (
            <Typography>No author information available.</Typography>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseAuthorDialog} color="primary">Close</Button>
        </DialogActions>
      </Dialog>

      <BookEditDialog openDialog={openDialog}
          onHandleCloseDialog={handleCloseDialog} 
          onHandleSave={handleSave} 
          onHandleDelete={handleDelete}
          selectedBook={selectedBook}
      />
    </div>
  );
};

export default Books;
