import { useEffect, useState } from "react";
import { getAuthors } from "../services/authorService";
import {
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Button,
  Typography,
  TextField,
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import Autocomplete from "@mui/material/Autocomplete";
import { z } from "zod";
import Book from "../types/book";
import Author from "../types/author";


const bookSchema = z.object({
  name: z.string().min(1, "Book Name is required!"),
  publicationYear: z
    .number()
    .int("Publication Year must be a whole number!")
    .min(1000, "Publication Year must be at least 1000")
    .max(new Date().getFullYear(), "Publication Year cannot be in the future")
    .optional()
    .nullable(),
  author: z.object({
    id: z.number(),
    firstName: z.string(),
    lastName: z.string(),
  }).nullable().refine((val) => val !== null, "Author is required!"),
});

interface BookEditDialogProps {
    openDialog: boolean;
    onHandleCloseDialog: () => void;
    onHandleSave: (updatedBook: Book) => void;
    onHandleDelete: (id: number) => void;
    selectedBook: Book | null;
}

const BookEditDialog = ({openDialog, onHandleCloseDialog, onHandleSave, onHandleDelete, selectedBook} : BookEditDialogProps)  => {
    const [book, setBook] = useState<Book | null>(selectedBook);
    const [openDeleteConfirmDialog, setOpenDeleteConfirmDialog] = useState(false);
    const [errors, setErrors] = useState<{ name?: string; author?: string; publicationYear? : number }>({});
    const [authors, setAuthors] = useState<Author[]>([]);

    useEffect(() => {
        if (selectedBook) {
            setBook(selectedBook);
            setErrors({});
            getAuthors()
                .then((data) => setAuthors(data.authors))
                .catch((error) => console.error("Error fetching authors:", error));
        }
    }, [selectedBook]);

    const handleSave = () => {
        if (!book) return;

        const validation = bookSchema.safeParse(book);
        if (!validation.success) {
          const fieldErrors: any = {};
          validation.error.errors.forEach((err) => {
            if (err.path.includes("name")) fieldErrors.name = err.message;
            if (err.path.includes("author")) fieldErrors.author = err.message;
            if (err.path.includes("publicationYear")) fieldErrors.publicationYear = err.message;
          });
          setErrors(fieldErrors);
          return;
        }

        onHandleSave(book);
        onHandleCloseDialog();
      };

      const handleDelete = () => {
        if (book?.id) {
            onHandleDelete(book.id);
            setOpenDeleteConfirmDialog(false);
            onHandleCloseDialog();
        }
    };
    return (
        <>
      <Dialog open={openDialog} onClose={onHandleCloseDialog}>
        <DialogTitle>{book?.id ? "Edit Book" : "Create Book"}</DialogTitle>
        <DialogContent className="flex flex-col gap-4 max-h-[400px] overflow-y-auto min-w-[500px]">
          <TextField
            label="Book Name"
            fullWidth
            value={book?.name ?? ""}
            onChange={(e) => setBook({ ...book!, name: e.target.value })}
            error={!!errors.name}
            helperText={errors.name}
          />
          <Autocomplete
            options={authors}
            getOptionLabel={(option) => `${option.firstName} ${option.lastName}`}
            value={book?.author || null}
            onChange={(_, newValue) => setBook({ ...book!, author: newValue })}
            renderInput={(params) => (
              <TextField {...params} label="Author" error={!!errors.author} helperText={errors.author} />
            )}
          />
          <TextField
            label="Publication Year"
            fullWidth
            type="number"
            value={book?.publicationYear ?? ""}
            onChange={(e) => setBook({ ...book!, publicationYear: Number(e.target.value) })}
            error={!!errors.publicationYear}
            helperText={errors.publicationYear}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={onHandleCloseDialog} color="primary">Cancel</Button>
          <Button onClick={handleSave} color="primary" variant="contained">Save</Button>
          {selectedBook?.id && (
            <Button onClick={() => setOpenDeleteConfirmDialog(true)}
                        color="secondary"
                        variant="outlined"
                        startIcon={<DeleteIcon />}
                    >
                        Delete
                    </Button>
          )}
        </DialogActions>
      </Dialog>

      <Dialog open={openDeleteConfirmDialog} onClose={() => setOpenDeleteConfirmDialog(false)}>
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <Typography>Are you sure you want to delete <strong>{selectedBook?.name}</strong>?</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDeleteConfirmDialog(false)} color="secondary">Cancel</Button>
          <Button onClick={handleDelete} color="secondary" variant="contained">Delete</Button>
        </DialogActions>
      </Dialog>
        </>
    )
}

export default BookEditDialog;
