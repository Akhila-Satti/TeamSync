import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../../api/axios";
import "./Recommendations.css";

function Recommendations() {
  const { projectId } = useParams();
  const navigate = useNavigate();

  const [recommendations, setRecommendations] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchRecommendations = async () => {
      try {
        setError("");

        const res = await api.get(`/api/recommendations/${projectId}`);

        if (res.data.length === 0) {
          setError("No recommendations available");
          return;
        }

        setRecommendations(res.data);
      } catch (err) {
        setError(
          err.response?.data?.message || "Failed to load recommendations",
        );
      }
    };

    fetchRecommendations();
  }, [projectId]);

  const openProfile = (studentId) => {
    navigate(`/dashboard/profile/${studentId}`);
  };

  return (
    <>
      <div className="recommendations-page">
        <button
          type="button"
          className="back-button"
          onClick={() => navigate(-1)}
        >
          Back
        </button>

        <h2 className="recommendations-title">Recommendations</h2>


        {error && <p className="page-error">{error}</p>}

        {recommendations.map((recommendation) => (
          <div key={recommendation.studentId} className="recommendation-card">
            <p>
              <strong>Student:</strong>{" "}
              <span
                className="recommendation-student"
                onClick={() => openProfile(recommendation.studentId)}
              >
                {recommendation.userName}
              </span>
            </p>

            <p>
              <strong>Experience:</strong> {recommendation.experience}
            </p>

            <p>
              <strong>Matching Score:</strong>{" "}
              {recommendation.matchingScore.toFixed(2)}%
            </p>

            <h4 className="skills-title">Matched Skills</h4>

            {recommendation.matchedSkills.length === 0 ? (
              <p>No matched skills</p>
            ) : (
              <ul className="skills-list">
                {recommendation.matchedSkills.map((skill) => (
                  <li key={skill.skillName}>
                    {skill.skillName}
                    {" - "}
                    Proficiency: {skill.proficiency}
                  </li>
                ))}
              </ul>
            )}

            <h4 className="skills-title">Missing Skills</h4>

            {recommendation.missingSkills.length === 0 ? (
              <p>No missing skills</p>
            ) : (
              <ul className="skills-list">
                {recommendation.missingSkills.map((skill) => (
                  <li key={skill.skillName}>{skill.skillName}</li>
                ))}
              </ul>
            )}
          </div>
        ))}
      </div>
    </>
  );
}

export default Recommendations;
