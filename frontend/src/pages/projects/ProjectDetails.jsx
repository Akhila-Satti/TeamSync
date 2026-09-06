import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../../api/axios";
import "./ProjectDetails.css";

function ProjectDetails() {

    const { projectId } = useParams();
    const navigate = useNavigate();

    // =========================
    // DATA
    // =========================

    const [project, setProject] = useState(null);
    const [members, setMembers] = useState([]);
    const [currentUser, setCurrentUser] = useState(null);

    const [loading, setLoading] = useState(true);

    // =========================
    // EDIT PROJECT
    // =========================

    const [showEditProject, setShowEditProject] = useState(false);

    const [editProject, setEditProject] = useState({
        name: "",
        description: "",
        domain: "",
        desiredTeamSize: ""
    });

    // =========================
    // ADD SKILL
    // =========================

    const [skills, setSkills] = useState([]);

    const [selectedSkillId, setSelectedSkillId] = useState("");
    const [skillImportance, setSkillImportance] = useState("");

    // =========================
    // EDIT SKILL
    // =========================

    const [showEditSkill, setShowEditSkill] = useState(false);
    const [editingSkill, setEditingSkill] = useState(null);
    const [editingImportance, setEditingImportance] = useState("");

    // =========================
    // FETCH DATA
    // =========================

    useEffect(() => {
        fetchData();
    }, [projectId]);

    const fetchData = async () => {

        try {

            setLoading(true);

            const [userResponse, projectResponse, membersResponse, skillsResponse] =
                await Promise.all([
                    api.get("/api/students/me"),
                    api.get(`/api/projects/${projectId}`),
                    api.get(`/api/projects/${projectId}/members`),
                    api.get("/api/skills")
                ]);

            setCurrentUser(userResponse.data);
            setProject(projectResponse.data);
            setMembers(membersResponse.data);
            setSkills(skillsResponse.data);

        } catch (error) {

            console.error(error);

            alert(
                error.response?.data?.message ||
                "Failed to load project details"
            );

        } finally {

            setLoading(false);
        }
    };

    // =========================
    // CREATOR CHECK
    // =========================

    const isCreator =
        currentUser &&
        project &&
        currentUser.studentId === project.creatorId;

    // =========================
    // EDIT PROJECT
    // =========================

    const openEditProject = () => {

        setEditProject({
            name: project.name || "",
            description: project.description || "",
            domain: project.domain || "",
            desiredTeamSize: project.desiredTeamSize || ""
        });

        setShowEditProject(true);
    };

    const handleEditProjectChange = (e) => {

        const { name, value } = e.target;

        setEditProject(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const updateProject = async () => {

        try {

            const response = await api.patch(
                `/api/projects/${projectId}/update`,
                {
                    name: editProject.name,
                    description: editProject.description,
                    domain: editProject.domain,
                    desiredTeamSize: Number(editProject.desiredTeamSize)
                }
            );

            alert(
                response.data.message ||
                "Project updated successfully"
            );

            setShowEditProject(false);

            await fetchData();

        } catch (error) {

            console.error(error);

            alert(
                error.response?.data?.message ||
                "Failed to update project"
            );
        }
    };

    // =========================
    // CLOSE PROJECT
    // =========================

    const closeProject = async () => {

        const confirmClose = window.confirm(
            "Are you sure you want to close this project?"
        );

        if (!confirmClose) {
            return;
        }

        try {

            const response = await api.patch(
                `/api/projects/${projectId}/close`
            );

            alert(
                response.data.message ||
                "Project closed successfully"
            );

            await fetchData();

        } catch (error) {

            console.error(error);

            alert(
                error.response?.data?.message ||
                "Failed to close project"
            );
        }
    };

    // =========================
    // REMOVE MEMBER
    // =========================

    const removeMember = async (studentId, userName) => {

        const confirmRemove = window.confirm(
            `Are you sure you want to remove ${userName} from the project?`
        );

        if (!confirmRemove) {
            return;
        }

        try {

            const response = await api.patch(
                `/api/projects/${projectId}/members/${studentId}/remove`
            );

            alert(
                response.data.message ||
                "Member removed successfully"
            );

            setMembers(prev =>
                prev.filter(member =>
                    member.studentId !== studentId
                )
            );

            // Refresh project because member count may have changed
            const projectResponse =
                await api.get(`/api/projects/${projectId}`);

            setProject(projectResponse.data);

        } catch (error) {

            console.error(error);

            alert(
                error.response?.data?.message ||
                "Failed to remove member"
            );
        }
    };

    // =========================
    // ADD SKILL
    // =========================

    const addSkill = async () => {

        if (!selectedSkillId) {

            alert("Please select a skill");

            return;
        }

        if (!skillImportance) {

            alert("Please enter skill importance");

            return;
        }

        const importance = Number(skillImportance);

        if (importance < 1 || importance > 5) {

            alert("Importance must be between 1 and 5");

            return;
        }

        try {

            const response = await api.post(
                `/api/projects/skill/${projectId}`,
                {
                    skillId: Number(selectedSkillId),
                    importance: importance
                }
            );

            alert(
                response.data.message ||
                "Skill added successfully"
            );

            setSelectedSkillId("");
            setSkillImportance("");

            await fetchData();

        } catch (error) {

            console.error(error);

            alert(
                error.response?.data?.message ||
                "Failed to add skill"
            );
        }
    };

    // =========================
    // OPEN EDIT SKILL
    // =========================

    const openEditSkill = (skill) => {

        setEditingSkill(skill);
        setEditingImportance(skill.importance);

        setShowEditSkill(true);
    };

    // =========================
    // UPDATE SKILL
    // =========================

    const updateSkill = async () => {

        const importance = Number(editingImportance);

        if (importance < 1 || importance > 5) {

            alert("Importance must be between 1 and 5");

            return;
        }

        try {

            const response = await api.patch(
                `/api/projects/skill/${editingSkill.projectSkillId}`,
                {
                    importance: importance
                }
            );

            alert(
                response.data.message ||
                "Skill importance updated successfully"
            );

            setShowEditSkill(false);
            setEditingSkill(null);
            setEditingImportance("");

            await fetchData();

        } catch (error) {

            console.error(error);

            alert(
                error.response?.data?.message ||
                "Failed to update skill"
            );
        }
    };

    // =========================
    // DELETE SKILL
    // =========================

    const deleteSkill = async (projectSkillId) => {

        const confirmDelete = window.confirm(
            "Are you sure you want to delete this skill?"
        );

        if (!confirmDelete) {
            return;
        }

        try {

            const response = await api.delete(
                `/api/projects/skill/${projectSkillId}`
            );

            alert(
                response.data.message ||
                "Skill deleted successfully"
            );

            await fetchData();

        } catch (error) {

            console.error(error);

            alert(
                error.response?.data?.message ||
                "Failed to delete skill"
            );
        }
    };

    // =========================
    // LEAVE PROJECT
    // =========================

    const leaveProject = async () => {

        const confirmLeave = window.confirm(
            "Are you sure you want to leave this project?"
        );

        if (!confirmLeave) {
            return;
        }

        try {

            const response = await api.patch(
                `/api/projects/${projectId}/leave`
            );

            alert(
                response.data.message ||
                "You left the project successfully"
            );

            navigate("/dashboard/my-projects");

        } catch (error) {

            console.error(error);

            alert(
                error.response?.data?.message ||
                "Failed to leave project"
            );
        }
    };

    // =========================
    // LOADING
    // =========================

    if (loading) {

        return (
            <div>
                <h2>Loading project...</h2>
            </div>
        );
    }

    // =========================
    // PROJECT NOT FOUND
    // =========================

    if (!project) {

        return (
            <div>
                <h2>Project not found</h2>
            </div>
        );
    }

    const isClosed = project.status === "CLOSED";

    return (
        <div className="project-details-page">

            <button
  className="back-button"
  onClick={() => navigate(-1)}
>
  ← Back
</button>


            <div className="project-header">

                <h1>{project.name}</h1>

                <p>
                    Status: {project.status}
                </p>

                <p>
                    Created by: {project.creatorName}
                </p>

                <p>
                    Created on:{" "}
                    {project.createdAt
                        ? new Date(project.createdAt).toLocaleString()
                        : "N/A"}
                </p>

            </div>


            {/* =========================
                PROJECT INFORMATION
            ========================= */}

            <div className="project-info-section">

                <h2>Project Details</h2>

                <p>
                    <strong>Description:</strong>{" "}
                    {project.description || "No description"}
                </p>

                <p>
                    <strong>Domain:</strong>{" "}
                    {project.domain}
                </p>

                <p>
                    <strong>Desired Team Size:</strong>{" "}
                    {project.desiredTeamSize}
                </p>

                <p>
                    <strong>Current Members:</strong>{" "}
                    {project.currentMemberCount}
                </p>

            </div>


            {/* =========================
                CREATOR CONTROLS
            ========================= */}

            {isCreator && !isClosed && (

                <div className="project-management">

                    <h2>Project Management</h2>

                    <button onClick={openEditProject}>
                        Edit Project
                    </button>

                    <button onClick={closeProject}>
                        Close Project
                    </button>

                </div>

            )}


            {/* =========================
                SKILLS
            ========================= */}

            <div className="project-skills-section">

                <h2>Project Skills</h2>

                {project.skills && project.skills.length > 0 ? (

                    project.skills.map(skill => (

                        <div className="project-skill" key={skill.projectSkillId}>

                            <span>
                                {skill.skillName}
                            </span>

                            <span>
                                {" "}
                                - Importance: {skill.importance}
                            </span>

                            {isCreator && !isClosed && (

                                <>

                                    <button
                                        onClick={() =>
                                            openEditSkill(skill)
                                        }
                                    >
                                        Edit
                                    </button>

                                    <button
                                        onClick={() =>
                                            deleteSkill(
                                                skill.projectSkillId
                                            )
                                        }
                                    >
                                        Delete
                                    </button>

                                </>

                            )}

                        </div>

                    ))

                ) : (

                    <p>No skills added.</p>

                )}

            </div>


            {/* =========================
                ADD SKILL
            ========================= */}

            {isCreator && !isClosed && (

                <div className="add-skill-section">

                    <h3>Add Skill</h3>

                    <select
                        value={selectedSkillId}
                        onChange={(e) =>
                            setSelectedSkillId(e.target.value)
                        }
                    >

                        <option value="">
                            Select Skill
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

                    <input
                        type="number"
                        min="1"
                        max="5"
                        placeholder="Importance (1-5)"
                        value={skillImportance}
                        onChange={(e) =>
                            setSkillImportance(e.target.value)
                        }
                    />

                    <button onClick={addSkill}>
                        Add Skill
                    </button>

                </div>

            )}


            {/* =========================
                MEMBERS
            ========================= */}

            <div className="project-members-section">

                <h2>Project Members</h2>

                {members.length === 0 ? (

                    <p>No members found.</p>

                ) : (

                    members.map(member => (

                        <div className="member-card" key={member.studentId}>

                            <p>
                                <strong>
                                    {member.userName}
                                </strong>
                            </p>

                            <p>
                                Role: {member.role}
                            </p>

                            <p>
                                Joined:{" "}
                                {member.joinedAt
                                    ? new Date(
                                        member.joinedAt
                                    ).toLocaleString()
                                    : "N/A"}
                            </p>

                            {isCreator &&
                                !isClosed &&
                                member.studentId !== project.creatorId && (

                                    <button
                                        onClick={() =>
                                            removeMember(
                                                member.studentId,
                                                member.userName
                                            )
                                        }
                                    >
                                        Remove
                                    </button>

                                )}

                            <hr />

                        </div>

                    ))

                )}

            </div>


            {/* =========================
                MEMBER-ONLY CONTROLS
            ========================= */}

            {!isCreator && !isClosed && (

              <div className="member-controls">

                    <button onClick={leaveProject}>
                        Leave Project
                    </button>

                    <button
                        onClick={() =>
                            navigate(
                                `/dashboard/project/${projectId}/recommendations`
                            )
                        }
                    >
                        Get Recommendation
                    </button>

                </div>

            )}


            {/* =========================
                EDIT PROJECT MODAL
            ========================= */}

            {showEditProject && (

                <div className="modal-overlay">

                      <div className="modal-content">

                        <h2>Edit Project</h2>

                        <input
                            name="name"
                            placeholder="Project Name"
                            value={editProject.name}
                            onChange={handleEditProjectChange}
                        />

                        <textarea
                            name="description"
                            placeholder="Description"
                            value={editProject.description}
                            onChange={handleEditProjectChange}
                        />

                        <input
                            name="domain"
                            placeholder="Domain"
                            value={editProject.domain}
                            onChange={handleEditProjectChange}
                        />

                        <input
                            type="number"
                            name="desiredTeamSize"
                            placeholder="Desired Team Size"
                            value={editProject.desiredTeamSize}
                            onChange={handleEditProjectChange}
                        />

                        <button onClick={updateProject}>
                            Save
                        </button>

                        <button
                            onClick={() =>
                                setShowEditProject(false)
                            }
                        >
                            Cancel
                        </button>

                    </div>

                </div>

            )}


            {/* =========================
                EDIT SKILL MODAL
            ========================= */}

            {showEditSkill && editingSkill && (

                <div className="modal-overlay">

                      <div className="modal-content">

                        <h2>
                            Edit Skill Importance
                        </h2>

                        <p>
                            Skill:{" "}
                            {editingSkill.skillName}
                        </p>

                        <input
                            type="number"
                            min="1"
                            max="5"
                            value={editingImportance}
                            onChange={(e) =>
                                setEditingImportance(
                                    e.target.value
                                )
                            }
                        />

                        <button onClick={updateSkill}>
                            Save
                        </button>

                        <button
                            onClick={() => {
                                setShowEditSkill(false);
                                setEditingSkill(null);
                                setEditingImportance("");
                            }}
                        >
                            Cancel
                        </button>

                    </div>

                </div>

            )}

        </div>
    );
}

export default ProjectDetails;