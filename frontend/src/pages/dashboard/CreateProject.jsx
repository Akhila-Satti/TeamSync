import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../api/axios";

import "./CreateProject.css";
function CreateProject() {
  const navigate = useNavigate();

  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [domain, setDomain] = useState("");
  const [desiredTeamSize, setDesiredTeamSize] = useState("");

  const [skills, setSkills] = useState([]);
  const [selectedSkills, setSelectedSkills] = useState([]);

  const [selectedSkillId, setSelectedSkillId] = useState("");
  const [importance, setImportance] = useState("");

  const [error, setError] = useState("");

  // Fetch available skills
  useEffect(() => {
    const fetchSkills = async () => {
      try {
        const res = await api.get("/api/skills");

        setSkills(res.data);
      } catch (err) {
        setError("Failed to load skills");
      }
    };

    fetchSkills();
  }, []);

  // Add selected skill
  const addSkill = () => {
    if (!selectedSkillId) {
      setError("Please select a skill");
      return;
    }

    if (!importance) {
      setError("Please select skill importance");
      return;
    }

    // Prevent duplicate skill
    const alreadyAdded = selectedSkills.some(
      (skill) => skill.skillId === Number(selectedSkillId),
    );

    if (alreadyAdded) {
      setError("This skill has already been added");
      return;
    }

    const selectedSkill = skills.find(
      (skill) => skill.id === Number(selectedSkillId),
    );

    setSelectedSkills([
      ...selectedSkills,
      {
        skillId: selectedSkill.id,
        skillName: selectedSkill.name,
        importance: Number(importance),
      },
    ]);

    // Reset skill selection
    setSelectedSkillId("");
    setImportance("");
    setError("");
  };

  // Remove skill
  const removeSkill = (skillId) => {
    setSelectedSkills(
      selectedSkills.filter((skill) => skill.skillId !== skillId),
    );
  };

  // Form submit
  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");

    if (!name.trim()) {
      setError("Project name is required");
      return;
    }

    if (name.trim().length < 2) {
      setError("Project name must be at least 2 characters");
      return;
    }

    if (!domain) {
      setError("Please select a domain");
      return;
    }

    if (!desiredTeamSize || Number(desiredTeamSize) <= 0) {
      setError("Team size must be greater than 0");
      return;
    }

    if (selectedSkills.length === 0) {
      setError("Please add at least one skill");
      return;
    }

    const projectData = {
      name: name.trim(),
      description: description,
      domain: domain,
      desiredTeamSize: Number(desiredTeamSize),
      projectSkills: selectedSkills.map((skill) => ({
        skillId: skill.skillId,
        importance: skill.importance,
      })),
    };

    try {
      const res = await api.post("/api/projects/create-project", projectData);

      alert(res.data.message);

      navigate("/dashboard/my-projects");
    } catch (err) {
      if (err.response && err.response.data) {
        setError(err.response.data.message || "Failed to create project");
      } else {
        setError("Something went wrong");
      }
    }
  };

  return (
    <div className="create-project-page">
  <h2>Create Project</h2>

  {error && (
    <p className="create-project-error">
      {error}
    </p>
  )}

  <form className="create-project-form" onSubmit={handleSubmit}>
        {/* Project Name */}
        <div>
          <label>Project Name</label>

          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Enter project name"
          />
        </div>

        {/* Description */}
        <div>
          <label>Description</label>

          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Describe your project"
          />
        </div>

        {/* Domain */}
        <div>
          <label>Domain</label>

          <select value={domain} onChange={(e) => setDomain(e.target.value)}>
            <option value="">Select Domain</option>

            <option value="Web Development">Web Development</option>

            <option value="AI/ML">AI / ML</option>

            <option value="Mobile Development">Mobile Development</option>

            <option value="Cloud">Cloud</option>

            <option value="Cybersecurity">Cybersecurity</option>

            <option value="Data Science">Data Science</option>

            <option value="Other">Other</option>
          </select>
        </div>

        {/* Team Size */}
        <div>
          <label>Desired Team Size</label>

          <input
            type="number"
            min="1"
            value={desiredTeamSize}
            onChange={(e) => setDesiredTeamSize(e.target.value)}
            placeholder="Enter team size"
          />
        </div>

        {/* Add Skills */}
        <div>
          <h3>Project Skills</h3>

          <select
            value={selectedSkillId}
            onChange={(e) => setSelectedSkillId(e.target.value)}
          >
            <option value="">Select Skill</option>

            {skills.map((skill) => (
              <option key={skill.id} value={skill.id}>
                {skill.name}
              </option>
            ))}
          </select>

          <select
            value={importance}
            onChange={(e) => setImportance(e.target.value)}
          >
            <option value="">Select Importance</option>

            <option value="1">1 - Low</option>
            <option value="2">2</option>
            <option value="3">3 - Medium</option>
            <option value="4">4</option>
            <option value="5">5 - High</option>
          </select>

          <button type="button" onClick={addSkill}>
            Add Skill
          </button>
        </div>

        {/* Selected Skills */}
        <div>
          <h3>Selected Skills</h3>

          {selectedSkills.length === 0 ? (
            <p>No skills added yet.</p>
          ) : (
            <ul>
              {selectedSkills.map((skill) => (
                <li key={skill.skillId}>
                  {skill.skillName}
                  {" - "}
                  Importance: {skill.importance}
                  <button
                    type="button"
                    onClick={() => removeSkill(skill.skillId)}
                  >
                    Remove
                  </button>
                </li>
              ))}
            </ul>
          )}
        </div>

        {/* Submit */}
        <button type="submit">Create Project</button>

        {/* Cancel */}
        <button
          type="button"
          onClick={() => navigate("/dashboard/my-projects")}
        >
          Cancel
        </button>
      </form>
    </div>
  );
}

export default CreateProject;
