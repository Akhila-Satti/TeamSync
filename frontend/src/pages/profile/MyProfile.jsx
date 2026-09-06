import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../api/axios";
import "./MyProfile.css";
function MyProfile() {
  const navigate = useNavigate();

  // ================= PROFILE =================

  const [profile, setProfile] = useState(null);
  const [loadingProfile, setLoadingProfile] = useState(true);
  const [showCurrentPassword, setShowCurrentPassword] = useState(false);
const [showNewPassword, setShowNewPassword] = useState(false);
  const [profileError, setProfileError] = useState("");

  // ================= EDIT PROFILE =================

  const [showEditProfile, setShowEditProfile] = useState(false);
  const [bio, setBio] = useState("");
  const [experience, setExperience] = useState("");
  const [availability, setAvailability] = useState("");
  const [updatingProfile, setUpdatingProfile] = useState(false);
  const [updateProfileError, setUpdateProfileError] = useState("");

  // ================= CHANGE PASSWORD =================

  const [showChangePassword, setShowChangePassword] = useState(false);
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [changingPassword, setChangingPassword] = useState(false);
  const [passwordError, setPasswordError] = useState("");

  // ================= SKILLS =================

  const [allSkills, setAllSkills] = useState([]);
  const [selectedSkillId, setSelectedSkillId] = useState("");
  const [skillProficiency, setSkillProficiency] = useState("");
  const [addingSkill, setAddingSkill] = useState(false);
  const [skillError, setSkillError] = useState("");

  // ================= APPLICATIONS =================

  const [applications, setApplications] = useState([]);
  const [loadingApplications, setLoadingApplications] = useState(true);
  const [applicationsError, setApplicationsError] = useState("");

  // ================= INVITATIONS =================

  const [invitations, setInvitations] = useState([]);
  const [loadingInvitations, setLoadingInvitations] = useState(true);
  const [invitationsError, setInvitationsError] = useState("");

  // =========================================================
  // FETCH PROFILE
  // =========================================================

  useEffect(() => {
    fetchProfile();
    fetchApplications();
    fetchInvitations();
    fetchSkills();
  }, []);

  const fetchProfile = async () => {
    try {
      setLoadingProfile(true);
      setProfileError("");

      const response = await api.get("/api/students/me");

      setProfile(response.data);

      setBio(response.data.bio || "");
      setExperience(
        response.data.experience !== null &&
          response.data.experience !== undefined
          ? response.data.experience
          : "",
      );
      setAvailability(response.data.availability || "");
    } catch (error) {
      console.error(error);
      setProfileError("Failed to load profile.");
    } finally {
      setLoadingProfile(false);
    }
  };

  // =========================================================
  // FETCH ALL SKILLS
  // =========================================================

  const fetchSkills = async () => {
    try {
      const response = await api.get("/api/skills");
      setAllSkills(response.data);
    } catch (error) {
      console.error(error);
    }
  };

  // =========================================================
  // FETCH APPLICATIONS
  // =========================================================

  const fetchApplications = async () => {
    try {
      setLoadingApplications(true);
      setApplicationsError("");

      const response = await api.get("/api/applications/my-applications");

      setApplications(response.data);
    } catch (error) {
      console.error(error);
      setApplicationsError("Failed to load applications.");
    } finally {
      setLoadingApplications(false);
    }
  };

  // =========================================================
  // FETCH INVITATIONS
  // =========================================================

  const fetchInvitations = async () => {
    try {
      setLoadingInvitations(true);
      setInvitationsError("");

      const response = await api.get("/api/invitations/my-invitations");

      setInvitations(response.data);
    } catch (error) {
      console.error(error);
      setInvitationsError("Failed to load invitations.");
    } finally {
      setLoadingInvitations(false);
    }
  };

  // =========================================================
  // EDIT PROFILE
  // =========================================================

  const openEditProfile = () => {
    setBio(profile.bio || "");

    setExperience(
      profile.experience !== null && profile.experience !== undefined
        ? profile.experience
        : "",
    );

    setAvailability(profile.availability || "");

    setUpdateProfileError("");
    setShowEditProfile(true);
  };

  const updateProfile = async (e) => {
    e.preventDefault();

    try {
      setUpdatingProfile(true);
      setUpdateProfileError("");

      const response = await api.patch(
        `/api/students/profile/${profile.studentId}`,
        {
          bio: bio,
          experience: experience === "" ? null : Number(experience),
          availability: availability === "" ? null : availability,
        },
      );

      alert(response.data.message);

      setShowEditProfile(false);

      await fetchProfile();
    } catch (error) {
      console.error(error);

      setUpdateProfileError(
        error.response?.data?.message || "Failed to update profile.",
      );
    } finally {
      setUpdatingProfile(false);
    }
  };

  // =========================================================
  // CHANGE PASSWORD
  // =========================================================

  const changePassword = async (e) => {
    e.preventDefault();

    try {
      setChangingPassword(true);
      setPasswordError("");

      const response = await api.post("/api/students/change-password", {
        currentPassword: currentPassword,
        newPassword: newPassword,
      });

      alert(response.data.message);

      setCurrentPassword("");
      setNewPassword("");
      setShowChangePassword(false);
    } catch (error) {
      console.error(error);

      setPasswordError(
        error.response?.data?.message || "Failed to change password.",
      );
    } finally {
      setChangingPassword(false);
    }
  };

  // =========================================================
  // ADD SKILL
  // =========================================================

  const addSkill = async (e) => {
    e.preventDefault();

    if (!selectedSkillId || !skillProficiency) {
      setSkillError("Please select a skill and proficiency.");
      return;
    }

    try {
      setAddingSkill(true);
      setSkillError("");

      const response = await api.post("/api/student-skills", {
        skillId: Number(selectedSkillId),
        proficiency: Number(skillProficiency),
      });

      alert(response.data.message);

      setSelectedSkillId("");
      setSkillProficiency("");

      await fetchProfile();
    } catch (error) {
      console.error(error);

      setSkillError(error.response?.data?.message || "Failed to add skill.");
    } finally {
      setAddingSkill(false);
    }
  };

  // =========================================================
  // UPDATE SKILL
  // =========================================================

  const updateSkill = async (skillId, currentProficiency) => {
    const newProficiency = prompt(
      "Enter new proficiency (1-5):",
      currentProficiency,
    );

    if (newProficiency === null) {
      return;
    }

    const proficiency = Number(newProficiency);

    if (!Number.isInteger(proficiency) || proficiency < 1 || proficiency > 5) {
      alert("Proficiency must be between 1 and 5.");
      return;
    }

    try {
      const response = await api.patch(`/api/student-skills/${skillId}`, {
        proficiency: proficiency,
      });

      alert(response.data.message);

      await fetchProfile();
    } catch (error) {
      console.error(error);

      alert(error.response?.data?.message || "Failed to update skill.");
    }
  };

  // =========================================================
  // DELETE SKILL
  // =========================================================

  const deleteSkill = async (skillId) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this skill?",
    );

    if (!confirmed) {
      return;
    }

    try {
      const response = await api.delete(`/api/student-skills/${skillId}`);

      alert(response.data.message);

      await fetchProfile();
    } catch (error) {
      console.error(error);

      alert(error.response?.data?.message || "Failed to delete skill.");
    }
  };

  // =========================================================
  // INVITATION STATUS
  // =========================================================

  const updateInvitationStatus = async (invitationId, status) => {
    try {
      const response = await api.patch(
        `/api/invitations/${invitationId}/status`,
        {
          status: status,
        },
      );

      alert(response.data.message);

      await fetchInvitations();
    } catch (error) {
      console.error(error);

      alert(error.response?.data?.message || "Failed to update invitation.");
    }
  };

  // =========================================================
  // LOGOUT
  // =========================================================

  const logout = () => {
    localStorage.removeItem("token");
    navigate("/");
  };

  // =========================================================
  // DATE FORMAT
  // =========================================================

  const formatDate = (dateTime) => {
    return new Date(dateTime).toLocaleString();
  };

  // =========================================================
  // LOADING PROFILE
  // =========================================================

  if (loadingProfile) {
    return <div>Loading profile...</div>;
  }

  if (profileError) {
    return <div>{profileError}</div>;
  }

  if (!profile) {
    return <div>Profile not found.</div>;
  }

  // =========================================================
  // UI
  // =========================================================

  return (
    <div className="my-profile-page">
      {/* ================= PROFILE ================= */}

      <h2 className="my-profile-title">My Profile</h2>

      <div className="profile-info-card">
        <p>
          <strong>Username:</strong> {profile.userName}
        </p>

        <p>
          <strong>Email:</strong> {profile.email}
        </p>

        <p>
          <strong>Experience:</strong> {profile.experience ?? "Not provided"}
        </p>

        <p>
          <strong>Bio:</strong> {profile.bio || "Not provided"}
        </p>

        <p>
          <strong>Availability:</strong>{" "}
          {profile.availability || "Not provided"}
        </p>
      </div>

      {/* ================= PROFILE BUTTONS ================= */}

      <div className="profile-actions">
        <button onClick={openEditProfile}>Edit Profile</button>

        <button
          onClick={() => {
            setPasswordError("");
            setCurrentPassword("");
            setNewPassword("");
            setShowChangePassword(true);
          }}
        >
          Change Password
        </button>

        <button className="logout-button" onClick={logout}>
          Logout
        </button>
      </div>
      {/* ================= SKILLS ================= */}

      <hr />
      <section className="profile-section">
        <h3>My Skills</h3>

        {profile.skills && profile.skills.length > 0 ? (
          <div className="my-skills-list">
            {profile.skills.map((skill) => (
              <div className="my-skill-card" key={skill.skillId}>
                <div>
                  <strong>{skill.skillName}</strong>
                  <span>Proficiency: {skill.proficiency}</span>
                </div>

                <div className="skill-actions">
                  <button
                    onClick={() =>
                      updateSkill(skill.skillId, skill.proficiency)
                    }
                  >
                    Edit
                  </button>

                  <button
                    className="delete-button"
                    onClick={() => deleteSkill(skill.skillId)}
                  >
                    Delete
                  </button>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p className="empty-message">No skills added yet.</p>
        )}

        {/* ================= ADD SKILL ================= */}

        <h4 className="subsection-title">Add Skill</h4>

        {skillError && <p className="section-error">{skillError}</p>}

        <form className="add-skill-form" onSubmit={addSkill}>
          <select
            value={selectedSkillId}
            onChange={(e) => setSelectedSkillId(e.target.value)}
          >
            <option value="">Select Skill</option>

            {allSkills.map((skill) => (
              <option key={skill.id} value={skill.id}>
                {skill.name}
              </option>
            ))}
          </select>

          <select
            value={skillProficiency}
            onChange={(e) => setSkillProficiency(e.target.value)}
          >
            <option value="">Select Proficiency</option>
            <option value="1">1 - Beginner</option>
            <option value="2">2</option>
            <option value="3">3 - Intermediate</option>
            <option value="4">4</option>
            <option value="5">5 - Advanced</option>
          </select>

          <button type="submit" disabled={addingSkill}>
            {addingSkill ? "Adding..." : "Add Skill"}
          </button>
        </form>
      </section>

      {/* ================= MY APPLICATIONS ================= */}

      <hr />
      <section className="profile-section">
        <h2>My Applications</h2>

        {loadingApplications && <p>Loading applications...</p>}

        {applicationsError && <p>{applicationsError}</p>}

        {!loadingApplications &&
          !applicationsError &&
          applications.length === 0 && (
            <p>You have not applied to any projects.</p>
          )}

        {!loadingApplications &&
          !applicationsError &&
          applications.length > 0 && (
            <div>
              {applications.map((application) => (
                <div
                  className="application-card"
                  key={application.applicationId}
                >
                  <h3>{application.projectName}</h3>

                  <p>
                    <strong>Description:</strong>{" "}
                    {application.projectDescription}
                  </p>

                  <p>
                    <strong>Applied on:</strong> {formatDate(application.date)}
                  </p>

                  <p>
                    <strong>Status:</strong> {application.status}
                  </p>
                </div>
              ))}
            </div>
          )}
      </section>
      {/* ================= MY INVITATIONS ================= */}

      <hr />
      <section className="profile-section">
        <h2>My Invitations</h2>

        {loadingInvitations && <p>Loading invitations...</p>}

        {invitationsError && <p>{invitationsError}</p>}

        {!loadingInvitations &&
          !invitationsError &&
          invitations.length === 0 && <p>You have no invitations.</p>}

        {!loadingInvitations && !invitationsError && invitations.length > 0 && (
          <div>
            {invitations.map((invitation) => (
              <div className="invitation-card" key={invitation.invitationId}>
                <h3>{invitation.projectName}</h3>

                <p>
                  <strong>Description:</strong> {invitation.description}
                </p>

                <p>
                  <strong>Invited by:</strong> {invitation.sentBy}
                </p>

                <p>
                  <strong>Sent at:</strong> {formatDate(invitation.sentAt)}
                </p>

                <p>
                  <strong>Status:</strong> {invitation.status}
                </p>

                <h4>Project Skills</h4>

                {invitation.projectSkills &&
                invitation.projectSkills.length > 0 ? (
                  <ul>
                    {invitation.projectSkills.map((skill) => (
                      <li key={skill.id}>{skill.name}</li>
                    ))}
                  </ul>
                ) : (
                  <p>No project skills.</p>
                )}

                {invitation.status === "PENDING" && (
                  <div className="invitation-actions">
                    <button
                      className="accept-button"
                      onClick={() =>
                        updateInvitationStatus(
                          invitation.invitationId,
                          "ACCEPTED",
                        )
                      }
                    >
                      Accept
                    </button>

                    <button
                      className="reject-button"
                      onClick={() =>
                        updateInvitationStatus(
                          invitation.invitationId,
                          "REJECTED",
                        )
                      }
                    >
                      Reject
                    </button>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </section>

      {/* ================= EDIT PROFILE FORM ================= */}

      {showEditProfile && (
        <div className="profile-modal-overlay">
          <div className="profile-modal">
            <h2>Edit Profile</h2>

            {updateProfileError && (
              <p className="section-error">{updateProfileError}</p>
            )}

            <form onSubmit={updateProfile}>
              <div>
                <label>Bio</label>

                <textarea
                  value={bio}
                  onChange={(e) => setBio(e.target.value)}
                />
              </div>

              <div>
                <label>Experience</label>

                <input
                  type="number"
                  min="0"
                  value={experience}
                  onChange={(e) => setExperience(e.target.value)}
                />
              </div>

              <div>
                <label>Availability</label>

                <select
                  value={availability}
                  onChange={(e) => setAvailability(e.target.value)}
                >
                  <option value="">Select Availability</option>
                  <option value="AVAILABLE">Available</option>
                  <option value="BUSY">Busy</option>
                  <option value="NOT_AVAILABLE">Not Available</option>
                </select>
              </div>

              <button type="submit" disabled={updatingProfile}>
                {updatingProfile ? "Updating..." : "Save Changes"}
              </button>

              <button type="button" onClick={() => setShowEditProfile(false)}>
                Cancel
              </button>
            </form>
          </div>
        </div>
      )}

      {/* ================= CHANGE PASSWORD FORM ================= */}

      {showChangePassword && (
        <div className="profile-modal-overlay">
          <div className="profile-modal">
            <h2>Change Password</h2>

            {passwordError && <p className="section-error">{passwordError}</p>}

            <form onSubmit={changePassword}>
              <div>
                <label>Current Password</label>

              <div className="password-input-container">
  <input 
    type={showCurrentPassword ? "text" : "password"} 
    value={currentPassword} 
    onChange={(e) => setCurrentPassword(e.target.value)} 
    required 
  />

  <button
    type="button"
    className="password-toggle"
    onClick={() => setShowCurrentPassword(!showCurrentPassword)}
  >
    {showCurrentPassword ? "~_~" : "^_^"}
  </button>
</div>
              </div>

              <div>
                <label>New Password</label>

                <div className="password-input-container">
  <input 
    type={showNewPassword ? "text" : "password"} 
    value={newPassword} 
    onChange={(e) => setNewPassword(e.target.value)} 
    required 
  />

  <button
    type="button"
    className="password-toggle"
    onClick={() => setShowNewPassword(!showNewPassword)}
  >
    {showNewPassword ? "~_~" : "^_^"}
  </button>
</div>
              </div>

              <button type="submit" disabled={changingPassword}>
                {changingPassword ? "Changing..." : "Change Password"}
              </button>

              <button
                type="button"
                onClick={() => setShowChangePassword(false)}
              >
                Cancel
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default MyProfile;
