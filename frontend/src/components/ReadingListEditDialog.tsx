import { Dialog, DialogActions, DialogContent, DialogTitle,
    Button,
    TextField,
    Typography,
    IconButton,
    TableContainer,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableRow,
    Paper} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import ReadingList from "../types/readingList";
import Book from "../types/book";
import { AddCircleOutline, Lock, LockOpen, Save } from "@mui/icons-material";
import ReadingListAddBookDialog from "./ReadingListAddBookDialog";
import { useEffect, useState } from "react";


interface ReadingListEditDialogProps {
    openDialog: boolean;
    onHandleCloseDialog: () => void;
    onHandleSave: (updatedReadingList: ReadingList) => void;
    onHandleDelete: (id: number) => void;
    selectedReadingList: ReadingList | null;
}


const ReadingListEditDialog = ({openDialog, onHandleCloseDialog, onHandleSave, onHandleDelete, selectedReadingList}: ReadingListEditDialogProps) => {
    const [readingList, setReadingList] = useState<ReadingList | null>(selectedReadingList);
    const [readingListName, setReadingListName] = useState("");
    const [isShared, setIsShared] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");
    const [openBookDialog, setOpenBookDialog] = useState(false);
    const [openDeleteConfirmDialog, setOpenDeleteConfirmDialog] = useState(false);

    useEffect(() => {
        if (selectedReadingList) {
            setReadingList(selectedReadingList);
            setReadingListName(selectedReadingList.name ?? "");
            setIsShared(selectedReadingList.shared ? true : false);
        }
    }, [selectedReadingList]);

    const handleSave = () => {
        if (!readingListName.trim()) {
          setErrorMessage("Reading List name is required.");
          return;
        }

        setErrorMessage("");
        const updatedReadingList = {
            ...readingList,
            name: readingListName,
            shared: isShared
        };
        onHandleSave(updatedReadingList);
        onHandleCloseDialog();
    };

    const handleOpenBookDialog = () => {
        setOpenBookDialog(true);
    };

    const handleCloseBookDialog = () => {
        setOpenBookDialog(false);
    };

    const handleAddBook = (book: Book) => {
        if (readingList && !readingList.books?.some(b => b.id === book.id)) {
          const updatedReadingList = {
            ...readingList,
            books: [...(readingList.books || []), book],
          };
          setReadingList(updatedReadingList);
        }
    };

    const handleRemoveBook = (bookId: number) => {
        if (readingList) {
          setReadingList({
            ...readingList,
            books: readingList.books?.filter((book) => book.id !== bookId) || [],
          });
        }
    };

    const handleDelete = () => {
        if (readingList?.id) {
            onHandleDelete(readingList.id);
            setOpenDeleteConfirmDialog(false);
            onHandleCloseDialog();
        }
    };

    return (
        <>
        <Dialog open={openDialog} onClose={onHandleCloseDialog}>
            <DialogTitle>{selectedReadingList?.id ? "Edit Reading List" : "Create Reading List"}</DialogTitle>
            <DialogContent className="max-h-[400px] overflow-y-auto min-w-[500px]">
                <TextField
                    fullWidth
                    label="Reading List Name"
                    value={readingListName}
                    onChange={(e) => setReadingListName(e.target.value)}
                    margin="dense"
                    error={!!errorMessage}
                    helperText={errorMessage}
                />


                <Button
                    onClick={() => setIsShared(!isShared)}
                    variant="text"
                    startIcon={isShared ? <LockOpen className="text-blue-600" /> : <Lock className="text-gray-600" />}
                    className="focus:outline-none focus:ring-0"
                    >
                    {isShared ? "Stop Sharing" : "Share It" }
                </Button>


                {readingList ? (
                <div className="mt-10">
                    <Typography variant="h6" className="font-bold">Books</Typography>

                    <TableContainer component={Paper}>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell><Typography variant="subtitle1" className="font-bold">Book Name</Typography></TableCell>
                                    <TableCell><Typography variant="subtitle1" className="font-bold">Author</Typography></TableCell>
                                    <TableCell>
                                        <IconButton edge="end" aria-label="add" onClick={() => handleOpenBookDialog()}>
                                            <AddCircleOutline />
                                        </IconButton>
                                    </TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {readingList.books?.map((book) => (
                                <TableRow key={book.id}>
                                    <TableCell>{book.name}</TableCell>
                                    <TableCell>{book.author?.firstName} {book.author?.lastName}</TableCell>
                                    <TableCell>
                                        <IconButton edge="end" aria-label="delete" onClick={() => handleRemoveBook(book.id!)} >
                                            <DeleteIcon />
                                        </IconButton>
                                    </TableCell>
                                </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                </div>
                ): null}
            </DialogContent>
            <DialogActions>
                <Button onClick={onHandleCloseDialog} color="primary">Cancel</Button>
                <Button onClick={handleSave} color="primary" variant="contained" startIcon={<Save />}>Save</Button>
                {readingList?.id ?
                <Button onClick={() => setOpenDeleteConfirmDialog(true)}
                        color="secondary"
                        variant="outlined"
                        startIcon={<DeleteIcon />}
                    >
                        Delete
                    </Button>
                : null }
            </DialogActions>
        </Dialog>

        <Dialog open={openDeleteConfirmDialog} onClose={() => setOpenDeleteConfirmDialog(false)}>
            <DialogTitle>Confirm Deletion</DialogTitle>
            <DialogContent>
                <Typography>Are you sure you want to delete <strong>{selectedReadingList?.name}</strong>?</Typography>
            </DialogContent>
            <DialogActions>
                <Button onClick={() => setOpenDeleteConfirmDialog(false)} color="primary">Cancel</Button>
                <Button onClick={handleDelete} color="secondary" variant="contained">Delete</Button>
            </DialogActions>
        </Dialog>

        <ReadingListAddBookDialog open={openBookDialog} onClose={handleCloseBookDialog} onAddBook={handleAddBook} selectedReadingList={readingList}/>
        </>
    );
}
export default ReadingListEditDialog;
