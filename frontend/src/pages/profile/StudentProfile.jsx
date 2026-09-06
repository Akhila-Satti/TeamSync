import { useEffect, useState } from "react";
import api from "../../api/axios";
import { useNavigate, useParams } from "react-router-dom";
import "./StudentProfile.css";

function StudentProfile() {

  const { studentId } = useParams();
  const navigate = useNavigate();

  const [userName, setUserName] = useState("");
  const [bio, setBio] = useState("");
  const [experience, setExperience] = useState(0);
  const [availability, setAvailability] = useState("");
  const [skills, setSkills] = useState([]);
  const [error, setError] = useState("");

  const [showInvitePopup, setShowInvitePopup] = useState(false);
  const [showReferPopup, setShowReferPopup] = useState(false);

  const [createdProjects, setCreatedProjects] = useState([]);
  const [involvedProjects, setInvolvedProjects] = useState([]);

  const [loadingProjects, setLoadingProjects] = useState(false);
  const [actionMessage, setActionMessage] = useState("");
  const [actionError, setActionError] = useState("");

  useEffect(() => {

    const fetchDetails = async () => {

      try {

        setError("");

        const details = await api.get(
          `/api/students/profile/${studentId}`
        );

        setUserName(details.data.userName || "");
        setBio(details.data.bio || "");
        setExperience(details.data.experience ?? 0);
        setSkills(details.data.skills || []);
        setAvailability(details.data.availability || "");

      } catch (err) {

        setError(
          err.response?.data?.message ||
          "Failed to load profile"
        );

      }

    };

    fetchDetails();

  }, [studentId]);


  // ================= INVITE =================

  const openInvitePopup = async () => {

    setActionMessage("");
    setActionError("");
    setLoadingProjects(true);

    try {

      const res = await api.get(
        "/api/projects/my-projects"
      );

      setCreatedProjects(res.data || []);
      setShowInvitePopup(true);

    } catch (err) {

      setActionError(
        err.response?.data?.message ||
        "Failed to load your projects"
      );

    } finally {

      setLoadingProjects(false);

    }

  };


  const inviteToProject = async (projectId) => {

    setActionMessage("");
    setActionError("");

    try {

      const res = await api.post(
        `/api/projects/${projectId}/invitations`,
        {
          studentId: Number(studentId)
        }
      );

      setActionMessage(
        res.data?.message ||
        "Invitation sent successfully"
      );

      setShowInvitePopup(false);

    } catch (err) {

      setActionError(
        err.response?.data?.message ||
        "Failed to send invitation"
      );

    }

  };


  // ================= REFER =================

  const openReferPopup = async () => {

    setActionMessage("");
    setActionError("");
    setLoadingProjects(true);

    try {

      const res = await api.get(
        "/api/projects/involved-projects"
      );

      setInvolvedProjects(res.data || []);
      setShowReferPopup(true);

    } catch (err) {

      setActionError(
        err.response?.data?.message ||
        "Failed to load your involved projects"
      );

    } finally {

      setLoadingProjects(false);

    }

  };


  const referToProject = async (projectId) => {

    setActionMessage("");
    setActionError("");

    try {

      const res = await api.post(
        `/api/referrals/${projectId}`,
        {
          referredStudentId: Number(studentId)
        }
      );

      setActionMessage(
        res.data?.message ||
        "Referral created successfully"
      );

      setShowReferPopup(false);

    } catch (err) {

      setActionError(
        err.response?.data?.message ||
        "Failed to create referral"
      );

    }

  };


  return (
    <div className="student-profile-page">

      {/* ================= PROFILE ================= */}

      <div id="student-profile">

        <p className="profile-error">
          {error}
        </p>

        <h3>{userName}</h3>

        <h5>{bio}</h5>

        <h5>
          Experience: {experience}
        </h5>

        <h4>
          Availability: {availability}
        </h4>


        <section id="student-skills">

          {skills.map((skill) => (

            <span key={skill.skillId}>

              <p>{skill.skillName} -</p>

              <p>{skill.proficiency}</p>

            </span>

          ))}

        </section>

      </div>


      {/* ================= ACTION MESSAGE ================= */}

      {actionMessage && (
        <p className="profile-success">
          {actionMessage}
        </p>
      )}

      {actionError && (
        <p className="profile-action-error">
          {actionError}
        </p>
      )}


      {/* ================= ACTION BUTTONS ================= */}

      <div className="student-profile-actions">

        <button
          type="button"
          onClick={openInvitePopup}
        >
          Invite to a project
        </button>

        <button
          type="button"
          onClick={openReferPopup}
        >
          Refer to a project
        </button>

      </div>


      {/* ================= INVITE POPUP ================= */}

      {showInvitePopup && (

        <div className="modal-overlay">

          <div className="modal-content">

            <h3>
              Invite {userName}
            </h3>

            <p>
              Select one of your created projects:
            </p>


            {loadingProjects ? (

              <p>Loading projects...</p>

            ) : createdProjects.length === 0 ? (

              <p>
                You have not created any projects.
              </p>

            ) : (

              <div className="project-selection-list">

                {createdProjects.map((project) => (

                  <div
                    key={project.projectId}
                    className="project-selection-card"
                  >

                    <h4>
                      {project.name}
                    </h4>

                    <p>
                      {project.description}
                    </p>

                    <p>
                      <strong>Domain:</strong>{" "}
                      {project.domain}
                    </p>

                    <p>
                      <strong>Team:</strong>{" "}
                      {project.currentMemberCount}/
                      {project.desiredTeamSize}
                    </p>

                    <button
                      type="button"
                      onClick={() =>
                        inviteToProject(
                          project.projectId
                        )
                      }
                    >
                      Send Invitation
                    </button>

                  </div>

                ))}

              </div>

            )}


            <button
              type="button"
              className="modal-cancel-button"
              onClick={() =>
                setShowInvitePopup(false)
              }
            >
              Cancel
            </button>

          </div>

        </div>

      )}


      {/* ================= REFER POPUP ================= */}

      {showReferPopup && (

        <div className="modal-overlay">

          <div className="modal-content">

            <h3>
              Refer {userName}
            </h3>

            <p>
              Select one of your involved projects:
            </p>


            {loadingProjects ? (

              <p>Loading projects...</p>

            ) : involvedProjects.length === 0 ? (

              <p>
                You are not involved in any projects.
              </p>

            ) : (

              <div className="project-selection-list">

                {involvedProjects.map((project) => (

                  <div
                    key={project.projectId}
                    className="project-selection-card"
                  >

                    <h4>
                      {project.name}
                    </h4>

                    <p>
                      {project.description}
                    </p>

                    <p>
                      <strong>Created By:</strong>{" "}
                      {project.creatorName}
                    </p>

                    <p>
                      <strong>Domain:</strong>{" "}
                      {project.domain}
                    </p>

                    <p>
                      <strong>Team:</strong>{" "}
                      {project.currentMemberCount}/
                      {project.desiredTeamSize}
                    </p>

                    <button
                      type="button"
                      onClick={() =>
                        referToProject(
                          project.projectId
                        )
                      }
                    >
                      Create Referral
                    </button>

                  </div>

                ))}

              </div>

            )}


            <button
              type="button"
              className="modal-cancel-button"
              onClick={() =>
                setShowReferPopup(false)
              }
            >
              Cancel
            </button>

          </div>

        </div>

      )}

    </div>
  );
}

export default StudentProfile;