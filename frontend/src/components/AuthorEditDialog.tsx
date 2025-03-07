import { useEffect, useState } from "react";
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
import { z } from "zod";
import Author from "../types/author";


const authorSchema = z.object({
  firstName: z.string().min(1, "First Name is required!"),
  lastName: z.string().min(1, "Last Name is required!")
});

interface AuthorEditDialogProps {
    openDialog: boolean;
    onHandleCloseDialog: () => void;
    onHandleSave: (updatedAuthor: Author) => void;
    onHandleDelete: (id: number) => void;
    selectedAuthor: Author | null;
}

const AuthorEditDialog = ({openDialog, onHandleCloseDialog, onHandleSave, onHandleDelete, selectedAuthor} : AuthorEditDialogProps)  => {
    const [author, setAuthor] = useState<Author | null>(selectedAuthor);
    const [openDeleteConfirmDialog, setOpenDeleteConfirmDialog] = useState(false);
    const [errors, setErrors] = useState<{ firstName?: string; lastName?: string }>({});

    useEffect(() => {
        if (selectedAuthor) {
            setAuthor(selectedAuthor);
            setErrors({});
        }
    }, [selectedAuthor]);

    const handleSave = () => {
        if (!author) return;

        const validation = authorSchema.safeParse(author);
        if (!validation.success) {
          const fieldErrors: any = {};
          validation.error.errors.forEach((err) => {
            if (err.path.includes("firstName")) fieldErrors.firstName = err.message;
            if (err.path.includes("lastName")) fieldErrors.lastName = err.message;
          });
          setErrors(fieldErrors);
          return;
        }

        onHandleSave(author);
        onHandleCloseDialog();
      };

      const handleDelete = () => {
        if (author?.id) {
            onHandleDelete(author.id);
            setOpenDeleteConfirmDialog(false);
            onHandleCloseDialog();
        }
    };
    return (
        <>
      <Dialog open={openDialog} onClose={onHandleCloseDialog}>
        <DialogTitle>{author?.id ? "Edit Author" : "Create Author"}</DialogTitle>
        <DialogContent className="flex flex-col gap-4 max-h-[400px] overflow-y-auto min-w-[500px]">
          <TextField
            label="First Name"
            fullWidth
            value={author?.firstName ?? ""}
            onChange={(e) => setAuthor({ ...author!, firstName: e.target.value })}
            error={!!errors.firstName}
            helperText={errors.firstName}
          />
          <TextField
            label="Last Name"
            fullWidth
            value={author?.lastName ?? ""}
            onChange={(e) => setAuthor({ ...author!, lastName: e.target.value })}
            error={!!errors.lastName}
            helperText={errors.lastName}
          />
          <TextField
            label="Photo URL"
            fullWidth
            value={author?.photoUrl ?? ""}
            onChange={(e) => setAuthor({ ...author!, photoUrl: e.target.value })}
          />
          {author?.photoUrl && (
            <img
              src={author?.photoUrl}
              alt={author?.firstName + ' ' + author?.lastName}
              className="w-40 h-40 rounded-full mx-auto mb-4"
            />
          )}

        </DialogContent>
        <DialogActions>
          <Button onClick={onHandleCloseDialog} color="primary">Cancel</Button>
          <Button onClick={handleSave} color="primary" variant="contained">Save</Button>
          {selectedAuthor?.id && (
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
          <Typography>Are you sure you want to delete <strong>{selectedAuthor?.firstName} {selectedAuthor?.lastName}</strong>?</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDeleteConfirmDialog(false)} color="secondary">Cancel</Button>
          <Button onClick={handleDelete} color="secondary" variant="contained">Delete</Button>
        </DialogActions>
      </Dialog>
        </>
    )
}

export default AuthorEditDialog;
