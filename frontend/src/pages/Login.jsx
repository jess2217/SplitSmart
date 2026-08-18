import { useState } from "react";
import {
    Lock,
    Mail
} from "lucide-react";

import { api } from "../services/Api";

function Login({
    onLogin,
    onShowSignup
}) {

    const [email, setEmail] =
        useState("");

    const [password, setPassword] =
        useState("");

    const [error, setError] =
        useState("");

    const handleSubmit = async (e) => {

        e.preventDefault();

        setError("");

        const cleanEmail =
            email.trim().toLowerCase();

        // =========================
        // VALIDATION
        // =========================

        if (!cleanEmail) {

            setError(
                "Please enter your email."
            );

            return;
        }

        if (!password.trim()) {

            setError(
                "Please enter your password."
            );

            return;
        }

        // =========================
        // LOGIN THROUGH BACKEND
        // =========================

        try {

            const user =
                await api.login({
                    email: cleanEmail,
                    password: password
                });

            console.log(
                "LOGIN SUCCESS:",
                user
            );

            /*
             * Store the current user.
             *
             * This is temporary for our
             * current authentication setup.
             */
            localStorage.setItem(
                "splitsmart_authenticated",
                "true"
            );

            localStorage.setItem(
                "splitsmart_current_user",
                JSON.stringify(user)
            );

            /*
             * Tell App.jsx that login
             * was successful.
             */
            onLogin({
                id: user.id,
                name: user.name,
                email: user.email
            });

        } catch (err) {

            console.error(
                "LOGIN ERROR:",
                err
            );

            setError(
                err.message ||
                "Invalid email or password."
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
                    Welcome back
                </h1>

                <p className="auth-subtitle">
                    Log in to continue to SplitSmart
                </p>

                <form onSubmit={handleSubmit}>

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
                                placeholder="Enter your password"
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
                        ERROR
                    ========================= */}

                    {error && (

                        <div className="auth-error">
                            {error}
                        </div>

                    )}

                    {/* =========================
                        LOGIN BUTTON
                    ========================= */}

                    <button
                        type="submit"
                        className="auth-submit"
                    >
                        Login
                    </button>

                </form>

                {/* =========================
                    SIGNUP LINK
                ========================= */}

                <div className="auth-switch">

                    <span>
                        Don't have an account?
                    </span>

                    <button
                        type="button"
                        onClick={onShowSignup}
                    >
                        Sign up
                    </button>

                </div>

            </div>

        </div>
    );
}

export default Login;