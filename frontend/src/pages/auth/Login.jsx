import api from "../../api/axios";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import "./Auth.css";
function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();

  const loginRequest = async (e) => {
    e.preventDefault();
    setError("");
    setMessage("");

    const request = {
      email,
      password,
    };
    try {
      const res = await api.post("/api/students/login", request);
      if (res.data.success) {
        localStorage.setItem("token", res.data.token);
        navigate("/dashboard");
      } else {
        setMessage("");
        setError(res.data.message);
      }
    } catch (err) {
      setMessage("");
      setError(err.response?.data?.message || "Something went wrong");
    }
  };
  const redirectRegister = () => {
    navigate("/register");
  };

 return (
  <div className="auth-page">
    <form className="auth-form" onSubmit={loginRequest}>
      <h1>TeamSync</h1>
      <h2>Login</h2>

      <label htmlFor="email">Email</label>
      <input
        name="email"
        id="email"
        type="email"
        placeholder="Enter Email"
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
    {showPassword ? "~_~" : "^_^"}
  </button>
</div>

     

      <p className="form-message">{message}</p>
      <p className="form-error">{error}</p>


      <button className="primary-button" type="submit">
        Login
      </button>

 <button
  className="forgot-password-button"
  type="button"
  onClick={() => navigate("/forgot-password")}
>
  Forgot Password?
</button>

      <button
        className="secondary-button"
        type="button"
        onClick={redirectRegister}
      >
        Don't have an account? Register
      </button>
    </form>
  </div>
);
}

export default Login;
