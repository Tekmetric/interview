import { useState } from "react";
import { KeyboardArrowDown, KeyboardArrowUp } from "@mui/icons-material";
import { ReactionIconList } from "./ReactionIconList";
import { ReviewTag } from "./ReviewTag";

export const Reviews = ({ reviews }) => {
  const [ reviewOpen, setReviewOpen ] = useState({});

  const topReviews = reviews.sort((a,b) => b.reactions.overall - a.reactions.overall).slice(0,3);
  const truncate = (input) => `${input.substring(0, 650)}...`;

  const handleReadMoreClick = (id) => {
    const openReviews = {...reviewOpen};
    if (openReviews[id]) {
      delete openReviews[id]
    } else {
      openReviews[id] = true;
    }
    setReviewOpen(openReviews);
  }

  return (
    <>
      <p className="font-bold text-lg border-b mt-8 mb-4">Top Reviews</p>
      {
        topReviews.map((review, idx) => {
          return (
            <div className="flex border-b mb-6" key={idx}>
              <img src={review.user.images.jpg.image_url} className="w-12 mr-4 max-h-14" alt="user_image" />
              <div>
                <div className="flex justify-between">
                  <span className="font-bold">{review.user.username}</span>
                  <span>{new Date(review.date).toLocaleString('default', { day: 'numeric', month: 'short', year: 'numeric' })}</span>
                </div>
                <ReviewTag tag={review.tags[0]} reactions={review.reactions.overall} />
                <p className="mt-4">{reviewOpen[review.mal_id] ? review.review : truncate(review.review)}</p>
                {reviewOpen[review.mal_id] ? 
                  <div className="my-4">
                    <span>Reviewer's Rating:</span>
                    <span className="font-bold mx-2">{review.score}</span>
                  </div>
                : null}
                {reviewOpen[review.mal_id] ? <ReactionIconList reactions={review.reactions} /> : null}
                <button onClick={() => handleReadMoreClick(review.mal_id)} className="text-slate-500">
                  {reviewOpen[review.mal_id] ? <KeyboardArrowUp /> : <KeyboardArrowDown />}
                  {reviewOpen[review.mal_id] ? 'Show less' : 'Read more'}
                </button>
              </div>
            </div>
          )
        })
      }
    </>
  )
}