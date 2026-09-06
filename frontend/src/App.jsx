import { Routes, Route } from "react-router-dom";

import Login from "./pages/auth/Login";
import Register from "./pages/auth/Register";
import Dashboard from "./pages/dashboard/Dashboard";
import VerifyOtp from "./pages/auth/VerifyOtp";
import Home from "./pages/dashboard/Home";
import MyProjects from "./pages/dashboard/MyProjects";
import Notifications from "./pages/notifications/Notifications";
import ProjectRooms from "./pages/dashboard/ProjectRooms";
import MyProfile from "./pages/profile/MyProfile";
import ProtectedRoute from "./components/ProtectedRoute";
import StudentProfile from "./pages/profile/StudentProfile";
import CreateProject from "./pages/dashboard/CreateProject";
import ProjectDetails from "./pages/projects/ProjectDetails";
import Invitations from "./pages/projects/Invitations";
import Applications from "./pages/projects/Applications";
import ReferredPeople from "./pages/projects/ReferredPeople";
import Recommendations from "./pages/recommendations/Recommendations";
import ChatRoom from "./pages/chat/ChatRoom";
import ForgotPassword from "./pages/auth/ForgotPassword";
function App() {

  return (
    <Routes>
      <Route path="/" element={<Login />} />

      <Route path="/register" element={<Register />} />
      <Route path="/verify-email" element={<VerifyOtp />} />
<Route path="/forgot-password" element={<ForgotPassword />} />
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
            <Dashboard />
          </ProtectedRoute>
        }
      >
        <Route index element={<Home />} />
        <Route path="my-projects" element={<MyProjects />} />
        <Route path="project-rooms" element={<ProjectRooms />} />
        <Route path="notifications" element={<Notifications />} />
        <Route path="my-profile" element={<MyProfile />} />
        <Route path="profile/:studentId" element={<StudentProfile />} />
        <Route path="create-project" element={<CreateProject />} />
        <Route path="project/:projectId" element={<ProjectDetails />} />

        <Route
          path="project/:projectId/invitations"
          element={<Invitations />}
        />

        <Route
          path="project/:projectId/applications"
          element={<Applications />}
        />

        <Route
          path="project/:projectId/recommendations"
          element={<Recommendations />}
        />

        <Route
          path="project/:projectId/referred-people"
          element={<ReferredPeople />}
        />
        <Route path="project-rooms/:roomId" element={<ChatRoom />} />
      </Route>
    </Routes>
  );
}

export default App;
