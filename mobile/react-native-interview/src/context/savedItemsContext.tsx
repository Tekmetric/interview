import React, { createContext, useContext, useState, useCallback, useEffect, useMemo } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { ArtCrime } from '@/types/artCrime';
const SAVED_ITEMS_KEY = '@saved_items';

type SavedItemsContextType = {
  savedItems: ArtCrime[];
  isLoading: boolean;
  saveItem: (item: ArtCrime) => Promise<void>;
  removeItem: (itemId: string) => Promise<void>;
  isItemSaved: (itemId: string) => boolean;
};

const SavedItemsContext = createContext<SavedItemsContextType | null>(null);

export function SavedItemsProvider({ children }: { children: React.ReactNode }) {
  const [savedItems, setSavedItems] = useState<ArtCrime[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  const loadSavedItems = useCallback(async () => {
    try {
      const savedItemsJson = await AsyncStorage.getItem(SAVED_ITEMS_KEY);
      if (savedItemsJson) {
        setSavedItems(JSON.parse(savedItemsJson));
      } else {
        setSavedItems([]);
      }
    } catch (error) {
      console.error('Error loading saved items:', error);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    loadSavedItems();
  }, [loadSavedItems]);

  const saveItem = useCallback(
    async (item: ArtCrime) => {
      try {
        const updatedItems = [...savedItems, item];
        await AsyncStorage.setItem(SAVED_ITEMS_KEY, JSON.stringify(updatedItems));
        setSavedItems(updatedItems);
      } catch (error) {
        console.error('Error saving item:', error);
      }
    },
    [savedItems],
  );

  const removeItem = useCallback(
    async (itemId: string) => {
      try {
        const updatedItems = savedItems.filter((item) => item.uid !== itemId);
        await AsyncStorage.setItem(SAVED_ITEMS_KEY, JSON.stringify(updatedItems));
        setSavedItems(updatedItems);
      } catch (error) {
        console.error('Error removing item:', error);
      }
    },
    [savedItems],
  );

  const isItemSaved = useCallback(
    (itemId: string) => {
      return savedItems.some((item) => item.uid === itemId);
    },
    [savedItems],
  );

  const value = useMemo(
    () => ({
      savedItems,
      isLoading,
      saveItem,
      removeItem,
      isItemSaved,
    }),
    [savedItems, isLoading, saveItem, removeItem, isItemSaved],
  );

  return <SavedItemsContext.Provider value={value}>{children}</SavedItemsContext.Provider>;
}

export function useSavedItems() {
  const context = useContext(SavedItemsContext);
  if (!context) {
    throw new Error('useSavedItems must be used within a SavedItemsProvider');
  }
  return context;
}
