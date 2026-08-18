import { useState } from "react";
import { api } from "../services/Api";

import {
    User,
    Mail,
    Lock
} from "lucide-react";

function Signup({
    onSignup,
    onShowLogin
}) {

    const [name, setName] =
        useState("");

    const [email, setEmail] =
        useState("");

    const [password, setPassword] =
        useState("");

    const [confirmPassword, setConfirmPassword] =
        useState("");

    const [error, setError] =
        useState("");

    const handleSubmit = async (e) => {

        e.preventDefault();

        setError("");

        const cleanName =
            name.trim();

        const cleanEmail =
            email.trim().toLowerCase();

        // =========================
        // VALIDATION
        // =========================

        if (!cleanName) {

            setError(
                "Please enter your name."
            );

            return;
        }

        if (!cleanEmail) {

            setError(
                "Please enter your email."
            );

            return;
        }

        if (!password) {

            setError(
                "Please enter a password."
            );

            return;
        }

        if (password.length < 6) {

            setError(
                "Password must contain at least 6 characters."
            );

            return;
        }

        if (password !== confirmPassword) {

            setError(
                "Passwords do not match."
            );

            return;
        }

        // =========================
        // CREATE ACCOUNT
        // =========================

        try {

            const user =
                await api.signup({
                    name: cleanName,
                    email: cleanEmail,
                    password: password
                });

            console.log(
                "ACCOUNT CREATED:",
                user
            );

            /*
             * Tell App.jsx that signup
             * was successful.
             */
            onSignup(user);

        } catch (err) {

            console.error(
                "SIGNUP ERROR:",
                err
            );

            setError(
                err.message ||
                "Could not create your account."
            );
        }
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

                    {/* =========================
                        FULL NAME
                    ========================= */}

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

                    {/* =========================
                        EMAIL
                    ========================= */}

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

                    {/* =========================
                        PASSWORD
                    ========================= */}

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

                    {/* =========================
                        CONFIRM PASSWORD
                    ========================= */}

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

                    {/* =========================
                        ERROR
                    ========================= */}

                    {error && (

                        <div className="auth-error">

                            {error}

                        </div>

                    )}

                    {/* =========================
                        SUBMIT
                    ========================= */}

                    <button
                        type="submit"
                        className="auth-submit"
                    >
                        Create Account
                    </button>

                </form>

                {/* =========================
                    LOGIN LINK
                ========================= */}

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