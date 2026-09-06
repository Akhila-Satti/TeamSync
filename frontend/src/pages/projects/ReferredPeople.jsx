import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../../api/axios";
import "./ReferredPeople.css";
function ReferredPeople() {
  const { projectId } = useParams();
  const navigate = useNavigate();

  const [referrals, setReferrals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchReferrals();
  }, [projectId]);

  const fetchReferrals = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await api.get(`/api/referrals/${projectId}`);

      setReferrals(response.data);
    } catch (err) {
      console.error(err);
      setError("Failed to load referred people");
    } finally {
      setLoading(false);
    }
  };

  const updateReferralStatus = async (referralId, status) => {
    try {
      await api.patch(`/api/referrals/${referralId}`, {
        referralStatus: status,
      });

      setReferrals((previousReferrals) =>
        previousReferrals.map((referral) =>
          referral.referralId === referralId
            ? { ...referral, status: status }
            : referral
        )
      );
    } catch (err) {
      console.error(err);
      alert("Failed to update referral status");
    }
  };

  const openProfile = (studentId) => {
    navigate(`/dashboard/profile/${studentId}`);
  };

  const formatDate = (dateTime) => {
    return new Date(dateTime).toLocaleString();
  };

  if (loading) {
  return (
    <div className="referred-people-page">
      <p className="page-message">
        Loading referred people...
      </p>
    </div>
  );
}

  if (error) {
  return (
    <div className="referred-people-page">
      <p className="page-error">{error}</p>
    </div>
  );
}

  return (
    <div className="referred-people-page">
      <button
  className="back-button"
  onClick={() => navigate(-1)}
>
  ← Back
</button>

      <h2>Referred People</h2>

      {referrals.length === 0 ? (
        <p>No referrals for this project.</p>
      ) : (
        referrals.map((referral) => (
         <div
  className="referral-card"
  key={referral.referralId}
>
            <h3>
              <button
  className="referred-person-button"
  onClick={() =>
    openProfile(referral.referredStudentId)
  }
>
  {referral.referredStudentName}
</button>
            </h3>

            <p>
              Referred by:{" "}
              {referral.referredByName}
            </p>

            <p>Status: {referral.status}</p>

            <p>
              Referred on:{" "}
              {formatDate(referral.createdAt)}
            </p>

            {referral.status === "PENDING" && (
            <div className="referral-actions">
                <button
                  onClick={() =>
                    updateReferralStatus(
                      referral.referralId,
                      "ACCEPTED"
                    )
                  }
                >
                  Accept
                </button>

                <button
                  onClick={() =>
                    updateReferralStatus(
                      referral.referralId,
                      "REJECTED"
                    )
                  }
                >
                  Reject
                </button>
              </div>
            )}
          </div>
        ))
      )}
    </div>
  );
}

export default ReferredPeople;