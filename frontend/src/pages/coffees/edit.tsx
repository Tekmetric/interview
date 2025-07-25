import { useParams, useNavigate } from "react-router";
import { useAuth } from "../../hooks/useAuth";
import { useCoffee, useUpdateCoffee, type CreateCoffeeData } from "../../hooks/useCoffeeQueries";
import CoffeeForm from "../../components/CoffeeForm";

export default function EditCoffeePage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated, user } = useAuth();
  const { data: coffee, isLoading: coffeeLoading, error: coffeeError } = useCoffee(id!);
  const updateCoffeeMutation = useUpdateCoffee();

  if (!isAuthenticated) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">
            Authentication Required
          </h2>
          <p className="text-gray-600 dark:text-gray-300 mb-6">
            You need to be logged in to edit a coffee entry.
          </p>
          <button
            onClick={() => navigate("/auth/login")}
            className="bg-gray-900 dark:bg-gray-100 text-white dark:text-gray-900 px-4 py-2 rounded-md hover:bg-gray-800 dark:hover:bg-gray-200 transition-colors"
          >
            Log In
          </button>
        </div>
      </div>
    );
  }

  if (coffeeLoading) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="flex justify-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-gray-900 dark:border-gray-100"></div>
        </div>
      </div>
    );
  }

  if (coffeeError || !coffee) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">
            Coffee Not Found
          </h2>
          <p className="text-gray-600 dark:text-gray-300 mb-6">
            The coffee you're trying to edit doesn't exist or has been removed.
          </p>
          <button
            onClick={() => navigate("/")}
            className="bg-gray-900 dark:bg-gray-100 text-white dark:text-gray-900 px-4 py-2 rounded-md hover:bg-gray-800 dark:hover:bg-gray-200 transition-colors"
          >
            Back to Coffee List
          </button>
        </div>
      </div>
    );
  }

  // Check if user owns this coffee
  if (coffee.userId !== user?.id) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">
            Not Authorized
          </h2>
          <p className="text-gray-600 dark:text-gray-300 mb-6">
            You can only edit coffee entries that you created.
          </p>
          <button
            onClick={() => navigate(`/coffee/${id}`)}
            className="bg-gray-900 dark:bg-gray-100 text-white dark:text-gray-900 px-4 py-2 rounded-md hover:bg-gray-800 dark:hover:bg-gray-200 transition-colors"
          >
            View Coffee
          </button>
        </div>
      </div>
    );
  }

  const handleSubmit = async (coffeeData: CreateCoffeeData) => {
    try {
      await updateCoffeeMutation.mutateAsync({ id: coffee.id, ...coffeeData });
      navigate(`/coffee/${coffee.id}`);
    } catch (error) {
      // Error is handled by the mutation and displayed in the form
      console.error("Failed to update coffee:", error);
    }
  };

  const handleCancel = () => {
    navigate(`/coffee/${coffee.id}`);
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
          Edit Coffee
        </h1>
        <p className="mt-2 text-gray-600 dark:text-gray-300">
          Update your coffee entry: {coffee.farmName}
        </p>
      </div>

      <CoffeeForm
        initialData={coffee}
        onSubmit={handleSubmit}
        onCancel={handleCancel}
        isLoading={updateCoffeeMutation.isPending}
        error={updateCoffeeMutation.error}
        submitLabel="Update Coffee"
      />
    </div>
  );
} 