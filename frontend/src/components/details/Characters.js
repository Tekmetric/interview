export const Characters = ({ characters }) => {
  return (
    <>
      <p className="font-bold text-lg border-b mt-8 mb-4">Characters and voice actors</p>
      <div className="grid grid-cols-2 gap-3">
        {characters.map((char) => {
          return (
            <div className="flex w-full justify-between border-2" key={char.character.mal_id}>
              <div className="flex">
                <img className="w-12 mr-4" src={char.character.images.jpg.image_url} alt="character_image" />
                <div className="max-w-52">
                  <p className="font-bold">{char.character.name}</p>
                  <p className="text-sm">{char.role}</p>
                </div>
              </div>
              {
                !!char.voice_actors.length && (
                  <div className="flex">
                    <div className="max-w-52">
                      <p className="font-bold">{char.voice_actors[0].person.name}</p>
                      <p className="text-sm">{char.voice_actors[0].language}</p>
                    </div>
                    <img className="w-12 ml-4" src={char.voice_actors[0].person.images.jpg.image_url} alt="voice_actor_image" />
                  </div>
                )
              }
            </div>
          )
        })}
      </div>
    </>
  )
}