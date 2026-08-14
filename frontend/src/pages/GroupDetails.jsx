import { useState } from "react";

import {
    ArrowLeft,
    Plus,
    Users,
    Receipt,
    X,
    Trash2
} from "lucide-react";

import ExpenseItem from "../components/ExpenseItem";
import { api } from "../services/api";

function GroupDetails({
    group,
    data,
    onBack,
    onAddExpense,
    onBalances,
    onMemberDeleted,
    onDeleteGroup
}) {

    const [showMemberForm, setShowMemberForm] =
        useState(false);

    const [name, setName] =
        useState("");

    const [email, setEmail] =
        useState("");

    const [college, setCollege] =
        useState("");

    const [savingMember, setSavingMember] =
        useState(false);

    const [memberError, setMemberError] =
        useState("");

    // =========================
    // DELETE MEMBER STATE
    // =========================

    const [memberToDelete, setMemberToDelete] =
        useState(null);

    const [deletingMember, setDeletingMember] =
        useState(false);

    const [deleteError, setDeleteError] =
        useState("");

    // =========================
    // DELETE GROUP STATE
    // =========================

    const [showDeleteGroup, setShowDeleteGroup] =
        useState(false);

    const [deletingGroup, setDeletingGroup] =
        useState(false);

    const [groupDeleteError, setGroupDeleteError] =
        useState("");

    if (!group) {
        return null;
    }

    const members = data?.members || [];
    const expenses = data?.expenses || [];

    const total =
        expenses.reduce(
            (sum, expense) =>
                sum + Number(expense.amount || 0),
            0
        );

    // =========================
    // ADD MEMBER
    // =========================

    async function handleAddMember(event) {

        event.preventDefault();

        setMemberError("");

        if (!name.trim()) {

            setMemberError(
                "Student name is required."
            );

            return;
        }

        try {

            setSavingMember(true);

            await api.addMember(
                group.id,
                {
                    name: name.trim(),
                    email: email.trim(),
                    college: college.trim()
                }
            );

            setName("");
            setEmail("");
            setCollege("");

            setShowMemberForm(false);

            if (typeof onMemberDeleted === "function") {
                await onMemberDeleted();
            }

        } catch (error) {

            console.error(
                "Failed to add member:",
                error
            );

            setMemberError(
                error.message ||
                "Unable to add member."
            );

        } finally {

            setSavingMember(false);
        }
    }

    // =========================
    // CANCEL MEMBER FORM
    // =========================

    function cancelMemberForm() {

        setShowMemberForm(false);

        setName("");
        setEmail("");
        setCollege("");

        setMemberError("");
    }

    // =========================
    // OPEN DELETE MEMBER MODAL
    // =========================

    function openDeleteMember(member) {

        setDeleteError("");

        setMemberToDelete(member);
    }

    // =========================
    // CLOSE DELETE MEMBER MODAL
    // =========================

    function closeDeleteMember() {

        if (deletingMember) {
            return;
        }

        setMemberToDelete(null);
        setDeleteError("");
    }

    // =========================
    // DELETE MEMBER
    // =========================

    async function confirmDeleteMember() {

        if (!memberToDelete) {
            return;
        }

        try {

            setDeletingMember(true);
            setDeleteError("");

            console.log(
                "Deleting member:",
                group.id,
                memberToDelete.id
            );

            await api.deleteMember(
                group.id,
                memberToDelete.id
            );

            console.log(
                "Member deleted successfully."
            );

            setMemberToDelete(null);

            if (typeof onMemberDeleted === "function") {
                await onMemberDeleted();
            }

        } catch (error) {

            console.error(
                "DELETE MEMBER ERROR:",
                error
            );

            setDeleteError(
                error.message ||
                "Unable to delete member."
            );

        } finally {

            setDeletingMember(false);
        }
    }

    // =========================
    // OPEN DELETE GROUP MODAL
    // =========================

    function openDeleteGroup() {

        setGroupDeleteError("");

        setShowDeleteGroup(true);
    }

    // =========================
    // CLOSE DELETE GROUP MODAL
    // =========================

    function closeDeleteGroup() {

        if (deletingGroup) {
            return;
        }

        setShowDeleteGroup(false);
        setGroupDeleteError("");
    }

    // =========================
    // DELETE GROUP
    // =========================

    async function confirmDeleteGroup() {

        if (!group) {
            return;
        }

        if (typeof onDeleteGroup !== "function") {

            setGroupDeleteError(
                "Delete group function is not connected."
            );

            return;
        }

        try {

            setDeletingGroup(true);
            setGroupDeleteError("");

            console.log(
                "Deleting group:",
                group.id
            );

            await onDeleteGroup(group.id);

            console.log(
                "Group deleted successfully."
            );

            setShowDeleteGroup(false);

        } catch (error) {

            console.error(
                "DELETE GROUP ERROR:",
                error
            );

            setGroupDeleteError(
                error.message ||
                "Unable to delete group."
            );

        } finally {

            setDeletingGroup(false);
        }
    }

    return (
        <div className="page">

            {/* =========================
                BACK BUTTON
            ========================= */}

            <button
                className="back-button"
                onClick={onBack}
            >
                <ArrowLeft size={17} />
                Back to groups
            </button>


            {/* =========================
                GROUP HEADER
            ========================= */}

            <div className="group-hero">

                <div>

                    <div className="big-group-avatar">
                        {group.name
                            ?.charAt(0)
                            ?.toUpperCase()}
                    </div>

                    <p className="eyebrow">
                        GROUP
                    </p>

                    <h1>
                        {group.name}
                    </h1>

                    <p className="page-description">
                        Group #{group.id}
                    </p>

                </div>


                <div className="group-actions">

                    <button
                        type="button"
                        className="primary-button"
                        onClick={onAddExpense}
                    >
                        <Plus size={18} />
                        Add Expense
                    </button>


                    <button
                        type="button"
                        className="danger-button"
                        onClick={openDeleteGroup}
                    >
                        <Trash2 size={17} />
                        Delete Group
                    </button>

                </div>

            </div>


            {/* =========================
                STATS
            ========================= */}

            <div className="stats-grid three">

                <div className="mini-stat">

                    <Users size={19} />

                    <strong>
                        {members.length}
                    </strong>

                    <span>
                        Members
                    </span>

                </div>


                <div className="mini-stat">

                    <Receipt size={19} />

                    <strong>
                        {expenses.length}
                    </strong>

                    <span>
                        Expenses
                    </span>

                </div>


                <div className="mini-stat">

                    <strong>
                        ₹{total.toFixed(2)}
                    </strong>

                    <span>
                        Total spent
                    </span>

                </div>

            </div>


            {/* =========================
                DASHBOARD
            ========================= */}

            <div className="dashboard-grid">


                {/* =========================
                    MEMBERS
                ========================= */}

                <section className="content-card">

                    <div className="section-heading">

                        <div>

                            <p className="eyebrow">
                                PEOPLE
                            </p>

                            <h2>
                                Members
                            </h2>

                        </div>


                        <button
                            type="button"
                            className="text-button"
                            onClick={() => {
                                setMemberError("");
                                setShowMemberForm(true);
                            }}
                        >
                            <Plus size={15} />
                            Add Member
                        </button>

                    </div>


                    {/* =========================
                        ADD MEMBER FORM
                    ========================= */}

                    {showMemberForm && (

                        <form
                            className="member-form"
                            onSubmit={handleAddMember}
                        >

                            <div className="form-header">

                                <div>

                                    <h3>
                                        Add a member
                                    </h3>

                                    <p>
                                        Add a student to this group.
                                    </p>

                                </div>


                                <button
                                    type="button"
                                    className="icon-button"
                                    onClick={cancelMemberForm}
                                >
                                    <X size={17} />
                                </button>

                            </div>


                            {memberError && (

                                <div className="form-error">
                                    {memberError}
                                </div>

                            )}


                            <div className="form-grid">

                                <div className="form-field">

                                    <label>
                                        Name
                                    </label>

                                    <input
                                        type="text"
                                        value={name}
                                        placeholder="Enter name"
                                        onChange={(event) =>
                                            setName(
                                                event.target.value
                                            )
                                        }
                                    />

                                </div>


                                <div className="form-field">

                                    <label>
                                        Email
                                    </label>

                                    <input
                                        type="email"
                                        value={email}
                                        placeholder="Enter email"
                                        onChange={(event) =>
                                            setEmail(
                                                event.target.value
                                            )
                                        }
                                    />

                                </div>


                                <div className="form-field">

                                    <label>
                                        College
                                    </label>

                                    <input
                                        type="text"
                                        value={college}
                                        placeholder="Enter college"
                                        onChange={(event) =>
                                            setCollege(
                                                event.target.value
                                            )
                                        }
                                    />

                                </div>

                            </div>


                            <div className="form-actions">

                                <button
                                    type="button"
                                    className="secondary-button"
                                    onClick={cancelMemberForm}
                                >
                                    Cancel
                                </button>


                                <button
                                    type="submit"
                                    className="primary-button"
                                    disabled={savingMember}
                                >
                                    {savingMember
                                        ? "Adding..."
                                        : "Add Member"}
                                </button>

                            </div>

                        </form>

                    )}


                    {/* =========================
                        MEMBER LIST
                    ========================= */}

                    <div className="member-list">

                        {members.map((member) => (

                            <div
                                className="member-item"
                                key={member.id}
                            >

                                <div className="member-avatar">

                                    {member.name
                                        ?.charAt(0)
                                        ?.toUpperCase()}

                                </div>


                                <div className="member-details">

                                    <strong>
                                        {member.name}
                                    </strong>

                                    <span>
                                        {member.email}
                                    </span>

                                </div>


                                <button
                                    type="button"
                                    className="delete-member-button"
                                    onClick={() =>
                                        openDeleteMember(member)
                                    }
                                    title="Delete member"
                                >
                                    <Trash2 size={16} />
                                </button>

                            </div>

                        ))}


                        {members.length === 0 && (

                            <div className="empty-mini">
                                No members yet.
                            </div>

                        )}

                    </div>

                </section>


                {/* =========================
                    EXPENSES
                ========================= */}

                <section className="content-card">

                    <div className="section-heading">

                        <div>

                            <p className="eyebrow">
                                EXPENSES
                            </p>

                            <h2>
                                Recent expenses
                            </h2>

                        </div>


                        <button
                            type="button"
                            className="text-button"
                            onClick={onAddExpense}
                        >
                            + Add
                        </button>

                    </div>


                    <div className="expense-list">

                        {expenses.map((expense) => (

                            <ExpenseItem
                                key={expense.id}
                                expense={expense}
                            />

                        ))}


                        {expenses.length === 0 && (

                            <div className="empty-mini">
                                No expenses yet.
                            </div>

                        )}

                    </div>

                </section>

            </div>


            {/* =========================
                BALANCES
            ========================= */}

            <div className="balance-preview">

                <div>

                    <p className="eyebrow">
                        GROUP BALANCES
                    </p>

                    <h2>
                        See who owes whom
                    </h2>

                    <p>
                        View the calculated balances and
                        simplified settlements.
                    </p>

                </div>


                <button
                    className="secondary-button"
                    onClick={onBalances}
                >
                    View balances →
                </button>

            </div>


            {/* =========================
                DELETE MEMBER MODAL
            ========================= */}

            {memberToDelete && (

                <div
                    className="delete-modal-overlay"
                    onClick={closeDeleteMember}
                >

                    <div
                        className="delete-modal"
                        onClick={(event) =>
                            event.stopPropagation()
                        }
                    >

                        <div className="delete-modal-icon">
                            <Trash2 size={22} />
                        </div>


                        <h2>
                            Delete member?
                        </h2>


                        <p>
                            Are you sure you want to remove{" "}
                            <strong>
                                {memberToDelete.name}
                            </strong>{" "}
                            from this group?
                        </p>


                        {deleteError && (

                            <div className="form-error">
                                {deleteError}
                            </div>

                        )}


                        <div className="delete-modal-actions">

                            <button
                                type="button"
                                className="secondary-button"
                                onClick={closeDeleteMember}
                                disabled={deletingMember}
                            >
                                Cancel
                            </button>


                            <button
                                type="button"
                                className="danger-button"
                                onClick={confirmDeleteMember}
                                disabled={deletingMember}
                            >
                                {deletingMember
                                    ? "Deleting..."
                                    : "Delete"}
                            </button>

                        </div>

                    </div>

                </div>

            )}


            {/* =========================
                DELETE GROUP MODAL
            ========================= */}

            {showDeleteGroup && (

                <div
                    className="delete-modal-overlay"
                    onClick={closeDeleteGroup}
                >

                    <div
                        className="delete-modal"
                        onClick={(event) =>
                            event.stopPropagation()
                        }
                    >

                        <div className="delete-modal-icon">
                            <Trash2 size={22} />
                        </div>


                        <h2>
                            Delete group?
                        </h2>


                        <p>
                            Are you sure you want to delete{" "}
                            <strong>
                                {group.name}
                            </strong>
                            ?
                        </p>


                        <p className="modal-warning">
                            This will permanently delete the
                            group and its expenses.
                        </p>


                        {groupDeleteError && (

                            <div className="form-error">
                                {groupDeleteError}
                            </div>

                        )}


                        <div className="delete-modal-actions">

                            <button
                                type="button"
                                className="secondary-button"
                                onClick={closeDeleteGroup}
                                disabled={deletingGroup}
                            >
                                Cancel
                            </button>


                            <button
                                type="button"
                                className="danger-button"
                                onClick={confirmDeleteGroup}
                                disabled={deletingGroup}
                            >
                                {deletingGroup
                                    ? "Deleting..."
                                    : "Delete Group"}
                            </button>

                        </div>

                    </div>

                </div>

            )}

        </div>
    );
}

export default GroupDetails;