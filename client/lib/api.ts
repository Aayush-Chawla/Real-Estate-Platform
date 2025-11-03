// lib/api.ts
import axios from "axios";

// Create an Axios instance for all API requests
export const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_AUTH_SERVICE_URL || "http://localhost:8081/api",
});

// 🧩 Common Type Definitions
export type UserRole = "BUYER" | "SELLER" | "ADMIN";

export interface RegisterParams {
  idToken: string; // Firebase ID Token (JWT)
  name: string;
  email: string;
  role: UserRole;
  imageUrl?: string;
}

export interface UpdateProfileData {
  name: string;
  imageUrl?: string;
}

// 🧠 Register a new user (Firebase ID token → backend verifies → creates user)
export async function registerUser(params: RegisterParams) {
  try {
    const response = await api.post("/auth/register", {
      idToken: params.idToken, // clear naming — backend should expect this
      name: params.name,
      email: params.email,
      role: params.role,
      imageUrl: params.imageUrl,
    });

    return response.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || error.message);
  }
}

// 🔐 Login existing user
export async function loginUser(idToken: string) {
  try {
    const response = await api.post("/auth/login", { idToken });
    return response.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || error.message);
  }
}

// 👤 Fetch authenticated user profile
export async function getProfile(idToken: string) {
  try {
    const response = await api.get("/auth/profile", {
      headers: { Authorization: `Bearer ${idToken}` },
    });
    return response.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || error.message);
  }
}

// ✏️ Update user profile
export async function updateProfile(idToken: string, data: UpdateProfileData) {
  try {
    const response = await api.put("/auth/profile", data, {
      headers: { Authorization: `Bearer ${idToken}` },
    });
    return response.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || error.message);
  }
}

