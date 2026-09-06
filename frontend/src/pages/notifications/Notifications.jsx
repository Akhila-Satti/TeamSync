import { useEffect, useState } from "react";

import api from "../../api/axios";
import "./Notifications.css";

function Notifications() {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchNotifications();
  }, []);

  const fetchNotifications = async () => {
    try {
      setLoading(true);
      setError("");

      const token = localStorage.getItem("token");

      const response = await api.get("/api/notifications", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      setNotifications(response.data);
    } catch (err) {
      console.error(err);
      setError("Failed to load notifications");
    } finally {
      setLoading(false);
    }
  };

  const markAsRead = async (notificationId) => {
    try {
      await api.patch(`/api/notifications/${notificationId}/read`);

      setNotifications((prev) =>
        prev.map((notification) =>
          notification.notificationId === notificationId
            ? { ...notification, read: true }
            : notification,
        ),
      );
    } catch (err) {
      console.error(err);
    }
  };

  if (loading) {
    return (
      <div className="notifications-page">
        <p className="notifications-state">Loading notifications...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="notifications-page">
        <p className="notifications-error">{error}</p>
      </div>
    );
  }

  return (
    <div className="notifications-page">
      <h2 className="notifications-title">Notifications</h2>

      {notifications.length === 0 ? (
        <p className="notifications-empty">No notifications yet.</p>
      ) : (
        <div className="notifications-list">
          {notifications.map((notification) => (
            <div
              key={notification.notificationId}
              className={`notification-card ${
                notification.read ? "read" : "unread"
              }`}
              onClick={() => {
                if (!notification.read) {
                  markAsRead(notification.notificationId);
                }
              }}
            >
              <p className="notification-type">
                <strong>{notification.type}</strong>
              </p>

              <p className="notification-message">{notification.message}</p>

              <p className="notification-date">
                {new Date(notification.createdAt).toLocaleString()}
              </p>

              <p
                className={`notification-status ${
                  notification.read ? "read-status" : "unread-status"
                }`}
              >
                {notification.read ? "Read" : "Unread"}
              </p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default Notifications;
