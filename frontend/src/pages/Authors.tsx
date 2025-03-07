import { useEffect, useRef, useState } from "react";
import { deleteAuthor, getAuthors, getBooksOfAuthor, saveAuthor } from "../services/authorService";
import AuthorCard from "../components/AuthorCard";
import { 
  Dialog, 
  DialogActions, 
  DialogContent, 
  DialogTitle, 
  Button, 
  Typography, 
  List, 
  ListItem, 
  ListItemIcon, 
  ListItemText, 
  Divider, 
  TextField,
  TablePagination
} from "@mui/material";
import { MenuBook } from "@mui/icons-material"; // Kitap ikonu eklendi
import Author, { PageOfAuthors } from "../types/author";
import Book from "../types/book";
import { DEFAULT_PAGE_SIZE } from "../util/constants";
import { useAuth } from "../hooks/useAuth";
import AuthorEditDialog from "../components/AuthorEditDialog";

const Authors = () => {
  const [pageOfAuthors, setPageOfAuthors] = useState<PageOfAuthors | null>(null);
  const [loading, setLoading] = useState(true);
  const [selectedAuthor, setSelectedAuthor] = useState<Author | null>(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [refresh, setRefresh] = useState(false);
  const [openBookDialog, setOpenBookDialog] = useState(false);
  const [authorForBooks, setAuthorForBooks] = useState<Author | null>(null);
  const [loadingBooks, setLoadingBooks] = useState(false);
  const [booksOfSelectedAuthor, setBooksOfSelectedAuthor] = useState<Book[]>([]);

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
      getAuthors(keyword, page, pageSize)
        .then((data) => { setPageOfAuthors(data); })
        .catch((error) => console.error("Error fetching authors:", error))
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
  
  const handleShowBooks = (author: Author) => {
    setAuthorForBooks(author);
    setLoadingBooks(true);
    setBooksOfSelectedAuthor([]);

    getBooksOfAuthor(author)
      .then((data) => {
        setBooksOfSelectedAuthor(data);
      })
      .catch((error) => console.error("Error fetching author's books:", error))
      .finally(() => setLoadingBooks(false));

    setOpenBookDialog(true);
  };

  const handleCloseBookDialog = () => {
    setOpenBookDialog(false);
    setAuthorForBooks(null);
    setBooksOfSelectedAuthor([]);
  };

  const handleSelect = (author: Author) => {
    setSelectedAuthor(author);
    setOpenDialog(true);
  };

  const handleCreate = () => {
    setSelectedAuthor({
      id: null,
      firstName: "",
      lastName: "",
      photoUrl: ""
    });
    setOpenDialog(true);
  };

  const handleSave = async (updatedAuthor : Author) => {
    try {
      await saveAuthor(updatedAuthor);
      setRefresh((prev) => !prev);
    } catch (error) {
      console.error("Error saving/updating author:", error);
      alert("An error occurred while saving the author. Please try again.");
      setOpenDialog(true);
    }
  };

  const handleDelete = async (deletedAuthorId : number) => {
    try {
      await deleteAuthor(deletedAuthorId);
      setRefresh((prev) => !prev);
    } catch (error) {
      console.error("Error deleting author:", error);
      alert("An error occurred while deleting the author. Please try again.");
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
        <Typography variant="h4" className="font-bold">Authors</Typography>
          <TextField
              label="type to search in author name"
              value={keyword}
              onChange={(e) => { setKeyword(e.target.value); }}
              margin="dense"
              className="w-80"
          />
          <TablePagination
              rowsPerPageOptions={rowsPerPageOptions}
              component="div"
              count={pageOfAuthors?.totalItems ?? 0}
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
      
      {pageOfAuthors && pageOfAuthors.authors.length > 0 ? 
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {pageOfAuthors?.authors.map((author) => (
          <AuthorCard key={author.id} author={author} onShowBooks={() => handleShowBooks(author)} onHandleSelect={() => handleSelect(author)} />
        ))}
      </div>
        : "No authors exist."
      }

      <Dialog open={openBookDialog} onClose={handleCloseBookDialog} maxWidth="sm" fullWidth>
        <DialogTitle>
          {authorForBooks ? `${authorForBooks.firstName} ${authorForBooks.lastName}'s Books` : "Books"}
        </DialogTitle>
        <DialogContent>
          {loadingBooks ? (
            <Typography>Loading books...</Typography>
          ) : booksOfSelectedAuthor.length > 0 ? (
            <List>
              {booksOfSelectedAuthor.map((book, index) => (
                <div key={book.id}>
                  <ListItem>
                    <ListItemIcon>
                      <MenuBook color="primary" />
                    </ListItemIcon>
                    <ListItemText
                      primary={
                        <Typography variant="subtitle1" fontWeight="bold">
                          {book.name}
                        </Typography>
                      }
                      secondary={book.publicationYear ? `Published: ${book.publicationYear}` : ""}
                    />
                  </ListItem>
                  {index < booksOfSelectedAuthor.length - 1 && <Divider />} {/* Son elemanda divider eklenmez */}
                </div>
              ))}
            </List>
          ) : (
            <Typography>No books available.</Typography>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseBookDialog} color="primary" variant="contained">
            Close
          </Button>
        </DialogActions>
      </Dialog>

      <AuthorEditDialog openDialog={openDialog}
          onHandleCloseDialog={handleCloseDialog} 
          onHandleSave={handleSave} 
          onHandleDelete={handleDelete}
          selectedAuthor={selectedAuthor}
      />
    </div>
  );
};

export default Authors;
