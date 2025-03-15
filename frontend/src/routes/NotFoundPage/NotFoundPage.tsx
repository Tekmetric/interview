import { AlertTriangle } from 'lucide-react';

const NotFoundPage = () => {
  return (
    <div className="mx-auto flex h-full w-full max-w-[640px] flex-col items-center justify-center gap-5">
      <AlertTriangle className="h-[64px] w-[64px] shrink-0 text-gray-500" />
      <span className="text-center text-lg text-gray-500">
        404: The page you are looking for isn't here. You either tried some
        shady route or you came here by mistake. Whichever it is, try using the
        navigation
      </span>
    </div>
  );
};

export default NotFoundPage;
