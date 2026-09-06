import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../../api/axios";
import "./Invitations.css";

function Invitations() {
  const { projectId } = useParams();
  const navigate = useNavigate();

  const [invitations, setInvitations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchInvitations();
  }, [projectId]);

  const fetchInvitations = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await api.get(
        `/api/invitations/project/${projectId}`
      );

      setInvitations(response.data);
    } catch (err) {
      console.error(err);
      setError("Failed to load invitations");
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateTime) => {
    return new Date(dateTime).toLocaleString();
  };

  const openProfile = (studentId) => {
    navigate(`/dashboard/profile/${studentId}`);
  };

  if (loading) {
  return (
    <div className="invitations-page">
      <p className="page-message">Loading invitations...</p>
    </div>
  );
}

if (error) {
  return (
    <div className="invitations-page">
      <p className="page-error">{error}</p>
    </div>
  );
}

  return (
    <div className="invitations-page">
     <button
  className="back-button"
  onClick={() => navigate(-1)}
>
  ← Back
</button>
      <h2>Invitations</h2>

      {invitations.length === 0 ? (
        <p>No invitations sent for this project.</p>
      ) : (
        invitations.map((invitation) => (
          <div
  className="invitation-card"
  key={invitation.invitationId}
>
            <h3>{invitation.projectName}</h3>

            <p>
              Invited student:{" "}
              <button
  className="profile-button"
  onClick={() =>
    openProfile(invitation.sentToStudentId)
  }
>
  {invitation.sentToUserName}
</button>
            </p>

            <p>
              Sent by: {invitation.sentBy}
            </p>

            <p>
              Description: {invitation.description}
            </p>

            <p>
              Status: {invitation.status}
            </p>

            <p>
              Sent at:{" "}
              {formatDate(invitation.sentAt)}
            </p>

            <h4>Project Skills</h4>

            {invitation.projectSkills &&
            invitation.projectSkills.length > 0 ? (
              <ul className="invitation-skills">
                {invitation.projectSkills.map((skill) => (
                  <li key={skill.id}>
                    {skill.name}
                  </li>
                ))}
              </ul>
            ) : (
              <p>No project skills.</p>
            )}
          </div>
        ))
      )}
    </div>
  );
}

export default Invitations;