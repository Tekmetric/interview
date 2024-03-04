import Card from '@mui/material/Card';
import CardActions from '@mui/material/CardActions';
import CardContent from '@mui/material/CardContent';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import { ContentData } from '../types/ContentData';
import Box from '@mui/material/Box';
import { Divider } from '@mui/material';
import useFavourites from '../hooks/useFavourites';
import { useCallback } from 'react';
import useShareDialog from '../hooks/useShareDialog';

type Props = {
  content: ContentData;
};

const ContentCard = ({ content }: Props) => {
  const { isLiked, like, dislike } = useFavourites();
  const { open } = useShareDialog();
  const yearDisplayText = content.yearEnd
    ? `${content.yearStart}-${content.yearEnd}`
    : content.yearStart;

  const imdbID = content.imdbID;
  const onClick = useCallback(() => {
    if (isLiked(imdbID)) {
      dislike(imdbID);
    } else {
      like(imdbID);
    }
  }, [isLiked, like, imdbID, dislike]);

  return (
    <Card
      variant="outlined"
      className="flex flex-1 justify-between font-mono"
      sx={{ height: '320px', maxWidth: '500px' }}
    >
      <div className="flex flex-col justify-between">
        <CardContent>
          <div className="line-clamp-2 font-mono font-medium mb-1.5 text-2xl">
            {content.name}
          </div>
          <div className="flex gap-3 line-clamp-1 mb-1.5">
            <div className="font-mono line-clamp-1">{yearDisplayText}</div>
            <Divider orientation="vertical" flexItem />
            <div className="font-mono">{content.type}</div>
          </div>
          <Typography
            variant="body2"
            color="text.secondary"
            sx={{
              display: '-webkit-box',
              overflow: 'hidden',
              WebkitLineClamp: '7',
              WebkitBoxOrient: 'vertical',
            }}
          >
            {content.description}
          </Typography>
        </CardContent>
        <CardActions>
          <Button size="small" color="secondary" onClick={() => open(imdbID)}>
            Share
          </Button>
          <Button size="small" color="secondary" onClick={onClick}>
            {isLiked(imdbID) ? 'Liked' : 'Like'}
          </Button>
        </CardActions>
      </div>
      <Box
        component="img"
        sx={{
          height: '100%',
          width: 240,
        }}
        alt="Poster image"
        src={content.posterURL}
      />
    </Card>
  );
};

export default ContentCard;
