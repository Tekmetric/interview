import {
  createContext,
  useState,
  ReactNode,
  useCallback,
  SetStateAction,
  Dispatch,
  useEffect,
} from 'react';

export type FavouritesContextType = {
  likes: Record<string, unknown>;
  like: (_imdbID: string) => void;
  dislike: (_imdbID: string) => void;
  isLiked: (_imdbID: string) => boolean;
};

export const FavouritesContext = createContext<FavouritesContextType | null>(
  null,
);

const getInitialState = (): Record<string, unknown> => {
  const likes = localStorage.getItem('likes');
  return likes ? JSON.parse(likes) : {};
};

export const FavouritesContextProvider = ({
  children,
}: {
  children: ReactNode;
}) => {
  const [likes, setLikes] =
    useState<Record<string, unknown>>(getInitialState());

  const isLiked = useCallback(
    (imdbID: string) => {
      return !!likes[imdbID];
    },
    [likes],
  );

  const like = useCallback(
    (imdbID: string) => {
      setLikes({ ...likes, [imdbID]: true });
    },
    [setLikes, likes],
  );

  const dislike = useCallback(
    (imdbID: string) => {
      delete likes[imdbID];
      setLikes({ ...likes });
    },
    [setLikes, likes],
  );

  const contextValue = {
    isLiked,
    likes,
    like,
    dislike,
  };

  useEffect(() => {
    localStorage.setItem('likes', JSON.stringify(likes));
  }, [likes]);

  return (
    <FavouritesContext.Provider value={contextValue}>
      {children}
    </FavouritesContext.Provider>
  );
};
