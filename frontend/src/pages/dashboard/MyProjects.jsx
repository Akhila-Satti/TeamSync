import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../api/axios";
import "./MyProjects.css";

function MyProjects() {

    const [errorMyProjects, setErrorMYProjects] = useState("");
    const [errorInvolvedProjects, setErrorInvolvedProjects] = useState("");

    const [myProjects, setMyProjects] = useState([]);
    const [involvedProjects, setInvolvedProjects] = useState([]);

    const [loadingMyProjects, setLoadingMyProjects] = useState(true);
    const [loadingInvolvedProjects, setLoadingInvolvedProjects] = useState(true);

    const navigate = useNavigate();


    useEffect(() => {

        const fetchMyProjects = async () => {

            try {

                setErrorMYProjects("");
                setLoadingMyProjects(true);

                const res = await api.get(
                    "/api/projects/my-projects"
                );

                if (res.data.length === 0) {

                    setErrorMYProjects(
                        "No projects available to display"
                    );

                    return;
                }

                setMyProjects(res.data);

            } catch (err) {

                setErrorMYProjects(
                    err.response?.data?.message ||
                    "Failed to load projects"
                );

            } finally {

                setLoadingMyProjects(false);

            }
        };


        const fetchInvolvedProjects = async () => {

            try {

                setErrorInvolvedProjects("");
                setLoadingInvolvedProjects(true);

                const res = await api.get(
                    "/api/projects/involved-projects"
                );

                if (res.data.length === 0) {

                    setErrorInvolvedProjects(
                        "No projects available to display"
                    );

                    return;
                }

                setInvolvedProjects(res.data);

            } catch (err) {

                setErrorInvolvedProjects(
                    err.response?.data?.message ||
                    "Failed to load projects"
                );

            } finally {

                setLoadingInvolvedProjects(false);

            }
        };


        fetchMyProjects();
        fetchInvolvedProjects();

    }, []);


    // ================= CREATE PROJECT =================

    const createProject = () => {

        navigate("/dashboard/create-project");

    };


    // ================= PROJECT DETAILS =================

    const openProject = (projectId) => {

        navigate(`/dashboard/project/${projectId}`);

    };


    // ================= INVITATIONS =================

    const openInvitations = (event, projectId) => {

        event.stopPropagation();

        navigate(
            `/dashboard/project/${projectId}/invitations`
        );

    };


    // ================= APPLICATIONS =================

    const openApplications = (event, projectId) => {

        event.stopPropagation();

        navigate(
            `/dashboard/project/${projectId}/applications`
        );

    };


    // ================= RECOMMENDATIONS =================

    const openRecommendations = (event, projectId) => {

        event.stopPropagation();

        navigate(
            `/dashboard/project/${projectId}/recommendations`
        );

    };


    // ================= REFERRED PEOPLE =================

    const openReferredPeople = (event, projectId) => {

        event.stopPropagation();

        navigate(
            `/dashboard/project/${projectId}/referred-people`
        );

    };


    // ================= LEAVE PROJECT =================

    const leaveProject = async (event, projectId) => {

        event.stopPropagation();

        try {

            const res = await api.patch(
                `/api/projects/${projectId}/leave`
            );

            alert(res.data.message);

            setInvolvedProjects((prevProjects) =>
                prevProjects.filter(
                    (project) =>
                        project.projectId !== projectId
                )
            );

        } catch (err) {

            alert(
                err.response?.data?.message ||
                "Failed to leave project"
            );
        }
    };


    // ================= GET RECOMMENDATION =================

    const getRecommendation = (event, projectId) => {

        event.stopPropagation();

        navigate(
            `/dashboard/project/${projectId}/recommendations`
        );

    };


    return (
        <>

            {/* ================= MY PROJECTS ================= */}

            <div className="projects">

                <section>

                    <button
                        type="button"
                        onClick={createProject}
                    >
                        Create Project
                    </button>

                </section>


                <section>

                    <h2>My Projects</h2>

                </section>


                <div id="my-projects">

                    {/* LOADING */}

                    {loadingMyProjects && (
                        <p>Loading...</p>
                    )}


                    {/* ERROR / EMPTY */}

                    {!loadingMyProjects && errorMyProjects && (
                        <p>{errorMyProjects}</p>
                    )}


                    {/* PROJECTS */}

                    {!loadingMyProjects &&
                        myProjects.map((p) => (

                            <div
                                key={p.projectId}
                                className="project-card"
                            >

                                <p>
                                    <strong>Project:</strong>{" "}
                                    {p.name}
                                </p>

                                <p>
                                    <strong>Created:</strong>{" "}
                                    {p.createdAt}
                                </p>


                                {/* PROJECT DETAILS */}

                                <button
                                    type="button"
                                    onClick={() =>
                                        openProject(
                                            p.projectId
                                        )
                                    }
                                >
                                    Project Details
                                </button>


                                {/* INVITATIONS */}

                                <button
                                    type="button"
                                    onClick={(event) =>
                                        openInvitations(
                                            event,
                                            p.projectId
                                        )
                                    }
                                >
                                    Invitations
                                </button>


                                {/* APPLICATIONS */}

                                <button
                                    type="button"
                                    onClick={(event) =>
                                        openApplications(
                                            event,
                                            p.projectId
                                        )
                                    }
                                >
                                    Applications
                                </button>


                                {/* RECOMMENDATIONS */}

                                <button
                                    type="button"
                                    onClick={(event) =>
                                        openRecommendations(
                                            event,
                                            p.projectId
                                        )
                                    }
                                >
                                    Recommendations
                                </button>


                                {/* REFERRED PEOPLE */}

                                <button
                                    type="button"
                                    onClick={(event) =>
                                        openReferredPeople(
                                            event,
                                            p.projectId
                                        )
                                    }
                                >
                                    Referred People
                                </button>

                            </div>

                        ))}

                </div>

            </div>


            {/* ================= INVOLVED PROJECTS ================= */}

            <div className="projects">

                <section>

                    <h2>Involved Projects</h2>

                </section>


                <div id="involved-projects">

                    {/* LOADING */}

                    {loadingInvolvedProjects && (
                        <p>Loading...</p>
                    )}


                    {/* ERROR / EMPTY */}

                    {!loadingInvolvedProjects &&
                        errorInvolvedProjects && (
                            <p>{errorInvolvedProjects}</p>
                        )
                    }


                    {/* PROJECTS */}

                    {!loadingInvolvedProjects &&
                        involvedProjects.map((p) => (

                            <div
                                key={p.projectId}
                                className="project-card"
                            >

                                <p>
                                    <strong>Project:</strong>{" "}
                                    {p.name}
                                </p>

                                <p>
                                    <strong>Created By:</strong>{" "}
                                    {p.creatorName}
                                </p>

                                <p>
                                    <strong>Created:</strong>{" "}
                                    {p.createdAt}
                                </p>


                                {/* PROJECT DETAILS */}

                                <button
                                    type="button"
                                    onClick={() =>
                                        openProject(
                                            p.projectId
                                        )
                                    }
                                >
                                    Project Details
                                </button>


                                {/* LEAVE PROJECT */}

                                <button
                                    type="button"
                                    onClick={(event) =>
                                        leaveProject(
                                            event,
                                            p.projectId
                                        )
                                    }
                                >
                                    Leave Project
                                </button>


                                {/* GET RECOMMENDATION */}

                                <button
                                    type="button"
                                    onClick={(event) =>
                                        getRecommendation(
                                            event,
                                            p.projectId
                                        )
                                    }
                                >
                                    Get Recommendation
                                </button>

                            </div>

                        ))}

                </div>

            </div>

        </>
    );
}

export default MyProjects;