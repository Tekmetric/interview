import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

export interface User {
  id: string;
  name: string;
  email: string;
}

export interface LoginCredentials {
  email: string;
  password: string;
}

export interface RegisterData {
  name: string;
  email: string;
  password: string;
}

interface AuthResponse {
  accessToken: string;
  user: User;
}

const API_BASE = "http://localhost:3000";

const login = async (credentials: LoginCredentials): Promise<AuthResponse> => {
  const response = await fetch(`${API_BASE}/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(credentials),
  });

  if (!response.ok) {
    const error = await response
      .json()
      .catch(() => ({ message: "Login failed" }));
    throw new Error(error.message || "Invalid email or password");
  }

  return response.json();
};

const register = async (userData: RegisterData): Promise<AuthResponse> => {
  const response = await fetch(`${API_BASE}/register`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(userData),
  });

  if (!response.ok) {
    const error = await response
      .json()
      .catch(() => ({ message: "Registration failed" }));
    throw new Error(error.message || "Failed to create account");
  }

  return response.json();
};

const getCurrentUser = async (token: string): Promise<User> => {
  // Get user ID from stored user data
  const storedUser = localStorage.getItem("user");
  if (!storedUser) {
    throw new Error("No user data found");
  }

  let userId: string;
  try {
    const userData = JSON.parse(storedUser);
    if (!userData || !userData.id) {
      throw new Error("Invalid user data");
    }
    userId = userData.id;
  } catch {
    throw new Error("Failed to parse user data");
  }

  const response = await fetch(`${API_BASE}/users/${userId}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) {
    throw new Error("Failed to get user data");
  }

  return response.json();
};

export const useLogin = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: login,
    onSuccess: (data) => {
      // Store token in localStorage
      localStorage.setItem("authToken", data.accessToken);
      localStorage.setItem("user", JSON.stringify(data.user));

      // Set user data in cache
      queryClient.setQueryData(["currentUser"], data.user);
    },
  });
};

export const useRegister = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: register,
    onSuccess: (data) => {
      // Store token in localStorage
      localStorage.setItem("authToken", data.accessToken);
      localStorage.setItem("user", JSON.stringify(data.user));

      // Set user data in cache
      queryClient.setQueryData(["currentUser"], data.user);
    },
  });
};

export const useCurrentUser = () => {
  const token = localStorage.getItem("authToken");

  return useQuery({
    queryKey: ["currentUser"],
    queryFn: () => {
      if (!token) {
        throw new Error("No authentication token found");
      }
      return getCurrentUser(token);
    },
    enabled: !!token,
    staleTime: 5 * 60 * 1000, // 5 minutes
    retry: false,
  });
};

export const useLogout = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      // Clear storage
      localStorage.removeItem("authToken");
      localStorage.removeItem("user");
    },
    onSuccess: () => {
      // Clear all cached data
      queryClient.clear();
    },
  });
};
