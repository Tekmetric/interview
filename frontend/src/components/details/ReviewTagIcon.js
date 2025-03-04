import { StarHalfOutlined, StarOutlined, StarOutlineOutlined } from "@mui/icons-material";
import { ReviewTagColors } from "../../utils/constants";

export const ReviewTagIcon = ({ tag }) => {
  switch (tag) {
    case 'Recommended':
      return <StarOutlined className={`mr-2 ${ReviewTagColors[tag]}`} />
    case 'Not Recommended':
      return <StarOutlineOutlined className={`mr-2 ${ReviewTagColors[tag]}`} />
    case 'Mixed Feelings':
      return <StarHalfOutlined className={`mr-2 ${ReviewTagColors[tag]}`} />
    default:
      break;
  }
}