import { useEffect, useState } from "react";
import Expenses from "./pages/Expenses";

import {
    Menu,
    RefreshCw
} from "lucide-react";

import Sidebar from "./components/Sidebar";
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import Dashboard from "./pages/Dashboard";
import Groups from "./pages/Groups";
import GroupDetails from "./pages/GroupDetails";
import AddExpense from "./pages/AddExpense";
import Balances from "./pages/Balances";
import Settlements from "./pages/Settlements";

import { api } from "./services/api";

function App() {

    // --------------------------------------------------
    // STATE
    // --------------------------------------------------

    const [activePage, setActivePage] =
        useState("dashboard");

   const [isAuthenticated, setIsAuthenticated] =
    useState(
        localStorage.getItem(
            "splitsmart_authenticated"
        ) === "true"
    );

const [authPage, setAuthPage] =
    useState(
        localStorage.getItem(
            "splitsmart_user"
        )
            ? "login"
            : "signup"
    );
    // --------------------------------------------------
    // LOGIN
    // --------------------------------------------------

   const handleLogin = (credentials) => {

    console.log(
        "Login successful:",
        credentials
    );

    localStorage.setItem(
        "splitsmart_authenticated",
        "true"
    );

    setIsAuthenticated(true);

    setActivePage("dashboard");
};

    // --------------------------------------------------
    // LOGOUT
    // --------------------------------------------------

    const handleLogout = () => {

        console.log("Logging out...");

        // Remove authentication status
        localStorage.removeItem(
            "splitsmart_authenticated"
        );

        // Log the user out
        setIsAuthenticated(false);

        // Always return to Login page
        setAuthPage("login");

        // Reset dashboard state
        setActivePage("dashboard");

        // Clear selected group
        setSelectedGroupId(null);

        console.log("Logged out successfully.");
    };


    // --------------------------------------------------
    // SIGNUP
    // --------------------------------------------------

const handleSignup = (userData) => {

    console.log(
        "Signup successful:",
        userData
    );

    /*
     * Account has already been saved
     * by Signup.jsx.
     *
     * Do NOT log the user in automatically.
     */

    setIsAuthenticated(false);

    /*
     * Send the user to Login.
     */
    setAuthPage("login");
};

    // --------------------------------------------------
    // OTHER STATE
    // --------------------------------------------------

    const [mobileMenu, setMobileMenu] =
        useState(false);

    const [groups, setGroups] =
        useState([]);

    const [groupData, setGroupData] =
        useState({});

    const [selectedGroupId, setSelectedGroupId] =
        useState(null);

    const [loading, setLoading] =
        useState(true);

    const [error, setError] =
        useState("");


    // --------------------------------------------------
    // CREATE GROUP MODAL
    // --------------------------------------------------

    const [showCreateGroup, setShowCreateGroup] =
        useState(false);

    const [newGroupName, setNewGroupName] =
        useState("");


    // --------------------------------------------------
    // LOAD GROUPS
    // --------------------------------------------------

    async function loadGroups() {

        try {

            setLoading(true);

            const data =
                await api.getGroups();

            setGroups(data);

            setError("");

            await loadGroupData(data);

        } catch (err) {

            console.error(
                "BACKEND ERROR:",
                err
            );

            setError(
                err.message ||
                "Unable to connect to the backend."
            );

        } finally {

            setLoading(false);

        }
    }


    // --------------------------------------------------
    // LOAD DATA FOR ALL GROUPS
    // --------------------------------------------------

    async function loadGroupData(
        groupList = groups
    ) {

        const result = {};

        await Promise.all(

            groupList.map(
                async (group) => {

                    try {

                        const [
                            members,
                            expenses,
                            balances,
                            settlements
                        ] = await Promise.all([
                            api.getMembers(group.id),
                            api.getExpenses(group.id),
                            api.getBalances(group.id),
                            api.getSettlements(group.id)
                        ]);

                        result[group.id] = {
                            members,
                            expenses,
                            balances,
                            settlements
                        };

                    } catch (err) {

                        console.error(
                            `Failed to load group ${group.id}`,
                            err
                        );

                        result[group.id] = {
                            members: [],
                            expenses: [],
                            balances: {},
                            settlements: []
                        };
                    }
                }
            )
        );

        setGroupData(result);
    }


    // --------------------------------------------------
    // INITIAL LOAD
    // --------------------------------------------------

    useEffect(() => {

        if (!isAuthenticated) {
            return;
        }

        (async () => {

            await loadGroups();

        })();

    }, [isAuthenticated]);


    // --------------------------------------------------
    // OPEN GROUP
    // --------------------------------------------------

    function openGroup(groupId) {

        setSelectedGroupId(groupId);

        setActivePage("group-details");
    }


    // --------------------------------------------------
    // OPEN ADD EXPENSE
    // --------------------------------------------------

    function openCreateExpense() {

        if (!selectedGroupId) {

            if (groups.length > 0) {

                setSelectedGroupId(
                    groups[0].id
                );

                setActivePage(
                    "add-expense"
                );
            }

            return;
        }

        setActivePage("add-expense");
    }


    // --------------------------------------------------
    // OPEN CREATE GROUP MODAL
    // --------------------------------------------------

    function openCreateGroup() {

        setNewGroupName("");

        setError("");

        setShowCreateGroup(true);
    }


    // --------------------------------------------------
    // CLOSE CREATE GROUP MODAL
    // --------------------------------------------------

    function closeCreateGroup() {

        setNewGroupName("");

        setShowCreateGroup(false);
    }


    // --------------------------------------------------
    // CREATE GROUP
    // --------------------------------------------------

    async function createGroup() {

        const name =
            newGroupName.trim();

        if (!name) {

            setError(
                "Please enter a group name."
            );

            return;
        }

        try {

            setError("");

            console.log(
                "CREATING GROUP:",
                name
            );

            const group =
                await api.createGroup(name);

            console.log(
                "GROUP CREATED:",
                group
            );

            // Close modal
            closeCreateGroup();

            // Refresh groups and group data
            await loadGroups();

            // Select newly created group
            setSelectedGroupId(
                group.id
            );

            // Open group details
            setActivePage(
                "group-details"
            );

        } catch (err) {

            console.error(
                "CREATE GROUP ERROR:",
                err
            );

            setError(
                err.message ||
                "Could not create group."
            );
        }
    }


    // --------------------------------------------------
    // DELETE GROUP
    // --------------------------------------------------

    async function deleteGroup(groupId) {

        console.log(
            "Deleting group:",
            groupId
        );

        try {

            await api.deleteGroup(groupId);

            console.log(
                "Group deleted from database."
            );

            // Remove deleted group from React state
            setGroups((previousGroups) =>
                previousGroups.filter(
                    (group) =>
                        group.id !== groupId
                )
            );

            // Remove deleted group's data
            setGroupData((previousData) => {

                const updatedData = {
                    ...previousData
                };

                delete updatedData[groupId];

                return updatedData;
            });

            // Clear selected group
            setSelectedGroupId(null);

            // Go back to Groups page
            setActivePage("groups");

        } catch (error) {

            console.error(
                "DELETE GROUP ERROR:",
                error
            );

            setError(
                error.message ||
                "Could not delete group."
            );
        }
    }


    // --------------------------------------------------
    // SAVE EXPENSE
    // --------------------------------------------------

    async function saveExpense(expense) {

        try {

            await api.addExpense(
                selectedGroupId,
                expense
            );

            const updated =
                await api.getGroups();

            setGroups(updated);

            await loadGroupData(updated);

            setActivePage(
                "group-details"
            );

        } catch (err) {

            console.error(
                "ADD EXPENSE ERROR:",
                err
            );

            setError(
                err.message ||
                "Could not add expense."
            );
        }
    }


    // --------------------------------------------------
    // DELETE EXPENSE
    // --------------------------------------------------

    async function deleteExpense(
        groupId,
        expenseId
    ) {

        console.log(
            "Deleting expense:",
            groupId,
            expenseId
        );

        try {

            // Delete from backend/database
            await api.deleteExpense(
                groupId,
                expenseId
            );

            // Immediately remove it from React state
            setGroupData((prev) => ({

                ...prev,

                [groupId]: {

                    ...prev[groupId],

                    expenses:
                        (
                            prev[groupId]
                                ?.expenses ||
                            []
                        ).filter(
                            (expense) =>
                                expense.id !==
                                expenseId
                        )
                }
            }));

            console.log(
                "Expense deleted successfully."
            );

        } catch (error) {

            console.error(
                "DELETE EXPENSE ERROR:",
                error
            );

            // Do NOT pretend deletion succeeded
            throw error;
        }
    }


    // --------------------------------------------------
    // MEMBER ADDED / DELETED
    // --------------------------------------------------

    async function handleMemberAdded() {

        try {

            await loadGroupData(groups);

        } catch (err) {

            console.error(
                "MEMBER REFRESH ERROR:",
                err
            );
        }
    }


    // --------------------------------------------------
    // RENDER PAGE
    // --------------------------------------------------

    function renderPage() {

        // ----------------------------------------------
        // EXPENSES
        // ----------------------------------------------

        if (
            activePage === "expenses"
        ) {

            return (
                <Expenses
                    groups={groups}
                    groupData={groupData}
                    selectedGroupId={
                        selectedGroupId
                    }
                    onOpenGroup={openGroup}
                    onDeleteExpense={
                        deleteExpense
                    }
                />
            );
        }


        // ----------------------------------------------
        // LOADING
        // ----------------------------------------------

        if (loading) {

            return (
                <div className="loading-screen">

                    <div className="loader" />

                    <p>
                        Loading SplitSmart...
                    </p>

                </div>
            );
        }


        // ----------------------------------------------
        // DASHBOARD
        // ----------------------------------------------

        if (
            activePage === "dashboard"
        ) {

            return (
                <Dashboard
                    groups={groups}
                    groupData={groupData}
                    onOpenGroup={
                        openGroup
                    }
                />
            );
        }


        // ----------------------------------------------
        // GROUPS
        // ----------------------------------------------

        if (
            activePage === "groups"
        ) {

            return (
                <Groups
                    groups={groups}
                    groupData={groupData}
                    onCreateGroup={
                        openCreateGroup
                    }
                    onOpenGroup={
                        openGroup
                    }
                />
            );
        }


        // ----------------------------------------------
        // GROUP DETAILS
        // ----------------------------------------------

        if (
            activePage === "group-details"
        ) {

            const group =
                groups.find(
                    (item) =>
                        item.id ===
                        selectedGroupId
                );

            return (
                <GroupDetails
                    group={group}
                    data={
                        groupData[
                            selectedGroupId
                        ]
                    }
                    onBack={() =>
                        setActivePage("groups")
                    }
                    onAddExpense={
                        openCreateExpense
                    }
                    onBalances={() =>
                        setActivePage("balances")
                    }
                    onMemberDeleted={
                        handleMemberAdded
                    }
                    onDeleteGroup={
                        deleteGroup
                    }
                />
            );
        }


        // ----------------------------------------------
        // ADD EXPENSE
        // ----------------------------------------------

        if (
            activePage === "add-expense"
        ) {

            const group =
                groups.find(
                    (item) =>
                        item.id ===
                        selectedGroupId
                );

            const data =
                groupData[
                    selectedGroupId
                ];

            return (
                <AddExpense
                    group={group}
                    members={
                        data?.members || []
                    }
                    onBack={() =>
                        setActivePage(
                            "group-details"
                        )
                    }
                    onSuccess={
                        saveExpense
                    }
                />
            );
        }


        // ----------------------------------------------
        // BALANCES
        // ----------------------------------------------

        if (
            activePage === "balances"
        ) {

            return (
                <Balances
                    groups={groups}
                    groupData={groupData}
                />
            );
        }


        // ----------------------------------------------
        // SETTLEMENTS
        // ----------------------------------------------

        if (
            activePage === "settlements"
        ) {

            return (
                <Settlements
                    groups={groups}
                    groupData={groupData}
                />
            );
        }


        return null;
    }


    // --------------------------------------------------
    // AUTHENTICATION
    // --------------------------------------------------

    if (!isAuthenticated) {

        if (authPage === "signup") {

            return (
                <Signup
                    onSignup={
                        handleSignup
                    }
                    onShowLogin={() =>
                        setAuthPage("login")
                    }
                />
            );
        }

        return (
            <Login
                onLogin={
                    handleLogin
                }
                onShowSignup={() =>
                    setAuthPage("signup")
                }
            />
        );
    }


    // --------------------------------------------------
    // APP UI
    // --------------------------------------------------

    return (
        <div className="app-shell">

            {/* SIDEBAR */}

            <Sidebar
                activePage={
                    activePage
                }
                setActivePage={
                    setActivePage
                }
                open={
                    mobileMenu
                }
                setOpen={
                    setMobileMenu
                }
                onAddExpense={
                    openCreateExpense
                }
                onLogout={
                    handleLogout
                }
            />


            {/* MAIN AREA */}

            <main className="main-area">

                {/* TOP BAR */}

                <header className="topbar">

                    <button
                        className="mobile-menu-button"
                        onClick={() =>
                            setMobileMenu(true)
                        }
                    >
                        <Menu size={21} />
                    </button>

                    <div className="topbar-title">
                        SplitSmart
                    </div>

                    <button
                        className="refresh-button"
                        onClick={
                            loadGroups
                        }
                        title="Refresh"
                    >
                        <RefreshCw
                            size={17}
                        />
                    </button>

                </header>


                {/* GLOBAL ERROR */}

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


                {/* PAGE */}

                {renderPage()}


                {/* CREATE GROUP MODAL */}

                {showCreateGroup && (

                    <div
                        className="modal-overlay"
                        onClick={(event) => {

                            if (
                                event.target ===
                                event.currentTarget
                            ) {
                                closeCreateGroup();
                            }

                        }}
                    >

                        <div
                            className="modal-card"
                        >

                            {/* MODAL HEADER */}

                            <div
                                className="modal-header"
                            >

                                <div>

                                    <p
                                        className="eyebrow"
                                    >
                                        CREATE
                                    </p>

                                    <h2>
                                        Create New Group
                                    </h2>

                                </div>

                                <button
                                    type="button"
                                    className="modal-close"
                                    onClick={
                                        closeCreateGroup
                                    }
                                >
                                    ×
                                </button>

                            </div>


                            {/* DESCRIPTION */}

                            <p
                                className="modal-description"
                            >
                                Give your group a name
                                to start tracking
                                shared expenses.
                            </p>


                            {/* GROUP NAME */}

                            <div
                                className="form-group"
                            >

                                <label>
                                    Group Name
                                </label>

                                <input
                                    type="text"
                                    className="form-input"
                                    placeholder="e.g. Goa Trip"
                                    value={
                                        newGroupName
                                    }
                                    onChange={(event) =>
                                        setNewGroupName(
                                            event.target.value
                                        )
                                    }
                                    onKeyDown={(event) => {

                                        if (
                                            event.key ===
                                            "Enter"
                                        ) {

                                            event.preventDefault();

                                            createGroup();
                                        }

                                        if (
                                            event.key ===
                                            "Escape"
                                        ) {

                                            closeCreateGroup();
                                        }

                                    }}
                                    autoFocus
                                />

                            </div>


                            {/* ACTION BUTTONS */}

                            <div
                                className="modal-actions"
                            >

                                <button
                                    type="button"
                                    className="secondary-button"
                                    onClick={
                                        closeCreateGroup
                                    }
                                >
                                    Cancel
                                </button>

                                <button
                                    type="button"
                                    className="primary-button"
                                    onClick={
                                        createGroup
                                    }
                                >
                                    Create Group
                                </button>

                            </div>

                        </div>

                    </div>

                )}

            </main>

        </div>
    );
}

export default App;