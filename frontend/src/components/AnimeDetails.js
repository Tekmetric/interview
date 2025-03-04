import { Characters } from "./details/Characters"
import { Reviews } from "./details/Reviews"
import { Staff } from "./details/Staff"
import { TopInfoBar } from "./details/TopInfoBar"

export const AnimeDetails = ({ data, characters, staff, reviews }) => {
  return (
    <div className="w-full m-2">
      <TopInfoBar data={data} />
      <div className="mt-8">
        <p className="font-bold text-lg border-b">Synopsis</p>
        <span>{data.synopsis}</span>
      </div>
      <div className="mt-8">
        <p className="font-bold text-lg border-b">Background</p>
        <span>{data.background}</span>
      </div>
      <Characters characters={characters} />
      <Staff staff={staff} />
      {!!reviews.length && <Reviews reviews={reviews} />}
    </div>
  )
}