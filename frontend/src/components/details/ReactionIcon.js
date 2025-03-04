import { EditNoteOutlined, FavoriteOutlined, SentimentNeutral, SentimentVerySatisfied, ThumbUpOutlined } from "@mui/icons-material";
import { ReactionColors } from "../../utils/constants"

export const ReactionIcon = ({ type }) => {
  switch (type) {
    case 'confusing':
      return <SentimentNeutral className={`mx-2 ${ReactionColors[type]}`} />
    case 'funny':
      return <SentimentVerySatisfied className={`mx-2 ${ReactionColors[type]}`} />
    case 'love_it':
      return <FavoriteOutlined className={`mx-2 ${ReactionColors[type]}`} />
    case 'nice':
      return <ThumbUpOutlined className={`mx-2 ${ReactionColors[type]}`} />
    case 'well_written':
      return <EditNoteOutlined className={`mx-2 ${ReactionColors[type]}`} />
    default:
      break;
  }
}