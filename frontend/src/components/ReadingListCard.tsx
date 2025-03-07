import { Card, CardContent, Typography, Button, Divider, List, ListItem, ListItemText } from "@mui/material";
import ReadingList from "../types/readingList";
import { Edit, Email, Lock, LockOpen  } from "@mui/icons-material";
import { formatDateTime } from "../util/utils";

interface Props {
  readingList: ReadingList;
  onHandleSelect?: () => void;
  readonly: boolean;
}

const ReadingListCard = ({ readingList, onHandleSelect, readonly }: Props) => {
  const formattedLastUpdate = readingList.lastUpdate ? formatDateTime(readingList.lastUpdate) : "";

  return (
    <Card className="shadow-md hover:shadow-lg transition pt-4 pb-0 mb-4">
      <CardContent>
        <div className="flex items-center justify-between">
          <Typography variant="h6" className="font-bold flex items-center">
            {readingList.name}
            {readingList.shared ? (
              <LockOpen className="text-blue-600 ml-2" />
            ) : (
              <Lock className="text-gray-600 ml-2" />
            )}
          </Typography>
          {!readonly && <Button color="primary" onClick={onHandleSelect} startIcon={<Edit />} />}
        </div>
        {readonly && readingList.owner?.email && (
          <div className="flex">
            <Email className="text-green-400 mr-2" />
          <Typography variant="subtitle1" className="font-bold flex items-center">
            <a href={`mailto:${readingList.owner.email}`} className="text-green-400 hover:underline">
              {readingList.owner.firstName} {readingList.owner.lastName}
            </a>
          </Typography>
          </div>
        )}

        <Typography variant="subtitle2" className="text-gray-400 mt-2">
          {readingList.lastUpdate ? `Updated on ${formattedLastUpdate}` : ""}
        </Typography>
        {readingList.books && readingList.books.length > 0 ? (
          <>
          <Divider className="my-4" />
          <Typography variant="subtitle1" className="font-semibold pt-4">
            Books:
          </Typography>
          <List dense>
            {readingList.books.map((book) => (
              <ListItem key={book.id}>
                <ListItemText primary={book.name} secondary={"by " + book.author?.firstName + ' ' + book.author?.lastName} />
              </ListItem>
            ))}
          </List>
          </>
          ) : 
          <Typography variant="subtitle1" className="pt-4 text-amber-800 font-bold">
            No books exist in the list!
          </Typography>
        }
      </CardContent>
    </Card>
  );
};

export default ReadingListCard;
