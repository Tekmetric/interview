import { useEffect, useState } from "react";
import { 
    Button, 
    Typography, 
} from "@mui/material";
import ReadingList from "../types/readingList";
import ReadingListCard from "../components/ReadingListCard";
import { deleteReadingList, getReadingLists, saveReadingList } from "../services/readingListService";
import ReadingListEditDialog from "../components/ReadingListEditDialog";


const ReadingLists = () => {
  const [readingLists, setReadingLists] = useState<ReadingList[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedReadingList, setSelectedReadingList] = useState<ReadingList | null>(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [refresh, setRefresh] = useState(false);

  useEffect(() => {
    setLoading(true);
    getReadingLists()
      .then((data) => setReadingLists(data.readingLists))
      .catch((error) => console.error("Error fetching reading lists:", error))
      .finally(() => setLoading(false));
  }, [refresh]);

  const handleSelect = (readingList: ReadingList) => {
    setSelectedReadingList(readingList);
    setOpenDialog(true);
  };

  const handleCreate = () => {
    setSelectedReadingList({
        id: null,
        name: null,
        owner: null,
        lastUpdate: null,
        shared: null,
        books: null
    });
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
  };

  const handleSave = async (updatedReadingList: ReadingList) => {
      try {
        await saveReadingList(updatedReadingList);
        setRefresh((prev) => !prev);
      } catch (error) {
        console.error("Error saving/updating reading list:", error);
        alert("An error occurred while saving the reading list. Please try again.");
        setOpenDialog(true);
      }
  };

  const handleDelete = async (deletedReadingListId: number) => {
    try {
      await deleteReadingList(deletedReadingListId);
      setRefresh((prev) => !prev);
    } catch (error) {
      console.error("Error deleting reading list:", error);
      alert("An error occurred while deleting the reading list. Please try again.");
      setOpenDialog(true);
    }
  }

  if (loading) {
    return <Typography>Loading...</Typography>;
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <Typography variant="h4" className="font-bold">
          Reading Lists
        </Typography>
        <Button variant="contained" color="primary" onClick={handleCreate}>
          Create
        </Button>
      </div>

      {readingLists.length > 0 ? 
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {readingLists.map((readingList) => (
          <ReadingListCard 
            key={readingList.id} 
            readingList={readingList} 
            onHandleSelect={() => handleSelect(readingList)} 
            readonly={false}
          />
        ))}
      </div>
      : "No reading lists exist. Click the CREATE button to create one."
      }

      <ReadingListEditDialog 
          openDialog={openDialog} 
          onHandleCloseDialog={handleCloseDialog} 
          onHandleSave={handleSave} 
          onHandleDelete={handleDelete}
          selectedReadingList={selectedReadingList}
      />
    </div>
  );
};

export default ReadingLists;
