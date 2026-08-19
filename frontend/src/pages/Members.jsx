import { useState } from "react";
import { Users, UserPlus, Trash2 } from "lucide-react";
import { api } from "../services/Api";

function Members({
    groups,
    groupData,
    currentUser,
    selectedGroupId,
    onSelectGroup,
    onMemberChanged
}) {

    const [removingId, setRemovingId] =
        useState(null);

    const [showAddForm, setShowAddForm] =
        useState(false);

    const [memberName, setMemberName] =
        useState("");

    const [memberEmail, setMemberEmail] =
        useState("");

    const [memberCollege, setMemberCollege] =
        useState("");

    const [addingMember, setAddingMember] =
        useState(false);

    const [error, setError] =
        useState("");
    

    const selectedGroup =
        groups.find(
            (group) =>
                group.id === selectedGroupId
        );

    const members =
        selectedGroup
            ? groupData[selectedGroup.id]?.members || []
            : [];

    /*
     * Check whether current user
     * is the owner of this group.
     */
    const isOwner =
        selectedGroup?.owner?.id ===
        currentUser?.id;

    // =========================
    // ADD MEMBER
    // =========================

    async function handleAddMember(event) {

        event.preventDefault();

        if (!selectedGroup) {
            return;
        }

        if (!memberName.trim()) {

            setError(
                "Member name is required."
            );

            return;
        }

        if (!memberEmail.trim()) {

            setError(
                "Member email is required."
            );

            return;
        }

        try {

            setAddingMember(true);
            setError("");

            await api.addMember(
                selectedGroup.id,
                {
                    name: memberName.trim(),
                    email: memberEmail.trim(),
                    college: memberCollege.trim()
                }
            );

            // Clear form
            setMemberName("");
            setMemberEmail("");
            setMemberCollege("");

            setShowAddForm(false);

            // Refresh group data
            await onMemberChanged();

        } catch (error) {

            console.error(
                "ADD MEMBER ERROR:",
                error
            );

            setError(
                error.message ||
                "Could not add member."
            );

        } finally {

            setAddingMember(false);
        }
    }

    // =========================
    // REMOVE MEMBER
    // =========================

    async function handleRemoveMember(
        studentId
    ) {

        if (!selectedGroup) {
            return;
        }

        const confirmed =
            window.confirm(
                "Are you sure you want to remove this member?"
            );

        if (!confirmed) {
            return;
        }

        try {

            setRemovingId(studentId);
            setError("");

            await api.deleteMember(
                selectedGroup.id,
                studentId,
                 currentUser.id
            );

            await onMemberChanged();

        } catch (error) {

            console.error(
                "REMOVE MEMBER ERROR:",
                error
            );

            setError(
                error.message ||
                "Could not remove member."
            );

        } finally {

            setRemovingId(null);
        }
    }

    // =========================
    // NO GROUPS
    // =========================

    if (groups.length === 0) {

        return (
            <div className="page">

                <div className="dashboard-empty">

                    <div className="dashboard-empty-icon">
                        <Users size={28} />
                    </div>

                    <h2>
                        No groups yet
                    </h2>

                    <p>
                        Create a group first to
                        manage its members.
                    </p>

                </div>

            </div>
        );
    }

    return (
        <div className="page">

            {/* PAGE HEADER */}

            <div className="page-header">

                <div>

                    <p className="eyebrow">
                        PEOPLE
                    </p>

                    <h1>
                        Members
                    </h1>

                    <p className="page-description">
                        Manage members of your groups.
                    </p>

                </div>

            </div>

            

           {/* GROUP SELECTOR */}

<div className="members-group-selector">

    <div className="members-selector-icon">
        <Users size={19} />
    </div>

    <div className="members-selector-content">

        <label>
            GROUP
        </label>

        <select
            value={selectedGroupId || ""}
            onChange={(event) => {

                const value =
                    event.target.value;

                onSelectGroup(
                    value
                        ? Number(value)
                        : null
                );

                setShowAddForm(false);
                setError("");
            }}
        >

            <option value="">
                Select a group
            </option>

            {groups.map((group) => (

                <option
                    key={group.id}
                    value={group.id}
                >
                    {group.name}
                </option>

            ))}

        </select>

    </div>

</div>

            {/* ERROR */}

            {error && (

                <div className="global-error">

                    <span>
                        {error}
                    </span>

                    <button
                        type="button"
                        onClick={() =>
                            setError("")
                        }
                    >
                        ×
                    </button>

                </div>
            )}

            {/* GROUP MEMBERS */}

            {selectedGroup && (

               <section className="dashboard-panel members-panel">

                    {/* HEADER */}

                  <div className="dashboard-panel-header members-panel-header">

    <div className="members-title-area">

        <div className="members-group-avatar">
            {selectedGroup.name
                ?.charAt(0)
                ?.toUpperCase()}
        </div>

        <div>

            <p className="eyebrow">
                GROUP MEMBERS
            </p>

            <h2>
                {selectedGroup.name}
            </h2>

            <span className="members-count">
                {members.length}{" "}
                {members.length === 1
                    ? "member"
                    : "members"}
            </span>

        </div>

    </div>

    {isOwner && (

        <button
            type="button"
            className="primary-button"
            onClick={() => {

                setShowAddForm(
                    !showAddForm
                );

                setError("");

            }}
        >

            <UserPlus size={17} />

            {showAddForm
                ? "Cancel"
                : "Add Member"}

        </button>

    )}

</div>
                    {/* ADD MEMBER FORM */}

                    {showAddForm && isOwner && (

                        <form
                            onSubmit={
                                handleAddMember
                            }
                            className="member-form"
                        >

                            <div className="form-group">

                                <label>
                                    Name
                                </label>

                                <input
                                    type="text"
                                    className="form-input"
                                    placeholder="e.g. Name of the member"
                                    value={
                                        memberName
                                    }
                                    onChange={(event) =>
                                        setMemberName(
                                            event.target.value
                                        )
                                    }
                                />

                            </div>

                            <div className="form-group">

                                <label>
                                    Email
                                </label>

                                <input
                                    type="email"
                                    className="form-input"
                                    placeholder="e.g. name@email.com"
                                    value={
                                        memberEmail
                                    }
                                    onChange={(event) =>
                                        setMemberEmail(
                                            event.target.value
                                        )
                                    }
                                />

                            </div>

                            <div className="form-group">

                                <label>
                                    College
                                </label>

                                <input
                                    type="text"
                                    className="form-input"
                                    placeholder="Optional"
                                    value={
                                        memberCollege
                                    }
                                    onChange={(event) =>
                                        setMemberCollege(
                                            event.target.value
                                        )
                                    }
                                />

                            </div>

                            <button
                                type="submit"
                                className="primary-button"
                                disabled={
                                    addingMember
                                }
                            >

                                {addingMember
                                    ? "Adding..."
                                    : "Add Member"}

                            </button>

                        </form>
                    )}

                   

                {/* MEMBER LIST */}

<div className="dashboard-group-list">

    {members.map((member) => {

        const isCurrentUser =
            member.email?.toLowerCase() ===
            currentUser?.email?.toLowerCase();

        const isGroupOwner =
            selectedGroup.owner?.email?.toLowerCase() ===
            member.email?.toLowerCase();

        return (
            <div
                className="dashboard-group-row members-row"
                key={member.id}
            >

                {/* AVATAR */}

                <div className="dashboard-group-avatar">

                    {member.name
                        ?.charAt(0)
                        ?.toUpperCase()}

                </div>


                {/* MEMBER INFORMATION */}

                <div className="dashboard-group-info">

                    <strong>
                        {member.name}
                    </strong>

                    <span>
                        {member.email}
                    </span>

                    {isCurrentUser && (
                        <small>
                            You
                        </small>
                    )}

                </div>


                {/* OWNER BADGE */}

                {isGroupOwner && (
                    <span className="member-owner-badge">
                        Owner
                    </span>
                )}


                {/* REMOVE BUTTON */}

                {isOwner &&
                    !isCurrentUser &&
                    !isGroupOwner && (

                    <button
                        type="button"
                        className="member-remove-button"
                        disabled={
                            removingId === member.id
                        }
                        onClick={() =>
                            handleRemoveMember(
                                member.id
                            )
                        }
                    >

                        <Trash2 size={15} />

                        <span>
                            {removingId === member.id
                                ? "Removing..."
                                : "Remove"}
                        </span>

                    </button>

                )}

            </div>
        );

    })}


    {/* EMPTY STATE */}

    {members.length === 0 && (

        <div className="dashboard-panel-empty">

            No members found.

        </div>

    )}

</div>

                </section>
            )}

        </div>
    );
}

export default Members;