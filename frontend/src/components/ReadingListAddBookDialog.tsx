import { useEffect, useState } from "react";
import {
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Button,
  TableContainer,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Paper,
  IconButton,
  TableSortLabel,
  TextField,
  TablePagination,
} from "@mui/material";
import { getBooks } from "../services/bookService";
import Book from "../types/book";
import { AddCircleOutline, Check } from "@mui/icons-material";
import { PageableBook } from "../types/pageableBook";
import ReadingList from "../types/readingList";

interface ReadingListAddBookDialogProps {
  open: boolean;
  onClose: () => void;
  onAddBook: (book: Book) => void;
  selectedReadingList: ReadingList | null;
}

const ReadingListAddBookDialog = ({ open, onClose, onAddBook, selectedReadingList }: ReadingListAddBookDialogProps) => {
    const [pageOfBooks, setPageOfBooks] = useState<PageableBook | null>(null);
    const [loading, setLoading] = useState(true);
    const [keyword, setKeyword] = useState('');
    const [pageNumber, setPageNumber] = useState(0);
    const [pageSize, setPageSize] = useState(4);
    const [order, setOrder] = useState<'asc' | 'desc'>('asc');
    const [orderBy, setOrderBy] = useState<string>('name');

    useEffect(() => {
        if (selectedReadingList) {
            getBooks(keyword, pageNumber, pageSize, orderBy, order)
            .then((data) => setPageOfBooks(data))
            .catch((error) => console.error("Error fetching books:", error))
            .finally(() => setLoading(false));
        }
    }, [pageNumber, pageSize, order, orderBy, keyword, selectedReadingList]);

    const handleRequestSort = (property: string) => {
        const isAsc = orderBy === property && order === 'asc';
        setOrder(isAsc ? 'desc' : 'asc');
        setOrderBy(property);
    };

    const handleChangePage = (event: unknown, newPage: number) => {
        setPageNumber(newPage);
    };
  
    const handleChangePageSize = (event: React.ChangeEvent<HTMLInputElement>) => {
        setPageSize(parseInt(event.target.value, 10));
        setPageNumber(0);
    };
  
    const handleKeywordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setKeyword(event.target.value);
    };

  
  return (
    <Dialog open={open} onClose={onClose} fullWidth>
      <DialogTitle>Select a Book</DialogTitle>
      <DialogContent className="max-h-[400px] overflow-y-auto min-w-[600px]">
        {loading ? (
          <p>Loading...</p>
        ) : (
            <>
            <TextField
                label="Filter by book or author name"
                variant="outlined"
                fullWidth
                value={keyword}
                onChange={handleKeywordChange}
                margin="normal"
            />
  
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>
                    <TableSortLabel
                        active={orderBy === 'name'}
                        direction={orderBy === 'name' ? order : 'asc'}
                        onClick={() => handleRequestSort('name')}
                    >
                        Book Name
                    </TableSortLabel>
                  </TableCell>
                  <TableCell>
                    <TableSortLabel
                          active={orderBy === 'author.firstName'}
                          direction={orderBy === 'author.firstName' ? order : 'asc'}
                          onClick={() => handleRequestSort('author.firstName')}
                      >
                      Author
                    </TableSortLabel>
                  </TableCell>
                  <TableCell></TableCell>
                  <TableCell></TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {pageOfBooks?.books.map((book) => (
                  <TableRow key={book.id}>
                    <TableCell>{book.name}</TableCell>
                    <TableCell>{book.author?.firstName} {book.author?.lastName}</TableCell>
                    <TableCell>{selectedReadingList?.books?.some(b => b.id === book.id) ? <Check /> : ""}</TableCell>
                    <TableCell>
                        <IconButton edge="end" aria-label="add" onClick={() => onAddBook(book)} >
                            <AddCircleOutline />
                        </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
          <TablePagination
            rowsPerPageOptions={[2, 4, 5, 10, 25]}
            component="div"
            count={pageOfBooks?.totalItems ?? 0}
            rowsPerPage={pageSize}
            page={pageNumber}
            onPageChange={handleChangePage}
            onRowsPerPageChange={handleChangePageSize}
            />
          </>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} color="secondary">Close</Button>
      </DialogActions>
    </Dialog>
);
};

export default ReadingListAddBookDialog;
