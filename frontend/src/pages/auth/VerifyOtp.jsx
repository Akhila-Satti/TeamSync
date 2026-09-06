import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import api from "../../api/axios";
import "./Auth.css";
function VerifyOtp() {
  const location = useLocation();
  const navigate = useNavigate();
  const email = location.state?.email;
  const [otp, setOtp] = useState("");
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const verify = async (e) => {
    e.preventDefault();
    setError("");
    setMessage("");
    if (!email) {
      setMessage("");
      setError("No Email found");

      return;
    }
    const validOtp = validateOtp(otp);
    if (!validOtp) {
      setMessage("");
      setError("Otp must be a 6 digit numeric");
      return;
    }
    const request = {
      email,
      otp,
    };
    try {
      const res = await api.post("/api/students/verify-email", request);
      if (res.data.success) {
        setMessage("successfully verified");
        navigate("/");
      } else {
        setMessage("");
        setError(res.data.message);
      }
    } catch (err) {
      setMessage("");
      setError(
        err.response?.data?.message ||
          "Something went wrong.Please try again later",
      );
    }
  };

  const resendOtp = async () => {
    if (!email) {
      setMessage("");
      setError("No Email found");

      return;
    }
    const resendOtpRequest = {
      email,
    };
    try {
      const res = await api.post("/api/students/resend-otp", resendOtpRequest);
      if (res.data.success) {
        setError("");
        setMessage("An otp sent again");
        return;
      }
    } catch (err) {
      setMessage("");
      setError(
        err.response?.data?.message ||
          "Something went wrong.Please try again later",
      );
    }
  };
  const validateOtp = (otp) => {
    return /^\d{6}$/.test(otp);
  };

  return (
  <div className="auth-page">
    <form className="auth-form" onSubmit={verify}>
      <h1>TeamSync</h1>

      <h2>Verify Email</h2>

      <p className="otp-info">
        An OTP has been sent to <strong>{email}</strong>
      </p>

      <label htmlFor="otp">Enter OTP</label>

      <input
        id="otp"
        name="otp"
        type="text"
        inputMode="numeric"
        placeholder="Enter 6-digit OTP"
        maxLength="6"
        onChange={(e) => setOtp(e.target.value)}
      />

      <p className="form-error">{error}</p>
      <p className="form-message">{message}</p>

      <button className="primary-button" type="submit">
        Verify
      </button>

      <button
        className="secondary-button"
        type="button"
        onClick={resendOtp}
      >
        Resend OTP
      </button>
    </form>
  </div>
);
}
export default VerifyOtp;
