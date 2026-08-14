import { useState } from "react";
import {
    User,
    Mail,
    Lock
} from "lucide-react";

function Signup({ onSignup, onShowLogin }) {

    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] =
        useState("");

    const [error, setError] = useState("");

    const handleSubmit = (e) => {

        e.preventDefault();

        setError("");

        const cleanName = name.trim();
        const cleanEmail = email.trim().toLowerCase();

        if (!cleanName) {
            setError("Please enter your name.");
            return;
        }

        if (!cleanEmail) {
            setError("Please enter your email.");
            return;
        }

        if (!password) {
            setError("Please enter a password.");
            return;
        }

        if (password.length < 6) {
            setError(
                "Password must contain at least 6 characters."
            );
            return;
        }

        if (password !== confirmPassword) {
            setError("Passwords do not match.");
            return;
        }

        /*
         * Check whether an account already exists.
         */
        const existingUser =
            localStorage.getItem(
                "splitsmart_user"
            );

        if (existingUser) {

            const user =
                JSON.parse(existingUser);

            if (
                user.email.toLowerCase() ===
                cleanEmail
            ) {

                setError(
                    "An account with this email already exists. Please log in."
                );

                return;
            }
        }

        /*
         * Temporarily save the account.
         *
         * This is only for the current
         * frontend version.
         */
        const user = {
            name: cleanName,
            email: cleanEmail,
            password: password
        };

        localStorage.setItem(
            "splitsmart_user",
            JSON.stringify(user)
        );

        /*
         * Tell App.jsx that signup succeeded.
         *
         * App.jsx will redirect the user
         * to the Login page.
         */
        onSignup(user);
    };

    return (
        <div className="auth-page">

            <div className="auth-card">

                <div className="auth-logo">
                    S
                </div>

                <h1>
                    Create your account
                </h1>

                <p className="auth-subtitle">
                    Start managing your shared expenses
                </p>

                <form onSubmit={handleSubmit}>

                    <div className="auth-field">

                        <label>
                            Full Name
                        </label>

                        <div className="auth-input-wrapper">

                            <User size={18} />

                            <input
                                type="text"
                                placeholder="Enter your name"
                                value={name}
                                onChange={(e) =>
                                    setName(
                                        e.target.value
                                    )
                                }
                            />

                        </div>

                    </div>

                    <div className="auth-field">

                        <label>
                            Email
                        </label>

                        <div className="auth-input-wrapper">

                            <Mail size={18} />

                            <input
                                type="email"
                                placeholder="Enter your email"
                                value={email}
                                onChange={(e) =>
                                    setEmail(
                                        e.target.value
                                    )
                                }
                            />

                        </div>

                    </div>

                    <div className="auth-field">

                        <label>
                            Password
                        </label>

                        <div className="auth-input-wrapper">

                            <Lock size={18} />

                            <input
                                type="password"
                                placeholder="Create a password"
                                value={password}
                                onChange={(e) =>
                                    setPassword(
                                        e.target.value
                                    )
                                }
                            />

                        </div>

                    </div>

                    <div className="auth-field">

                        <label>
                            Confirm Password
                        </label>

                        <div className="auth-input-wrapper">

                            <Lock size={18} />

                            <input
                                type="password"
                                placeholder="Confirm your password"
                                value={confirmPassword}
                                onChange={(e) =>
                                    setConfirmPassword(
                                        e.target.value
                                    )
                                }
                            />

                        </div>

                    </div>

                    {error && (
                        <div className="auth-error">
                            {error}
                        </div>
                    )}

                    <button
                        type="submit"
                        className="auth-submit"
                    >
                        Create Account
                    </button>

                </form>

                <div className="auth-switch">

                    <span>
                        Already have an account?
                    </span>

                    <button
                        type="button"
                        onClick={onShowLogin}
                    >
                        Log in
                    </button>

                </div>

            </div>

        </div>
    );
}

export default Signup;