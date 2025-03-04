export const AnimeSpecs = ({ data }) => {
  return (
    <div className="p-2 max-w-64 border-r">
      <img src={data.images.jpg.image_url} alt="anime_image" />
      <p className="border-b my-4 font-bold">Alternative titles</p>
      <div>
        {data.titles.map((title, idx) => {
          if (title.type !== 'Default') {
            return (
              <p key={idx}>
                <span className="font-bold">{title.type}: </span>
                <span>{title.title}</span>
              </p>
            )
          }
          return null;
        })}
      </div>
      <p className="border-b my-4 font-bold">Information</p>
      <div>
        <span className="font-bold">Type: </span>
        <span>{data.type}</span>
      </div>
      <div>
        <span className="font-bold">Episodes: </span>
        <span>{data.episodes ? data.episodes : 'Unknown'}</span>
      </div>
      <div>
        <span className="font-bold">Status: </span>
        <span>{data.status}</span>
      </div>
      <div>
        <span className="font-bold">Aired: </span>
        <span>{data.aired.string}</span>
      </div>
      <div>
        <span className="font-bold">Premiered: </span>
        <span>{data.season} {data.aired.from ? new Date(data.aired.from).getFullYear() : '?'}</span>
      </div>
      <div>
        <span className="font-bold">Broadcast: </span>
        <span>{data.broadcast.string}</span>
      </div>
      <div>
        <span className="font-bold">Producers: </span>
        {data.producers.length ? data.producers.map((producer, idx) => (
          <div key={idx}>
            <span>{producer.name}</span>
            {idx !== (data.producers.length - 1) && <span>, </span>}
          </div>
        )) : 'None Found'}
      </div>
      <div>
        <span className="font-bold">Licensors: </span>
        {data.licensors.length ? data.licensors.map((licensor, idx) => (
          <div key={idx}>
            <span>{licensor.name}</span>
            {idx !== (data.licensors.length - 1) && <span>, </span>}
          </div>
        )) : 'None Found'}
      </div>
      <div>
        <span className="font-bold">Studios: </span>
        {data.studios.length ? data.studios.map((studio, idx) => (
          <div key={idx}>
            <span>{studio.name}</span>
            {idx !== (data.studios.length - 1) && <span>, </span>}
          </div>
        )) : 'None Found'}
      </div>
      <div>
        <span className="font-bold">Source: </span>
        <span>{data.source}</span>
      </div>
      <div>
        <span className="font-bold">Genres: </span>
        {data.genres.length ? data.genres.map((genre, idx) => (
          <div key={idx}>
            <span>{genre.name}</span>
            {idx !== (data.genres.length - 1) && <span>, </span>}
          </div>
        )) : 'None Found'}
      </div>
      <div>
        <span className="font-bold">Demographics: </span>
        {data.demographics.length ? data.demographics.map((demographic, idx) => (
          <div key={idx}>
            <span>{demographic.name}</span>
            {idx !== (data.demographics.length - 1) && <span>, </span>}
          </div>
        )) : 'None Found'}
      </div>
      <div>
        <span className="font-bold">Duration: </span>
        <span>{data.duration}</span>
      </div>
      <div>
        <span className="font-bold">Rating: </span>
        <span>{data.rating}</span>
      </div>
      <p className="border-b my-4 font-bold">Statistics</p>
      <div>
        <span className="font-bold">Score: </span>
        <span>{data.score ? `${data.score} scored by ${data.scored_by} users` : 'N/A'}</span>
      </div>
      <div>
        <span className="font-bold">Ranked: </span>
        <span>{data.rank ? `#${data.rank}` : 'N/A'}</span>
      </div>
      <div>
        <span className="font-bold">Popularity: </span>
        <span>{data.popularity ? `#${data.popularity}` : 'N/A'}</span>
      </div>
      <div>
        <span className="font-bold">Members: </span>
        <span>{data.members.toLocaleString()}</span>
      </div>
      <div>
        <span className="font-bold">Favorites: </span>
        <span>{data.favorites}</span>
      </div>
    </div>
  )
}