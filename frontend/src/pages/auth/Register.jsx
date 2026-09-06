import api from "../../api/axios";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import "./Auth.css";
function Register() {
  const [userName, setUserName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();
  const registerRequest = async (e) => {
    e.preventDefault();
    setError("");
    setMessage("");
    const validUserName = validateUserName(userName);
    if (!validUserName) {
      setError("");
      setMessage(
        "Username must be at least 4 characters and contain at least one alphabet",
      );
      return;
    }
    const validEmail = validateEmail(email);
    if (!validEmail) {
      setError("");
      setMessage("Invalid vit Email");
      return;
    }
    const validPassword = validatePassword(password);
    if (!validPassword) {
      setError("");
      setMessage(
        "Password must be at least 8 characters and contain uppercase, lowercase, number, and special character",
      );
      return;
    }
    const request = {
      userName,
      email,
      password,
    };
    try {
      const res = await api.post("/api/students/register", request);
      if (res.data.success) {
        navigate("/verify-email", {
          state: { email },
        });
      } else {
        setMessage("");
        setError(res.data.message);
      }
    } catch (err) {
      setMessage("");
      setError(err.response?.data?.message || "Something went wrong");
    }
  };
  const validateUserName = (userName) => {
    if (userName == null) {
      return false;
    }

    const regex = /^(?=.*[A-Za-z]).{4,}$/;

    if (!regex.test(userName)) {
      return false;
    }
    return true;
  };

  const validateEmail = (email) => {
    if (email == null) {
      return false;
    }

    const regex = /^[a-z]+\.\d{2}[a-z]{3}\d{4,5}@vitapstudent\.ac\.in$/;
    if (!regex.test(email)) {
      return false;
    }
    return true;
  };

  const validatePassword = (password) => {
    if (!password) {
      return false;
    }

    const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/;

    if (!regex.test(password)) {
      return false;
    }
    return true;
  };
  const redirectLogin = () => {
    navigate("/");
  };

  return (
  <div className="auth-page">
    <form className="auth-form" onSubmit={registerRequest}>
      <h1>TeamSync</h1>
      <h2>Create Account</h2>

      <label htmlFor="userName">Username</label>
      <input
        name="userName"
        id="userName"
        type="text"
        placeholder="Enter Username"
        onChange={(e) => setUserName(e.target.value)}
      />

      <label htmlFor="email">Email</label>
      <input
        name="email"
        id="email"
        type="email"
        placeholder="Enter VIT email"
        onChange={(e) => setEmail(e.target.value)}
      />

      <label htmlFor="password">Password</label>
      <div className="password-input-container">
  <input 
    name="password" 
    id="password" 
    type={showPassword ? "text" : "password"} 
    placeholder="Enter Password" 
    onChange={(e) => setPassword(e.target.value)} 
  />

  <button
    type="button"
    className="password-toggle"
    onClick={() => setShowPassword(!showPassword)}
  >
    {showPassword ?"~_~" : "^_^"}
  </button>
</div>

      <p className="form-message">{message}</p>
      <p className="form-error">{error}</p>

      <button className="primary-button" type="submit">
        Register
      </button>

      <button
        className="secondary-button"
        type="button"
        onClick={redirectLogin}
      >
        Already have an account? Login
      </button>
    </form>
  </div>
);
}
export default Register;
