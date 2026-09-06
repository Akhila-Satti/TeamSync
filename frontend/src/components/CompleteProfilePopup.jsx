import { useEffect, useState } from "react";
import api from "../api/axios";
import "./CompleteProfilePopup.css";

function CompleteProfilePopup({ onComplete }) {

  const [skills, setSkills] = useState([]);
  const [selectedSkills, setSelectedSkills] = useState([]);

  const [skillId, setSkillId] = useState("");
  const [proficiency, setProficiency] = useState("BEGINNER");

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");

  useEffect(() => {
    const fetchSkills = async () => {
      try {
        const res = await api.get("/api/skills");
        setSkills(res.data);
      } catch (err) {
        setError(
          err.response?.data?.message ||
          "Failed to load skills"
        );
      } finally {
        setLoading(false);
      }
    };

    fetchSkills();
  }, []);

  const addSkill = () => {

    if (!skillId) {
      setError("Please select a skill");
      return;
    }

    const selectedSkill = skills.find(
      skill => skill.id === Number(skillId)
    );

    if (!selectedSkill) {
      return;
    }

    const alreadyAdded = selectedSkills.some(
      skill => skill.skillId === selectedSkill.id
    );

    if (alreadyAdded) {
      setError("Skill already added");
      return;
    }

    setSelectedSkills(prev => [
      ...prev,
      {
        skillId: selectedSkill.id,
        skillName: selectedSkill.name,
        proficiency
      }
    ]);

    setSkillId("");
    setProficiency("BEGINNER");
    setError("");
  };

  const removeSkill = (id) => {
    setSelectedSkills(prev =>
      prev.filter(skill => skill.skillId !== id)
    );
  };

  const completeProfile = async () => {

    setError("");
    setSaving(true);

    try {

      for (const skill of selectedSkills) {

        await api.post("/api/student-skills", {
          skillId: skill.skillId,
          proficiency: skill.proficiency
        });

      }

      await api.patch("/api/students/profile/complete");

      onComplete();

    } catch (err) {

      setError(
        err.response?.data?.message ||
        "Failed to complete profile"
      );

    } finally {
      setSaving(false);
    }
  };

  const skipProfile = async () => {

    setError("");
    setSaving(true);

    try {

      await api.patch("/api/students/profile/complete");

      onComplete();

    } catch (err) {

      setError(
        err.response?.data?.message ||
        "Failed to complete profile"
      );

    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="profile-popup-overlay">
        <div className="profile-popup">
          <p>Loading skills...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="profile-popup-overlay">

      <div className="profile-popup">

        <h2>Complete Your Profile</h2>

        <p className="profile-popup-description">
          Add your skills so TeamSync can help you find
          suitable projects and teammates.
        </p>

        {/* Add skill */}

        <div className="add-skill-row">

          <select
            value={skillId}
            onChange={(e) => setSkillId(e.target.value)}
          >
            <option value="">
              Select a skill
            </option>

            {skills.map(skill => (
              <option
                key={skill.id}
                value={skill.id}
              >
                {skill.name}
              </option>
            ))}
          </select>

          <select
            value={proficiency}
            onChange={(e) =>
              setProficiency(e.target.value)
            }
          >
            <option value="">Select Proficiency</option>
            <option value="1">1 - Beginner</option>
            <option value="2">2</option>
            <option value="3">3 - Intermediate</option>
            <option value="4">4</option>
            <option value="5">5 - Advanced</option>
          </select>

          <button
            type="button"
            className="add-skill-button"
            onClick={addSkill}
          >
            Add
          </button>

        </div>

        {/* Selected skills */}

        <div className="selected-skills">

          {selectedSkills.length === 0 ? (

            <p className="no-skills">
              No skills added yet.
            </p>

          ) : (

            selectedSkills.map(skill => (

              <div
                className="selected-skill"
                key={skill.skillId}
              >

                <div>
                  <strong>
                    {skill.skillName}
                  </strong>

                  <span>
                    {skill.proficiency}
                  </span>
                </div>

                <button
                  type="button"
                  onClick={() =>
                    removeSkill(skill.skillId)
                  }
                >
                  ×
                </button>

              </div>

            ))

          )}

        </div>

        {error && (
          <p className="form-error">
            {error}
          </p>
        )}

        {/* Actions */}

        <div className="profile-popup-actions">

          <button
            type="button"
            className="skip-button"
            onClick={skipProfile}
            disabled={saving}
          >
            Skip for now
          </button>

          <button
            type="button"
            className="primary-button"
            onClick={completeProfile}
            disabled={saving}
          >
            {saving ? "Saving..." : "Continue"}
          </button>

        </div>

      </div>

    </div>
  );
}

export default CompleteProfilePopup;