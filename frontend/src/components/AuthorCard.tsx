import { Card, CardContent, Typography, Button } from "@mui/material";
import Author from "../types/author";
import { useAuth } from "../hooks/useAuth";
import { Edit } from "@mui/icons-material";
import avatar from "../assets/avatar.png";

interface Props {
  author: Author;
  onShowBooks: () => void;
  onHandleSelect: () => void;
}

const AuthorCard = ({ author, onShowBooks, onHandleSelect }: Props) => {
    const { user } = useAuth();
  
  return (
    <Card className="shadow-md hover:shadow-lg transition p-4 mb-4">
      <CardContent>
        <div className="flex items-center justify-between">
        <Typography variant="h6" className="font-bold text-center">{`${author.firstName} ${author.lastName}`}</Typography>
          {user?.admin && (<Button color="primary" onClick={onHandleSelect} startIcon={<Edit />} />)}
        </div>

        <img src={author.photoUrl ? author.photoUrl : avatar} alt={`${author.firstName} ${author.lastName}`} className="w-24 h-24 rounded-full mx-auto mb-4" />
        <div className="flex justify-center mt-4">
          <Button variant="outlined" color="primary" onClick={onShowBooks}>
            Show Books
          </Button>
        </div>
      </CardContent>
    </Card>
  );
};

export default AuthorCard;
