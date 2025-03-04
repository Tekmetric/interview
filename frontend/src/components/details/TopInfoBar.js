export const TopInfoBar = ({ data }) => {
  return (
    <div className="flex bg-slate-100 p-2" key={data.mal_id}>
      <div className="flex flex-col justify-center items-center border-r px-4">
        <p className="bg-blue-700 text-white text-center text-xs w-20">SCORE</p>
        <p className="font-bold text-3xl text-center">{data.score ? data.score : 'N/A'}</p>
        <p className="text-xs text-center">{data.scored_by?.toLocaleString()} members</p>
      </div>
      <div className="m-3 w-full">
        <div className="flex mb-6">
          <div className="w-1/3">
            <span className="text-xl mr-1">Ranked </span>
            <span className="text-xl font-bold">{data.rank ? `#${data.rank}` : 'N/A'}</span>
          </div>
          <div className="w-1/3">
            <span className="text-xl mr-1">Popularity</span>
            <span className="text-xl font-bold">#{data.popularity}</span>
          </div>
          <div className="w-1/3">
            <span className="text-xl mr-1">Members</span>
            <span className="text-xl font-bold">#{data.members.toLocaleString()}</span>
          </div>
        </div>
        <div>
          <span className="text-sm mr-1">{data.season} {data.aired.from ? new Date(data.aired.from).getFullYear() : 'N/A'}</span>
          <span className="border-x-2 text-sm w-20 border-slate-400 inline-block text-center mx-8">{data.type}</span>
          {data.studios ? data.studios.map((studio, idx) => (
            <div key={idx}>
              <span className="text-sm mr-1">{studio.name}</span>
              {idx !== (data.studios.length - 1) && <span>, </span>}
            </div>
          )) : 'None Found'}
        </div>
      </div>
    </div>
  )
}