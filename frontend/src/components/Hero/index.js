import dragon_img from "../../assets/img/dragon_launch.jpeg"
export default function Hero() {
    return (
    //   <div className="relative container">
    //       <img
    //         className=" w-full max-h-96"
    //         src={dragon_img}
    //         alt="SpaceX Dragon 9 Launch"
    //       />
    //     <div className="absolute top-1/4 left-1/4">
    //         <h1 className="text-red text-3xl inset-0">SpaceX Launches</h1>
    //     </div>
    
    //   </div>

    <div className="relative">
          <div className="absolute inset-x-0 bottom-0 h-1/2 bg-gray-100" />
          <div className="max-w-7xl mx-auto sm:px-6 lg:px-8">
            <div className="relative shadow-xl sm:rounded-2xl sm:overflow-hidden">
              <div className="absolute inset-0">
                <img
                  className="h-full w-full object-cover"
                  src={dragon_img}
                  alt="Dragon 9 SpaceX Launch"
                />
                <div className="absolute inset-0" />
              </div>
              <div className="relative px-4 py-16 sm:px-6 sm:py-24 lg:py-32 lg:px-8">
                <h1 className="text-center text-4xl font-extrabold tracking-tight sm:text-5xl lg:text-6xl">
                  <span className="block text-white">SpaceX Launch Tracker</span>
                </h1>
              </div>
            </div>
          </div>
        </div>

    )
  }