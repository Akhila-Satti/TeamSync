import { NavLink } from "react-router-dom";
import { useEffect, useState } from "react";
import "./Navbar.css";

function Navbar() {
  const [darkMode, setDarkMode] = useState(
    localStorage.getItem("theme") === "dark"
  );

  useEffect(() => {
    document.documentElement.setAttribute(
      "data-theme",
      darkMode ? "dark" : "light"
    );

    localStorage.setItem("theme", darkMode ? "dark" : "light");
  }, [darkMode]);

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        TeamSync
      </div>

      <div className="navbar-links">
        <NavLink to="/dashboard" end>Home</NavLink>

        <NavLink to="/dashboard/my-projects">
          My Projects
        </NavLink>

        <NavLink to="/dashboard/project-rooms">
          Project Rooms
        </NavLink>

        <NavLink to="/dashboard/notifications">
          Notifications
        </NavLink>

        <NavLink to="/dashboard/my-profile">
          My Profile
        </NavLink>
      </div>

      <button
        className="theme-toggle"
        onClick={() => setDarkMode(!darkMode)}
        aria-label="Toggle theme"
      >
        {darkMode ? "☀️" : "🌙"}
      </button>
    </nav>
  );
}

export default Navbar;