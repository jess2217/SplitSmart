import { useState } from "react";
import {
    Lock,
    Mail
} from "lucide-react";

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

    const handleSubmit = (e) => {

        e.preventDefault();

        setError("");

        const cleanEmail =
            email.trim().toLowerCase();

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

        /*
         * Get the account created during signup.
         */
        const savedUser =
            localStorage.getItem(
                "splitsmart_user"
            );

        /*
         * No account exists yet.
         */
        if (!savedUser) {

            setError(
                "No account found. Please sign up first."
            );

            return;
        }

        let user;

        try {

            user = JSON.parse(savedUser);

        } catch (error) {

            console.error(
                "USER DATA ERROR:",
                error
            );

            setError(
                "Account data is corrupted. Please sign up again."
            );

            return;
        }

        /*
         * Check email.
         */
        if (
            user.email.toLowerCase() !==
            cleanEmail
        ) {

            setError(
                "Invalid email or password."
            );

            return;
        }

        /*
         * Check password.
         */
        if (
            user.password !==
            password
        ) {

            setError(
                "Invalid email or password."
            );

            return;
        }

        /*
         * Login successful.
         */
        localStorage.setItem(
            "splitsmart_authenticated",
            "true"
        );

        onLogin({
            name: user.name,
            email: user.email
        });
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

                    {error && (
                        <div className="auth-error">
                            {error}
                        </div>
                    )}

                    <button
                        type="submit"
                        className="auth-submit"
                    >
                        Login
                    </button>

                </form>

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