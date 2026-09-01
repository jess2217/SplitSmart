import {
    ArrowLeft,
    User,
    Mail,
    LockKeyhole,
    LogOut,
    Pencil,
    Check,
    X
} from "lucide-react";

import { useState } from "react";
import { api } from "../services/Api";

function Profile({
    currentUser,
    onBack,
    onLogout,
    onProfileUpdate
}) {

    // =========================
    // PROFILE STATE
    // =========================

    const [isEditing, setIsEditing] =
        useState(false);

    const [name, setName] =
        useState(currentUser?.name || "");

    const [email, setEmail] =
        useState(currentUser?.email || "");

    const [saved, setSaved] =
        useState(false);


    // =========================
    // PASSWORD STATE
    // =========================

    const [changePasswordOpen, setChangePasswordOpen] =
        useState(false);

    const [currentPassword, setCurrentPassword] =
        useState("");

    const [newPassword, setNewPassword] =
        useState("");

    const [confirmPassword, setConfirmPassword] =
        useState("");

    const [passwordMessage, setPasswordMessage] =
        useState("");

    const [passwordMessageType, setPasswordMessageType] =
        useState("");


    // =========================
    // USER INITIAL
    // =========================

    const userInitial =
        name
            .charAt(0)
            .toUpperCase() || "U";


    // =========================
    // EDIT PROFILE
    // =========================

    function handleEdit() {

        setSaved(false);

        setIsEditing(true);
    }


    // =========================
    // CANCEL PROFILE EDIT
    // =========================

    function handleCancel() {

        setName(
            currentUser?.name || ""
        );

        setEmail(
            currentUser?.email || ""
        );

        setIsEditing(false);

        setSaved(false);
    }


    // =========================
    // SAVE PROFILE
    // =========================

    async function handleSave() {

        if (!name.trim()) {

            alert(
                "Name cannot be empty."
            );

            return;
        }

        if (!email.trim()) {

            alert(
                "Email cannot be empty."
            );

            return;
        }

        try {

            const updatedUser = {
                ...currentUser,
                name: name.trim(),
                email: email.trim()
            };

            if (onProfileUpdate) {

                await onProfileUpdate(
                    updatedUser
                );
            }

            setName(
                updatedUser.name
            );

            setEmail(
                updatedUser.email
            );

            setIsEditing(false);

            setSaved(true);

            setTimeout(() => {

                setSaved(false);

            }, 2500);

        } catch (error) {

            console.error(
                "PROFILE UPDATE ERROR:",
                error
            );

            alert(
                error.message ||
                "Could not update profile."
            );
        }
    }


    // =========================
    // CHANGE PASSWORD
    // =========================

    async function handleChangePassword() {

        setPasswordMessage("");
        setPasswordMessageType("");


        // -------------------------
        // REQUIRED FIELDS
        // -------------------------

        if (
            !currentPassword ||
            !newPassword ||
            !confirmPassword
        ) {

            setPasswordMessage(
                "Please fill in all password fields."
            );

            setPasswordMessageType(
                "error"
            );

            return;
        }


        // -------------------------
        // PASSWORD LENGTH
        // -------------------------

        if (newPassword.length < 6) {

            setPasswordMessage(
                "New password must be at least 6 characters."
            );

            setPasswordMessageType(
                "error"
            );

            return;
        }


        // -------------------------
        // CONFIRM PASSWORD
        // -------------------------

        if (
            newPassword !==
            confirmPassword
        ) {

            setPasswordMessage(
                "New passwords do not match."
            );

            setPasswordMessageType(
                "error"
            );

            return;
        }


        // -------------------------
        // SAME PASSWORD
        // -------------------------

        if (
            currentPassword ===
            newPassword
        ) {

            setPasswordMessage(
                "New password must be different from your current password."
            );

            setPasswordMessageType(
                "error"
            );

            return;
        }


        try {

            await api.changePassword(
                currentUser.id,
                currentPassword,
                newPassword
            );


            // -------------------------
            // CLEAR FORM
            // -------------------------

            setCurrentPassword("");

            setNewPassword("");

            setConfirmPassword("");


            // -------------------------
            // CLOSE FORM
            // -------------------------

            setChangePasswordOpen(false);


            // -------------------------
            // SUCCESS
            // -------------------------

            setPasswordMessage(
                "Password changed successfully."
            );

            setPasswordMessageType(
                "success"
            );


            setTimeout(() => {

                setPasswordMessage("");

                setPasswordMessageType("");

            }, 3000);

        } catch (error) {

            console.error(
                "CHANGE PASSWORD ERROR:",
                error
            );

            setPasswordMessage(
                error.message ||
                "Could not change password."
            );

            setPasswordMessageType(
                "error"
            );
        }
    }


    // =========================
    // CANCEL PASSWORD CHANGE
    // =========================

    function handleCancelPassword() {

        setChangePasswordOpen(false);

        setCurrentPassword("");

        setNewPassword("");

        setConfirmPassword("");

        setPasswordMessage("");

        setPasswordMessageType("");
    }


    return (

        <div className="page profile-page">


            {/* =========================
                BACK BUTTON
            ========================= */}

            <button
                type="button"
                className="back-button"
                onClick={onBack}
            >

                <ArrowLeft size={18} />

                Back

            </button>


            {/* =========================
                HEADER
            ========================= */}

            <div className="page-header">

                <div>

                    <p className="eyebrow">
                        ACCOUNT
                    </p>

                    <h1>
                        Profile
                    </h1>

                    <p className="page-description">
                        Manage your SplitSmart account.
                    </p>

                </div>

            </div>


            {/* =========================
                PROFILE CARD
            ========================= */}

            <div className="profile-card">


                {/* =========================
                    PROFILE HEADER
                ========================= */}

                <div className="profile-card-top">

                    <div className="profile-large-avatar">
                        {userInitial}
                    </div>

                    <div className="profile-card-info">

                        <h2>
                            {name || "User"}
                        </h2>

                        <p>
                            {email ||
                                "No email available"}
                        </p>

                    </div>

                </div>


                {/* =========================
                    ACCOUNT INFORMATION
                ========================= */}

                <div className="profile-section">

                    <div className="profile-section-heading">

                        <div className="profile-section-title">

                            <User size={18} />

                            <span>
                                Account Information
                            </span>

                        </div>


                        {!isEditing && (

                            <button
                                type="button"
                                className="secondary-button"
                                onClick={handleEdit}
                            >

                                <Pencil size={15} />

                                Edit Profile

                            </button>

                        )}

                    </div>


                    {/* =========================
                        NAME
                    ========================= */}

                    <div className="profile-field">

                        <label>
                            Name
                        </label>


                        {isEditing ? (

                            <input
                                type="text"
                                value={name}
                                onChange={(e) =>
                                    setName(
                                        e.target.value
                                    )
                                }
                                className="profile-input"
                                placeholder="Enter your name"
                                style={{
                                    width: "100%",
                                    height: "46px",
                                    boxSizing:
                                        "border-box",
                                    padding:
                                        "0 13px",
                                    backgroundColor:
                                        "#151922",
                                    color:
                                        "#f0f1f5",
                                    border:
                                        "1px solid #343b4c",
                                    borderRadius:
                                        "9px",
                                    outline:
                                        "none",
                                    fontFamily:
                                        "inherit",
                                    fontSize:
                                        "13px"
                                }}
                            />

                        ) : (

                            <div className="profile-field-value">

                                {name}

                            </div>

                        )}

                    </div>


                    {/* =========================
                        EMAIL
                    ========================= */}

                    <div className="profile-field">

                        <label>
                            Email
                        </label>


                        {isEditing ? (

                            <div className="profile-input-with-icon">

                                <Mail size={16} />

                                <input
                                    type="email"
                                    value={email}
                                    onChange={(e) =>
                                        setEmail(
                                            e.target.value
                                        )
                                    }
                                    className="profile-input"
                                    placeholder="Enter your email"
                                    style={{
                                        width:
                                            "100%",
                                        height:
                                            "46px",
                                        boxSizing:
                                            "border-box",
                                        padding:
                                            "0 13px 0 40px",
                                        backgroundColor:
                                            "#151922",
                                        color:
                                            "#f0f1f5",
                                        border:
                                            "1px solid #343b4c",
                                        borderRadius:
                                            "9px",
                                        outline:
                                            "none",
                                        fontFamily:
                                            "inherit",
                                        fontSize:
                                            "13px"
                                    }}
                                />

                            </div>

                        ) : (

                            <div className="profile-field-value">

                                <Mail size={16} />

                                <span>
                                    {email}
                                </span>

                            </div>

                        )}

                    </div>


                    {/* =========================
                        EDIT ACTIONS
                    ========================= */}

                    {isEditing && (

                        <div className="profile-edit-actions">

                            <button
                                type="button"
                                className="secondary-button"
                                onClick={
                                    handleCancel
                                }
                            >

                                <X size={16} />

                                Cancel

                            </button>


                            <button
                                type="button"
                                className="primary-button"
                                onClick={
                                    handleSave
                                }
                            >

                                <Check size={16} />

                                Save Changes

                            </button>

                        </div>

                    )}


                    {/* =========================
                        PROFILE SUCCESS
                    ========================= */}

                    {saved && (

                        <div className="profile-success-message">

                            <Check size={16} />

                            Profile updated successfully.

                        </div>

                    )}

                </div>


                {/* =========================
                    SECURITY
                ========================= */}

                <div className="profile-section">

                    <div className="profile-section-title">

                        <LockKeyhole size={18} />

                        <span>
                            Security
                        </span>

                    </div>


                    {/* SECURITY HEADER */}

                    <div className="security-row">

                        <div>

                            <strong>
                                Password
                            </strong>

                            <p>
                                Keep your account secure
                                with a strong password.
                            </p>

                        </div>


                        <button
                            type="button"
                            className="secondary-button"
                            onClick={() => {

                                setPasswordMessage("");

                                setPasswordMessageType("");

                                setChangePasswordOpen(
                                    !changePasswordOpen
                                );

                            }}
                        >

                            Change Password

                        </button>

                    </div>


                    {/* =========================
                        CHANGE PASSWORD FORM
                    ========================= */}

                    {changePasswordOpen && (

                        <div className="password-change-form">


                            {/* CURRENT PASSWORD */}

                            <div className="profile-field">

                                <label>
                                    Current Password
                                </label>

                                <input
                                    type="password"
                                    value={
                                        currentPassword
                                    }
                                    onChange={(e) =>
                                        setCurrentPassword(
                                            e.target.value
                                        )
                                    }
                                    placeholder="Enter current password"
                                    className="profile-input"
                                    style={{
                                        width:
                                            "100%",
                                        height:
                                            "46px",
                                        boxSizing:
                                            "border-box",
                                        padding:
                                            "0 13px",
                                        backgroundColor:
                                            "#151922",
                                        color:
                                            "#f0f1f5",
                                        border:
                                            "1px solid #343b4c",
                                        borderRadius:
                                            "9px",
                                        outline:
                                            "none",
                                        fontFamily:
                                            "inherit",
                                        fontSize:
                                            "13px"
                                    }}
                                />

                            </div>


                            {/* NEW PASSWORD */}

                            <div className="profile-field">

                                <label>
                                    New Password
                                </label>

                                <input
                                    type="password"
                                    value={
                                        newPassword
                                    }
                                    onChange={(e) =>
                                        setNewPassword(
                                            e.target.value
                                        )
                                    }
                                    placeholder="Enter new password"
                                    className="profile-input"
                                    style={{
                                        width:
                                            "100%",
                                        height:
                                            "46px",
                                        boxSizing:
                                            "border-box",
                                        padding:
                                            "0 13px",
                                        backgroundColor:
                                            "#151922",
                                        color:
                                            "#f0f1f5",
                                        border:
                                            "1px solid #343b4c",
                                        borderRadius:
                                            "9px",
                                        outline:
                                            "none",
                                        fontFamily:
                                            "inherit",
                                        fontSize:
                                            "13px"
                                    }}
                                />

                            </div>


                            {/* CONFIRM PASSWORD */}

                            <div className="profile-field">

                                <label>
                                    Confirm New Password
                                </label>

                                <input
                                    type="password"
                                    value={
                                        confirmPassword
                                    }
                                    onChange={(e) =>
                                        setConfirmPassword(
                                            e.target.value
                                        )
                                    }
                                    placeholder="Confirm new password"
                                    className="profile-input"
                                    style={{
                                        width:
                                            "100%",
                                        height:
                                            "46px",
                                        boxSizing:
                                            "border-box",
                                        padding:
                                            "0 13px",
                                        backgroundColor:
                                            "#151922",
                                        color:
                                            "#f0f1f5",
                                        border:
                                            "1px solid #343b4c",
                                        borderRadius:
                                            "9px",
                                        outline:
                                            "none",
                                        fontFamily:
                                            "inherit",
                                        fontSize:
                                            "13px"
                                    }}
                                />

                            </div>


                            {/* PASSWORD MESSAGE */}

                            {passwordMessage && (

                                <div
                                    className={
                                        passwordMessageType ===
                                        "error"
                                            ? "profile-error-message"
                                            : "profile-success-message"
                                    }
                                >

                                    {passwordMessageType ===
                                    "success" ? (
                                        <Check size={16} />
                                    ) : null}

                                    {passwordMessage}

                                </div>

                            )}


                            {/* PASSWORD ACTIONS */}

                            <div className="profile-edit-actions">

                                <button
                                    type="button"
                                    className="secondary-button"
                                    onClick={
                                        handleCancelPassword
                                    }
                                >

                                    <X size={16} />

                                    Cancel

                                </button>


                                <button
                                    type="button"
                                    className="primary-button"
                                    onClick={
                                        handleChangePassword
                                    }
                                >

                                    <Check size={16} />

                                    Change Password

                                </button>

                            </div>

                        </div>

                    )}

                </div>


                {/* =========================
                    LOGOUT
                ========================= */}

                <div className="profile-section danger-section">

                    <div>

                        <strong>
                            Sign out
                        </strong>

                        <p>
                            Sign out of your
                            SplitSmart account.
                        </p>

                    </div>


                    <button
                        type="button"
                        className="danger-button"
                        onClick={onLogout}
                    >

                        <LogOut size={17} />

                        Logout

                    </button>

                </div>

            </div>

        </div>
    );
}

export default Profile;