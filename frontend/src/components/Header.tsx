
type HeaderProps = {
  onAddDogsClick: () => void;
};

const Header = ({ onAddDogsClick }: HeaderProps) => {
  return (
    <header className="mb-9">
      <h1 className="text-3xl font-bold text-green-500 underline">
        Tekmetric Interview
      </h1>
      <button
        className="bg-sky-700 px-4 py-2 text-white hover:bg-sky-800 sm:px-8 sm:py-3"
        onClick={onAddDogsClick}
      >
        Add 5 dogs
      </button>
    </header>
  );
};

export default Header;
