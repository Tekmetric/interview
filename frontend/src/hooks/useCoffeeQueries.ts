import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";

export interface Coffee {
  id: string;
  farmName: string;
  price: string;
  altitude: number;
  tasteNotes: string[];
  description: string;
  userId?: string;
}

interface CoffeesResponse {
  coffees: Coffee[];
  totalCount: number;
}

interface CoffeesQueryParams {
  page: number;
  limit: number;
}

export interface CreateCoffeeData {
  farmName: string;
  price: string;
  altitude: number;
  tasteNotes: string[];
  description: string;
}

export interface UpdateCoffeeData extends CreateCoffeeData {
  id: string;
}

const API_BASE = "http://localhost:3000";

const fetchCoffees = async ({ page, limit }: CoffeesQueryParams): Promise<CoffeesResponse> => {
  const response = await fetch(`${API_BASE}/coffees?_page=${page}&_limit=${limit}`);
  
  if (!response.ok) {
    throw new Error("Failed to fetch coffees");
  }
  
  const coffees = await response.json();
  const totalCount = response.headers.get('X-Total-Count');
  
  return {
    coffees,
    totalCount: totalCount ? parseInt(totalCount, 10) : coffees.length,
  };
};

const fetchCoffeeById = async (id: string): Promise<Coffee> => {
  const response = await fetch(`${API_BASE}/coffees/${id}`);
  
  if (!response.ok) {
    throw new Error("Coffee not found");
  }
  
  return response.json();
};

const createCoffee = async (coffeeData: CreateCoffeeData): Promise<Coffee> => {
  const token = localStorage.getItem("authToken");
  const user = localStorage.getItem("user");
  
  if (!token) {
    throw new Error("Authentication required");
  }

  if (!user) {
    throw new Error("User data not found");
  }

  let userId: string;
  try {
    const userData = JSON.parse(user);
    if (!userData || !userData.id) {
      throw new Error("User data missing or invalid");
    }
    userId = userData.id;
  } catch {
    throw new Error("Invalid user data");
  }

  const response = await fetch(`${API_BASE}/600/coffees`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token}`,
    },
    body: JSON.stringify({
      ...coffeeData,
      userId, // Include userId in the request body for json-server-auth
    }),
  });

  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: "Failed to create coffee" }));
    throw new Error(error.message || "Failed to create coffee");
  }

  return response.json();
};

const updateCoffee = async ({ id, ...coffeeData }: UpdateCoffeeData): Promise<Coffee> => {
  const token = localStorage.getItem("authToken");
  const user = localStorage.getItem("user");
  
  if (!token) {
    throw new Error("Authentication required");
  }

  if (!user) {
    throw new Error("User data not found");
  }

  let userId: string;
  try {
    const userData = JSON.parse(user);
    if (!userData || !userData.id) {
      throw new Error("User data missing or invalid");
    }
    userId = userData.id;
  } catch {
    throw new Error("Invalid user data");
  }

  const response = await fetch(`${API_BASE}/600/coffees/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token}`,
    },
    body: JSON.stringify({
      ...coffeeData,
      userId, // Include userId in the request body for json-server-auth
    }),
  });

  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: "Failed to update coffee" }));
    throw new Error(error.message || "Failed to update coffee");
  }

  return response.json();
};

export const useCoffees = (page: number, limit: number = 12) => {
  return useQuery({
    queryKey: ['coffees', page, limit],
    queryFn: () => fetchCoffees({ page, limit }),
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
};

export const useCoffee = (id: string) => {
  return useQuery({
    queryKey: ['coffee', id],
    queryFn: () => fetchCoffeeById(id),
    enabled: !!id, // Only run if id exists
    staleTime: 10 * 60 * 1000, // 10 minutes
  });
};

export const useCreateCoffee = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: createCoffee,
    onSuccess: () => {
      // Invalidate and refetch coffee lists
      queryClient.invalidateQueries({ queryKey: ['coffees'] });
    },
  });
};

export const useUpdateCoffee = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: updateCoffee,
    onSuccess: (data) => {
      // Update the specific coffee in cache
      queryClient.setQueryData(['coffee', data.id], data);
      // Invalidate coffee lists to refresh
      queryClient.invalidateQueries({ queryKey: ['coffees'] });
    },
  });
}; 