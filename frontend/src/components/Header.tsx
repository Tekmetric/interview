const Header = () => (
  <div className="flex flex-col gap-y-3 justify-center items-center w-screen bg-cyan-300 max-h-fit py-8">
    <div className="text-cyan-800 text-center text-4xl">
      Tekmetric Movie Plot
    </div>
    <div className="relative w-fit text-cyan-700 text-center text-xxl before:absolute before:inset-0 before:animate-typewriter before:bg-cyan-300">
      Famous movies described with random quotes from the Internet
    </div>
  </div>
);

export default Header;
