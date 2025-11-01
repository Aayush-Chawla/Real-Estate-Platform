"use client";

import { useEffect, useState } from "react";
import { auth } from "@/lib/firebase";
import { api } from "@/lib/api";
import { useRouter } from "next/navigation";

export default function DashboardPage() {
    const router = useRouter();
    const [userData, setUserData] = useState<any>(null);

    useEffect(() => {
        const unsubscribe = auth.onAuthStateChanged(async (user) => {
            if (!user) {
                router.push("/login");
                return;
            }

            try {
                const token = await user.getIdToken();
                const data = await api.get("/auth/profile", {
                    headers: { Authorization: `Bearer ${token}` }
                });
                setUserData(data.data);
            } catch (error) {
                console.error("Failed to fetch user data:", error);
                router.push("/login");
            }
        });

        return () => unsubscribe();
    }, [router]);

    if (!userData) return <p>Loading dashboard...</p>;

    return (
        <div className="p-6">
            <h1 className="text-3xl font-bold mb-2">Welcome, {userData.name}!</h1>
            <p>Email: {userData.email}</p>
            <p>Role: {userData.role}</p>
        </div>
    );
}
