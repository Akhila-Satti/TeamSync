import { useEffect, useState } from "react";
import Navbar from "../../components/Navbar";
import { Outlet } from "react-router-dom";
import api from "../../api/axios";
import CompleteProfilePopup from "../../components/CompleteProfilePopup";
import "./Dashboard.css";

function Dashboard() {

  const [showProfilePopup, setShowProfilePopup] = useState(false);

  useEffect(() => {

    const checkProfileCompletion = async () => {
      try {

        const res = await api.get("/api/students/me");

        if (!res.data.profileCompleted) {
          setShowProfilePopup(true);
        }

      } catch (err) {
        console.error("Failed to check profile completion:", err);
      }
    };

    checkProfileCompletion();

  }, []);

  return (
    <>
      <Navbar />

      <main className="dashboard-container">
        <Outlet />
      </main>

      {showProfilePopup && (
        <CompleteProfilePopup
          onComplete={() => setShowProfilePopup(false)}
        />
      )}
    </>
  );
}

export default Dashboard;