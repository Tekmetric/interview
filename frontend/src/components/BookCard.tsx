import { Card, CardContent, Typography, Button, Link } from "@mui/material";
import Book from "../types/book";
import { Edit } from "@mui/icons-material";
import { useAuth } from "../hooks/useAuth";


interface Props {
  book: Book;
  onShowAuthor: () => void;
  onHandleSelect: () => void;
}

const BookCard = ({ book, onShowAuthor, onHandleSelect }: Props) => {
  const { user } = useAuth();

  return (
    <Card className="shadow-md hover:shadow-lg transition p-4 mb-4">
      <CardContent>
        <div className="flex items-center justify-between">
          <Typography variant="h6" className="font-bold">{book.name}</Typography>
          {user?.admin && (<Button color="primary" onClick={onHandleSelect} startIcon={<Edit />} />)}
        </div>

        {book.author && (
          <Typography variant="subtitle1" className="text-gray-500">
            Author:{" "}
            <Link component="button" onClick={onShowAuthor} color="primary" underline="hover">
              {book.author.firstName} {book.author.lastName}
            </Link>
          </Typography>
        )}
        {book.publicationYear ? (
          <Typography variant="body2" className="text-gray-400">
          Published: {book.publicationYear}
         </Typography>) : null}
      </CardContent>
    </Card>
  );
};

export default BookCard;
