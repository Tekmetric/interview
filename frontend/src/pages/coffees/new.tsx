import { useNavigate } from "react-router";
import { useAuth } from "../../hooks/useAuth";
import { useCreateCoffee, type CreateCoffeeData } from "../../hooks/useCoffeeQueries";
import CoffeeForm from "../../components/CoffeeForm";

export default function NewCoffeePage() {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const createCoffeeMutation = useCreateCoffee();

  if (!isAuthenticated) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">
            Authentication Required
          </h2>
          <p className="text-gray-600 dark:text-gray-300 mb-6">
            You need to be logged in to create a new coffee entry.
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

  const handleSubmit = async (coffeeData: CreateCoffeeData) => {
    try {
      const newCoffee = await createCoffeeMutation.mutateAsync(coffeeData);
      navigate(`/coffee/${newCoffee.id}`);
    } catch (error) {
      // Error is handled by the mutation and displayed in the form
      console.error("Failed to create coffee:", error);
    }
  };

  const handleCancel = () => {
    navigate("/");
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
          Add New Coffee
        </h1>
        <p className="mt-2 text-gray-600 dark:text-gray-300">
          Share a new coffee with the community
        </p>
      </div>

      <CoffeeForm
        onSubmit={handleSubmit}
        onCancel={handleCancel}
        isLoading={createCoffeeMutation.isPending}
        error={createCoffeeMutation.error}
        submitLabel="Create Coffee"
      />
    </div>
  );
} 