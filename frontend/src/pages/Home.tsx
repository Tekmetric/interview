import { useEffect, useState } from "react";
import { Typography } from "@mui/material";
import ReadingList from "../types/readingList";
import ReadingListCard from "../components/ReadingListCard";
import { getSharedReadingLists } from "../services/readingListService";


const Home = () => {
  const [readingLists, setReadingLists] = useState<ReadingList[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    getSharedReadingLists()
      .then((data) => setReadingLists(data.readingLists))
      .catch((error) => console.error("Error fetching reading lists:", error))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <Typography>Loading...</Typography>;
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <Typography variant="h4" className="font-bold">
          Home
        </Typography>
      </div>

      {readingLists.length > 0 ? 
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {readingLists.map((readingList) => (
          <ReadingListCard 
            key={readingList.id} 
            readingList={readingList} 
            readonly={true}
          />
        ))}
      </div>
      : "Sorry, no one shared a reading list, yet. :("
      }

    </div>
  );
};

export default Home;
