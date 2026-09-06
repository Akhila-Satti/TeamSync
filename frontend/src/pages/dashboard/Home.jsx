import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../api/axios";
import "./Home.css";

function Home() {

  const [userName, setUserName] = useState("");
  const [students, setStudents] = useState([]);

  const [searchError, setSearchError] = useState("");
  const [projectError, setProjectError] = useState("");

  const [availableProjects, setAvailableProjects] = useState([]);
  const [loadingProjects, setLoadingProjects] = useState(true);

  const navigate = useNavigate();


  // ================= AVAILABLE PROJECTS =================

  useEffect(() => {

    const fetchAvailableProjects = async () => {

      try {

        setProjectError("");
        setLoadingProjects(true);

        const res = await api.get("/api/projects");

        if (res.data.length === 0) {

          setProjectError(
            "No projects available to display"
          );

          return;
        }

        setAvailableProjects(res.data);

      } catch (err) {

        setProjectError(
          err.response?.data?.message ||
          "Failed to load projects"
        );

      } finally {

        setLoadingProjects(false);

      }
    };

    fetchAvailableProjects();

  }, []);


  // ================= SEARCH STUDENT =================

  const searchName = async (e) => {

    e.preventDefault();

    if (!userName.trim()) {

      setSearchError("Please enter a username");
      setStudents([]);

      return;
    }

    try {

      setSearchError("");

      const res = await api.get(
        `/api/students/search?userName=${userName}`
      );

      setStudents(res.data);

    } catch (err) {

      setStudents([]);

      setSearchError(
        err.response?.data?.message ||
        "Something went wrong"
      );
    }
  };


  // ================= APPLY =================

  const apply = async (projectId) => {

    try {

      const res = await api.post(
        `/api/applications/${projectId}/apply`
      );

      alert(res.data.message);

    } catch (err) {

      alert(
        err.response?.data?.message ||
        "Failed to apply for the project"
      );
    }
  };


  return (
    <>

      {/* ================= SEARCH ================= */}

      <div id="search-space">

        <form onSubmit={searchName}>

          <input
            placeholder="Enter username"
            value={userName}
            onChange={(e) =>
              setUserName(e.target.value)
            }
          />

          <button type="submit">
            Search
          </button>

        </form>


        {/* SEARCH ERROR */}

        {searchError && (
          <p>{searchError}</p>
        )}


        <div id="search-results">

          {students.map((student) => (

            <div
              key={student.studentId}
              onClick={() =>
                navigate(
                  `/dashboard/profile/${student.studentId}`
                )
              }
            >

              <h3>{student.userName}</h3>

              <p>{student.bio}</p>

            </div>

          ))}

        </div>

      </div>


      {/* ================= AVAILABLE PROJECTS ================= */}

      <section>

        <h2>Available Projects</h2>

      </section>


      <div id="active-projects">

        {/* PROJECT LOADING */}

        {loadingProjects && (
          <p>Loading...</p>
        )}


        {/* PROJECT ERROR */}

        {!loadingProjects && projectError && (
          <p>{projectError}</p>
        )}


        {/* PROJECTS */}

        {!loadingProjects &&
          availableProjects.map((p) => (

            <div key={p.projectId}>

              <p>{p.name}</p>

              <p>{p.description}</p>

              <p>{p.domain}</p>

              <p>{p.desiredTeamSize}</p>

              <p>{p.status}</p>

              <p>{p.currentMemberCount}</p>

              <p>{p.createdAt}</p>

              <p>{p.creatorName}</p>


              {/* PROJECT SKILLS */}

              {p.skills.map((s) => (

                <div key={s.skillId}>

                  <p>{s.skillName}</p>

                  <p>{s.importance}</p>

                </div>

              ))}


              {/* APPLY */}

              <button
                type="button"
                onClick={() =>
                  apply(p.projectId)
                }
              >
                Apply
              </button>

            </div>

          ))}

      </div>

    </>
  );
}

export default Home;