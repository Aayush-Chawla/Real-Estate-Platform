"use client";

import { useEffect, useState } from "react";
import { onAuthStateChanged, signOut } from "firebase/auth";
import { auth } from "@/lib/firebase";
import { getProfile, updateProfile } from "@/lib/api";
import { useRouter } from "next/navigation";

export default function ProfilePage() {
  const router = useRouter();
  const [loading, setLoading] = useState(true);
  const [user, setUser] = useState<any>(null);
  const [name, setName] = useState("");
  const [message, setMessage] = useState("");
  const [saving, setSaving] = useState(false);

  // Fetch profile after Firebase login state changes
  useEffect(() => {
    const unsub = onAuthStateChanged(auth, async (firebaseUser) => {
      if (!firebaseUser) {
        router.push("/login");
        return;
      }

      try {
        const token = await firebaseUser.getIdToken();
        const res = await getProfile(token);
        setUser(res.data);
        setName(res.data?.name || "");
      } catch (err) {
        console.error("Failed to fetch profile:", err);
      } finally {
        setLoading(false);
      }
    });

    return () => unsub();
  }, [router]);

  // Save updated name to backend
  const handleSave = async () => {
    if (!auth.currentUser) return;
    setSaving(true);
    setMessage("");

    try {
      const token = await auth.currentUser.getIdToken();
      const res = await updateProfile(token, { name });
      setUser(res.data);
      setMessage("✅ Profile updated successfully!");
    } catch (err: any) {
      setMessage(err?.message || "❌ Update failed. Try again.");
    } finally {
      setSaving(false);
    }
  };

  const handleLogout = async () => {
    await signOut(auth);
    router.push("/login");
  };

  if (loading)
    return (
      <div className="flex items-center justify-center h-screen">
        <p className="text-gray-500">Loading profile...</p>
      </div>
    );

  if (!user)
    return (
      <div className="flex items-center justify-center h-screen">
        <p className="text-gray-500">No user logged in.</p>
      </div>
    );

  return (
    <div className="flex flex-col items-center justify-center h-screen gap-6 px-4">
      <div className="bg-white shadow-lg rounded-xl p-6 w-full max-w-md border border-gray-100">
        <h1 className="text-2xl font-semibold text-center mb-4">My Profile</h1>

        <div className="space-y-4">
          <div>
            <label className="block text-sm text-gray-600 mb-1">Email</label>
            <p className="text-gray-800">{user.email}</p>
          </div>

          <div>
            <label className="block text-sm text-gray-600 mb-1">Role</label>
            <p className="text-gray-800">{user.role}</p>
          </div>

          <div>
            <label className="block text-sm text-gray-600 mb-1">Status</label>
            <p
              className={`font-medium ${user.status === "ACTIVE"
                  ? "text-green-600"
                  : "text-yellow-600"
                }`}
            >
              {user.status}
            </p>
          </div>

          <div>
            <label className="block text-sm text-gray-600 mb-1">Full Name</label>
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="border border-gray-300 rounded-lg px-3 py-2 w-full focus:outline-none focus:ring focus:ring-blue-300"
            />
          </div>

          <button
            onClick={handleSave}
            disabled={saving}
            className="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition"
          >
            {saving ? "Saving..." : "Save Changes"}
          </button>

          <button
            onClick={handleLogout}
            className="w-full bg-gray-200 text-gray-800 py-2 rounded-lg hover:bg-gray-300 transition"
          >
            Logout
          </button>

          {message && (
            <p className="text-center text-sm text-gray-600 mt-2">{message}</p>
          )}
        </div>
      </div>
    </div>
  );
}




