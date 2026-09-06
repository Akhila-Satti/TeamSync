import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../api/axios";
import "./ProjectRooms.css";

function ProjectRooms() {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const navigate = useNavigate();

  useEffect(() => {
    fetchRooms();
  }, []);

  const fetchRooms = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await api.get("/api/rooms");

      setRooms(response.data);
    } catch (err) {
      console.error(err);
      setError("Failed to load project rooms");
    } finally {
      setLoading(false);
    }
  };

  const openRoom = (roomId) => {
    navigate(`/dashboard/project-rooms/${roomId}`);
  };

  if (loading) {
    return (
      <div className="project-rooms-page">
        <p className="project-rooms-state">Loading project rooms...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="project-rooms-page">
        <p className="project-rooms-error">{error}</p>
      </div>
    );
  }

  return (
    <div className="project-rooms-page">
      <h2 className="project-rooms-title">Project Rooms</h2>

      {rooms.length === 0 ? (
        <p className="project-rooms-empty">
          You are not a member of any project yet.
        </p>
      ) : (
        <div className="project-rooms-grid">
          {rooms.map((room) => (
            <div className="project-room-card" key={room.roomId}>
              <h3>{room.projectName}</h3>

              <button
                className="open-room-button"
                onClick={() => openRoom(room.roomId)}
              >
                Open Room
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default ProjectRooms;