import { useEffect, useRef, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../../api/axios";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import "./ChatRoom.css";

function ChatRoom() {
  const { roomId } = useParams();
  const navigate = useNavigate();

  const stompClient = useRef(null);
  const messagesEndRef = useRef(null);

  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [message, setMessage] = useState("");
  const [sending, setSending] = useState(false);
  const [currentStudentId, setCurrentStudentId] = useState(null);
  const [connected, setConnected] = useState(false);

  // Fetch previous messages
  useEffect(() => {
    const fetchMessages = async () => {
      try {
        const response = await api.get(
          `/api/rooms/${roomId}/messages`
        );

        setMessages(response.data);
      } catch (error) {
        console.error(error);
        setError("Failed to load messages.");
      } finally {
        setLoading(false);
      }
    };

    fetchMessages();
  }, [roomId]);

  // Fetch current student
  useEffect(() => {
    const fetchCurrentStudent = async () => {
      try {
        const response = await api.get("/api/students/me");

        setCurrentStudentId(response.data.studentId);
      } catch (error) {
        console.error(error);
      }
    };

    fetchCurrentStudent();
  }, []);

  // WebSocket connection
  useEffect(() => {
    const token = localStorage.getItem("token");

    const client = new Client({
      webSocketFactory: () =>
        new SockJS("http://localhost:8080/ws"),

      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },

      onConnect: () => {
        console.log("WebSocket connected");

        setConnected(true);

        client.subscribe(
          `/topic/${roomId}/chat`,
          (message) => {
            const newMessage = JSON.parse(message.body);

            setMessages((previousMessages) => [
              ...previousMessages,
              newMessage,
            ]);
          }
        );
      },

      onStompError: (frame) => {
        console.error("WebSocket error:", frame);
        setConnected(false);
      },

      onWebSocketClose: () => {
        console.log("WebSocket disconnected");
        setConnected(false);
      },
    });

    client.activate();

    stompClient.current = client;

    return () => {
      client.deactivate();
      setConnected(false);
    };
  }, [roomId]);

  // Auto-scroll to latest message
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({
      behavior: "smooth",
    });
  }, [messages]);

  // Send message
  const sendMessage = () => {
    if (!message.trim()) {
      return;
    }

    if (!connected) {
      console.log("WebSocket is not connected");
      return;
    }

    setSending(true);

    stompClient.current.publish({
      destination: `/app/chat/${roomId}`,
      body: JSON.stringify({
        content: message.trim(),
      }),
    });

    setMessage("");
    setSending(false);
  };

  // Format message time
  const formatTime = (dateTime) => {
    return new Date(dateTime).toLocaleTimeString([], {
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  return (
  <div className="chat-room-page">

    <div className="chat-room-header">
      <button
        className="back-button"
        onClick={() => navigate(-1)}
      >
        Back
      </button>

      <h2>Chat Room</h2>
    </div>

    <p className={`chat-status ${connected ? "connected" : "disconnected"}`}>
      Status: {connected ? "Connected" : "Disconnected"}
    </p>

     {loading && (
  <p className="chat-room-message">
    Loading messages...
  </p>
)}

{error && (
  <p className="chat-room-error">
    {error}
  </p>
)}

      {!loading &&
        !error &&
        messages.length === 0 && (
         <p className="chat-room-message">
  No messages yet.
</p>
        )}

      {!loading &&
        !error &&
        messages.length > 0 && (
          <div className="messages-container">
            {messages.map((message, index) => {
              const isMyMessage =
                message.studentId === currentStudentId;

              return (
                <div
                  key={index}
                  className={
                    isMyMessage
                      ? "my-message"
                      : "other-message"
                  }
                >
                  {isMyMessage ? (
                    <strong>You</strong>
                  ) : (
                    <strong>{message.userName}</strong>
                  )}

                  <p>{message.content}</p>

                  <small>
                    {formatTime(message.sentAt)}
                  </small>
                </div>
              );
            })}

            <div ref={messagesEndRef}></div>
          </div>
        )}

      <div className="chat-input-container">
  <input
    className="chat-input"
    type="text"
    value={message}
    onChange={(e) => setMessage(e.target.value)}
    onKeyDown={(e) => {
      if (e.key === "Enter") {
        sendMessage();
      }
    }}
    placeholder="Type a message..."
  />

  <button
    className="send-message-button"
    onClick={sendMessage}
    disabled={sending || !connected}
  >
    {sending ? "Sending..." : "Send"}
  </button>
</div>
    </div>
  );
}

export default ChatRoom;