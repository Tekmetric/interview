import { ReviewTagColors } from "../../utils/constants";
import { ReviewTagIcon } from "./ReviewTagIcon";

export const ReviewTag = ({ tag, reactions }) => {
  return (
    <div className="bg-slate-100 flex w-fit p-1">
      <ReviewTagIcon tag={tag} />
      <span className={`font-bold mr-5 ${ReviewTagColors[tag]}`}>{tag}</span>
      <span className="mx-4 font-bold">{reactions}</span>
      <span>Reactions</span>
    </div>
  );
}