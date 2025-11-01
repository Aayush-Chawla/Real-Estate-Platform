"use client";

import { useState } from "react";
import { createUserWithEmailAndPassword, signInWithPopup } from "firebase/auth";
import { auth, googleProvider } from "@/lib/firebase";
import { api } from "@/lib/api";
import { useRouter } from "next/navigation";

export default function RegisterPage() {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [name, setName] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleRegister = async () => {
    try {
      setLoading(true);
      const userCred = await createUserWithEmailAndPassword(auth, email, password);
      const token = await userCred.user.getIdToken();

      try {
        await api.post(
          "/auth/register",
          {
            idToken: token,
            name,
            email,
            role: "BUYER", // default role
          }
        );
        router.push("/dashboard");
      } catch (registerError: any) {
        // If user already exists, try to login instead
        if (registerError.response?.data?.message?.includes("already registered")) {
          try {
            await api.post("/auth/login", { idToken: token });
            router.push("/dashboard");
          } catch (loginError) {
            console.error("Login failed after registration error:", loginError);
            setError("User exists but login failed. Please try logging in directly.");
          }
        } else {
          throw registerError;
        }
      }
    } catch (err: any) {
      console.error(err);
      const errorMessage = err.response?.data?.message || err.message || "Registration failed";
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const handleGoogleSignIn = async () => {
    try {
      setLoading(true);
      const result = await signInWithPopup(auth, googleProvider);
      const token = await result.user.getIdToken();

      try {
        await api.post(
          "/auth/register",
          {
            idToken: token,
            name: result.user.displayName,
            email: result.user.email,
            imageUrl: result.user.photoURL,
            role: "BUYER",
          }
        );
        router.push("/dashboard");
      } catch (registerError: any) {
        // If user already exists, try to login instead
        if (registerError.response?.data?.message?.includes("already registered")) {
          try {
            await api.post("/auth/login", { idToken: token });
            router.push("/dashboard");
          } catch (loginError) {
            console.error("Login failed after registration error:", loginError);
            setError("User exists but login failed. Please try logging in directly.");
          }
        } else {
          throw registerError;
        }
      }
    } catch (err: any) {
      console.error(err);
      const errorMessage = err.response?.data?.message || err.message || "Google sign-in failed";
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex flex-col items-center justify-center h-screen gap-4">
      <h1 className="text-3xl font-bold">Create an Account</h1>
      <input
        type="text"
        placeholder="Full Name"
        className="border p-2 rounded w-64"
        value={name}
        onChange={(e) => setName(e.target.value)}
      />
      <input
        type="email"
        placeholder="Email"
        className="border p-2 rounded w-64"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
      />
      <input
        type="password"
        placeholder="Password"
        className="border p-2 rounded w-64"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />

      <button
        onClick={handleRegister}
        disabled={loading}
        className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
      >
        {loading ? "Registering..." : "Register"}
      </button>

      <button
        onClick={handleGoogleSignIn}
        className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
      >
        Continue with Google
      </button>

      {error && <p className="text-red-500">{error}</p>}
    </div>
  );
}

