import api from "../../api/axios";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import "./Auth.css";

function ForgotPassword() {
  const navigate = useNavigate();

  const [step, setStep] = useState(1);

  const [email, setEmail] = useState("");
  const [otp, setOtp] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [showNewPassword, setShowNewPassword] = useState(false);

  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const clearMessages = () => {
    setMessage("");
    setError("");
  };

  // STEP 1: Send OTP
  const sendOtp = async (e) => {
    e.preventDefault();
    clearMessages();

    try {
      const res = await api.post("/api/students/forgot-password", {
        email
      });

      if (res.data.success) {
        setMessage(res.data.message);
        setStep(2);
      } else {
        setError(res.data.message);
      }
    } catch (err) {
      setError(
        err.response?.data?.message || "Something went wrong"
      );
    }
  };

  // STEP 2: Verify OTP
  const verifyOtp = async (e) => {
    e.preventDefault();
    clearMessages();

    try {
      const res = await api.post("/api/students/verify-reset-otp", {
        email,
        otp
      });

      if (res.data.success) {
        setMessage(res.data.message);
        setStep(3);
      } else {
        setError(res.data.message);
      }
    } catch (err) {
      setError(
        err.response?.data?.message || "Something went wrong"
      );
    }
  };

  // STEP 3: Reset password
  const resetPassword = async (e) => {
    e.preventDefault();
    clearMessages();

    try {
      const res = await api.post("/api/students/reset-password", {
        email,
        newPassword
      });

      if (res.data.success) {
        setMessage(res.data.message);

        setTimeout(() => {
          navigate("/");
        }, 1500);
      } else {
        setError(res.data.message);
      }
    } catch (err) {
      setError(
        err.response?.data?.message || "Something went wrong"
      );
    }
  };

  return (
    <div className="auth-page">

      {/* STEP 1 */}
      {step === 1 && (
        <form className="auth-form" onSubmit={sendOtp}>
          <h1>TeamSync</h1>
          <h2>Forgot Password</h2>

          <label htmlFor="email">Email</label>

          <input
            id="email"
            name="email"
            type="email"
            placeholder="Enter your email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />

          <p className="form-message">{message}</p>
          <p className="form-error">{error}</p>

          <button
            className="primary-button"
            type="submit"
          >
            Send OTP
          </button>

          <button
            className="secondary-button"
            type="button"
            onClick={() => navigate("/")}
          >
            Back to Login
          </button>
        </form>
      )}

      {/* STEP 2 */}
      {step === 2 && (
        <form className="auth-form" onSubmit={verifyOtp}>
          <h1>TeamSync</h1>
          <h2>Verify OTP</h2>

          <p>
            Enter the OTP sent to your email.
          </p>

          <label htmlFor="otp">OTP</label>

          <input
            id="otp"
            name="otp"
            type="text"
            placeholder="Enter 6-digit OTP"
            value={otp}
            onChange={(e) => setOtp(e.target.value)}
            maxLength="6"
            required
          />

          <p className="form-message">{message}</p>
          <p className="form-error">{error}</p>

          <button
            className="primary-button"
            type="submit"
          >
            Verify OTP
          </button>

          <button
            className="secondary-button"
            type="button"
            onClick={() => {
              setStep(1);
              clearMessages();
            }}
          >
            Back
          </button>
        </form>
      )}

      {/* STEP 3 */}
      {step === 3 && (
        <form className="auth-form" onSubmit={resetPassword}>
          <h1>TeamSync</h1>
          <h2>Reset Password</h2>

          <label htmlFor="newPassword">
            New Password
          </label>

          <div className="password-input-container">
  <input 
    id="newPassword" 
    name="newPassword" 
    type={showNewPassword ? "text" : "password"} 
    placeholder="Enter new password" 
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

          <p className="form-message">{message}</p>
          <p className="form-error">{error}</p>

          <button
            className="primary-button"
            type="submit"
          >
            Reset Password
          </button>
        </form>
      )}

    </div>
  );
}

export default ForgotPassword;