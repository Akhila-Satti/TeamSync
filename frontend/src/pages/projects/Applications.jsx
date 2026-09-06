import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../../api/axios";
import "./Applications.css";

function Applications() {
  const { projectId } = useParams();
  const navigate = useNavigate();

  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchApplications();
  }, [projectId]);

  const fetchApplications = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await api.get(
        `/api/applications/${projectId}`
      );

      setApplications(response.data);
    } catch (err) {
      console.error(err);
      setError("Failed to load applications");
    } finally {
      setLoading(false);
    }
  };

  const updateApplicationStatus = async (
    applicationId,
    status
  ) => {
    try {
      await api.patch(
        `/api/applications/${applicationId}/status`,
        {
          applicationStatus: status,
        }
      );

      setApplications((previousApplications) =>
        previousApplications.map((application) =>
          application.applicationId === applicationId
            ? {
                ...application,
                status: status,
              }
            : application
        )
      );
    } catch (err) {
      console.error(err);
      alert("Failed to update application status");
    }
  };

  const openProfile = (studentId) => {
    navigate(`/dashboard/profile/${studentId}`);
  };

  const formatDate = (dateTime) => {
    return new Date(dateTime).toLocaleString();
  };

  if (loading) {
  return (
    <div className="applications-page">
      <p className="page-message">Loading applications...</p>
    </div>
  );
}

  if (error) {
  return (
    <div className="applications-page">
      <p className="page-error">{error}</p>
    </div>
  );
}

  return (
    <div className="applications-page">
     <button
  className="back-button"
  onClick={() => navigate(-1)}
>
  ← Back
</button>

      <h2>Applications</h2>

      {applications.length === 0 ? (
        <p>No applications yet.</p>
      ) : (
        applications.map((application) => (
         <div
  className="application-card"
  key={application.applicationId}
>
            <h3>
              <button
  className="applicant-button"
  onClick={() => openProfile(application.applicantId)}
>
  {application.userName}
</button>
            </h3>

            <p>Status: {application.status}</p>

            <p>
              Applied on:{" "}
              {formatDate(application.date)}
            </p>

            <h4>Skills</h4>

            {application.skills &&
            application.skills.length > 0 ? (
              <ul className="application-skills">
                {application.skills.map((skill) => (
                  <li key={skill.id}>
                    {skill.name}
                  </li>
                ))}
              </ul>
            ) : (
              <p>No skills listed.</p>
            )}

            {application.status === "PENDING" && (
             <div className="application-actions">
                <button
                  onClick={() =>
                    updateApplicationStatus(
                      application.applicationId,
                      "ACCEPTED"
                    )
                  }
                >
                  Accept
                </button>

                <button
                  onClick={() =>
                    updateApplicationStatus(
                      application.applicationId,
                      "REJECTED"
                    )
                  }
                >
                  Reject
                </button>
              </div>
            )}
          </div>
        ))
      )}
    </div>
  );
}

export default Applications;