export default function GrassBottom({ numberOfGrassBlades} : { numberOfGrassBlades: number }) {
  
    return (
    <div className="absolute bottom-0 left-0 flex h-full w-full flex-wrap bg-gradient-to-t from-green-700 to-green-200 -z-10">
    {
        Array.from({length: numberOfGrassBlades}, (_, i) => {
            const dynamicStyle = {
              left: `${i+.5}%`,
            };
            return <div key={i} className={`blade`} style={dynamicStyle}></div>
          }
        )
    }

    {
      Array.from({length: numberOfGrassBlades}, (_, i) => {
          const dynamicStyle = {
            left: `${i+1}%`
          };
          return <div key={i} className={`blade sm`} style={dynamicStyle}></div>
        }
      )
    }

  </div>
    )
}