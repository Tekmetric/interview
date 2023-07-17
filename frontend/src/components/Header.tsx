
type HeaderProps = {
    onAddDogsClick: () => void;
    dogsPetted: number;
};

const Header = ({ onAddDogsClick, dogsPetted }: HeaderProps) => {
  return (
    <header className="mx-auto max-w-md mb-9 flex flex-col ">
      <h1 className="text-3xl font-bold text-green-500 underline">
        Tekmetric Interview
      </h1>
      <span className="text-xl text-white">Dogs Petted {dogsPetted}</span>
      <span className="text-xl text-white">Your favourite dog is...</span>
      <button
        className="bg-sky-700 px-4 py-2 text-white hover:bg-sky-800 sm:px-8 sm:py-3 max-w-xs"
        onClick={onAddDogsClick}
      >
        Add 5 dogs
      </button>
    </header>
  );
};

export default Header;
