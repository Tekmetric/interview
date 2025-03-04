import { ReactionTypes } from "../../utils/constants";
import { ReactionIcon } from "./ReactionIcon";

export const ReactionIconList = ({ reactions }) => {

  return (
    <div className="flex bg-slate-100 my-4">
      {
        Object.keys(ReactionTypes).map((key) => (
          <div key={key} className="border bg-white p-2 m-4">
            <span>{ReactionTypes[key]}</span>
            <ReactionIcon type={key} />
            <span>{reactions[key]}</span>
          </div>
        ))
      }
    </div>
  )
}