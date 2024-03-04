import { createContext, useState, ReactNode, useCallback } from 'react';

export type ShareDialogContextType = {
  open: (_imdbID: string) => void;
  close: () => void;
  activeImdbID?: string;
};

export const ShareDialogContext = createContext<ShareDialogContextType | null>(
  null,
);

export const ShareDialogContextProvider = ({
  children,
}: {
  children: ReactNode;
}) => {
  const [activeImdbID, setActiveImdbID] = useState<string | undefined>(
    undefined,
  );

  const open = useCallback(
    (imdbID: string) => {
      setActiveImdbID(imdbID);
    },
    [setActiveImdbID],
  );

  const close = useCallback(() => {
    setActiveImdbID(undefined);
  }, [setActiveImdbID]);

  const contextValue = {
    open,
    close,
    activeImdbID,
  };

  return (
    <ShareDialogContext.Provider value={contextValue}>
      {children}
    </ShareDialogContext.Provider>
  );
};
