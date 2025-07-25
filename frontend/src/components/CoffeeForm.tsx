import { useState } from "react";
import type { CreateCoffeeData, Coffee } from "../hooks/useCoffeeQueries";

interface CoffeeFormProps {
  initialData?: Partial<Coffee>;
  onSubmit: (data: CreateCoffeeData) => void;
  onCancel: () => void;
  isLoading?: boolean;
  error?: Error | null;
  submitLabel?: string;
}

export default function CoffeeForm({
  initialData,
  onSubmit,
  onCancel,
  isLoading = false,
  error,
  submitLabel = "Save Coffee"
}: CoffeeFormProps) {
  const [farmName, setFarmName] = useState(initialData?.farmName || "");
  const [price, setPrice] = useState(initialData?.price || "");
  const [altitude, setAltitude] = useState(initialData?.altitude?.toString() || "");
  const [tasteNotesInput, setTasteNotesInput] = useState(
    initialData?.tasteNotes?.join(", ") || ""
  );
  const [description, setDescription] = useState(initialData?.description || "");

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!farmName.trim() || !price.trim() || !altitude.trim() || !description.trim()) {
      return;
    }

    const tasteNotes = tasteNotesInput
      .split(",")
      .map(note => note.trim())
      .filter(note => note.length > 0);

    onSubmit({
      farmName: farmName.trim(),
      price: price.trim(),
      altitude: parseInt(altitude, 10),
      tasteNotes,
      description: description.trim(),
    });
  };

  return (
    <div className="max-w-2xl mx-auto">
      <form onSubmit={handleSubmit} className="space-y-6">
        {error && (
          <div className="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-md p-4">
            <div className="flex">
              <div className="ml-3">
                <h3 className="text-sm font-medium text-red-800 dark:text-red-200">
                  Error
                </h3>
                <div className="mt-2 text-sm text-red-700 dark:text-red-300">
                  {error.message}
                </div>
              </div>
            </div>
          </div>
        )}

        <div className="bg-white dark:bg-gray-800 shadow rounded-lg p-6">
          <div className="grid grid-cols-1 gap-6">
            <div>
              <label htmlFor="farmName" className="block text-sm font-medium text-gray-700 dark:text-gray-300">
                Farm Name *
              </label>
              <input
                type="text"
                id="farmName"
                required
                value={farmName}
                onChange={(e) => setFarmName(e.target.value)}
                disabled={isLoading}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm placeholder-gray-400 dark:placeholder-gray-500 text-gray-900 dark:text-white bg-white dark:bg-gray-700 focus:outline-none focus:ring-gray-500 focus:border-gray-500"
                placeholder="Enter farm name"
              />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label htmlFor="price" className="block text-sm font-medium text-gray-700 dark:text-gray-300">
                  Price *
                </label>
                <input
                  type="text"
                  id="price"
                  required
                  value={price}
                  onChange={(e) => setPrice(e.target.value)}
                  disabled={isLoading}
                  className="mt-1 block w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm placeholder-gray-400 dark:placeholder-gray-500 text-gray-900 dark:text-white bg-white dark:bg-gray-700 focus:outline-none focus:ring-gray-500 focus:border-gray-500"
                  placeholder="$XX.XX"
                />
              </div>

              <div>
                <label htmlFor="altitude" className="block text-sm font-medium text-gray-700 dark:text-gray-300">
                  Altitude (meters) *
                </label>
                <input
                  type="number"
                  id="altitude"
                  required
                  min="0"
                  value={altitude}
                  onChange={(e) => setAltitude(e.target.value)}
                  disabled={isLoading}
                  className="mt-1 block w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm placeholder-gray-400 dark:placeholder-gray-500 text-gray-900 dark:text-white bg-white dark:bg-gray-700 focus:outline-none focus:ring-gray-500 focus:border-gray-500"
                  placeholder="1500"
                />
              </div>
            </div>

            <div>
              <label htmlFor="tasteNotes" className="block text-sm font-medium text-gray-700 dark:text-gray-300">
                Taste Notes
              </label>
              <input
                type="text"
                id="tasteNotes"
                value={tasteNotesInput}
                onChange={(e) => setTasteNotesInput(e.target.value)}
                disabled={isLoading}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm placeholder-gray-400 dark:placeholder-gray-500 text-gray-900 dark:text-white bg-white dark:bg-gray-700 focus:outline-none focus:ring-gray-500 focus:border-gray-500"
                placeholder="chocolate, nutty, fruity (comma separated)"
              />
              <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
                Separate multiple taste notes with commas
              </p>
            </div>

            <div>
              <label htmlFor="description" className="block text-sm font-medium text-gray-700 dark:text-gray-300">
                Description *
              </label>
              <textarea
                id="description"
                rows={4}
                required
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                disabled={isLoading}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm placeholder-gray-400 dark:placeholder-gray-500 text-gray-900 dark:text-white bg-white dark:bg-gray-700 focus:outline-none focus:ring-gray-500 focus:border-gray-500"
                placeholder="Describe the coffee's characteristics, origin, or brewing notes..."
              />
            </div>
          </div>

          <div className="mt-6 flex justify-end space-x-3">
            <button
              type="button"
              onClick={onCancel}
              disabled={isLoading}
              className="px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm text-sm font-medium text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500 disabled:opacity-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isLoading}
              className="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-gray-900 dark:bg-gray-100 dark:text-gray-900 hover:bg-gray-800 dark:hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500 disabled:opacity-50"
            >
              {isLoading ? (
                <div className="flex items-center">
                  <div className="animate-spin -ml-1 mr-2 h-4 w-4 border-2 border-white dark:border-gray-900 border-t-transparent rounded-full"></div>
                  {submitLabel}...
                </div>
              ) : (
                submitLabel
              )}
            </button>
          </div>
        </div>
      </form>
    </div>
  );
} 