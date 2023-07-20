import { Card, CardContent, Divider, Typography } from '@mui/material';
import { ChapterType } from '../../shared/types/chapter';

const Chapter = ({ chapter_number, name_meaning, chapter_summary }: ChapterType) => {
  return (
    <Card data-testid="card" sx={{ minWidth: 275 }}>
      <CardContent className="flex flex-col items-center gap-2">
        <Typography variant="h2" component="h2" className="text-5xl">
          {`Chapter ${chapter_number}`}
        </Typography>
        <Divider data-testid="divider" className="w-1/3" />
        <Typography variant="h3" component="h3" className="text-3xl text-slate-500">
          {name_meaning}
        </Typography>
        <Typography className="text-justify pt-5">
          {chapter_summary}
        </Typography>
      </CardContent>
    </Card>
  );
};

export default Chapter;
